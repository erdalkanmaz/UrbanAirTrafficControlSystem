package com.airtraffic.control;

import com.airtraffic.model.Position;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Batch processor for efficient bulk processing of vehicle position updates
 * 
 * This service processes multiple vehicle updates in optimized batches,
 * reducing overhead and improving throughput for high-volume scenarios.
 * 
 * Performance benefits:
 * - Reduced per-update overhead
 * - Optimized batch operations
 * - Parallel processing within batches
 * - Better resource utilization
 */
public class BatchProcessor {
    
    private final TrafficControlCenter controlCenter;
    private final ExecutorService executorService;
    private volatile boolean isShutdown = false;
    
    /**
     * Create batch processor with default thread pool
     * @param controlCenter Traffic control center instance
     */
    public BatchProcessor(TrafficControlCenter controlCenter) {
        this(controlCenter, createDefaultExecutor());
    }
    
    /**
     * Create batch processor with custom executor
     * @param controlCenter Traffic control center instance
     * @param executorService Custom executor service
     */
    public BatchProcessor(TrafficControlCenter controlCenter, ExecutorService executorService) {
        if (controlCenter == null) {
            throw new IllegalArgumentException("Control center cannot be null");
        }
        if (executorService == null) {
            throw new IllegalArgumentException("Executor service cannot be null");
        }
        
        this.controlCenter = controlCenter;
        this.executorService = executorService;
    }
    
    /**
     * Process a batch of vehicle position updates
     * @param updates Map of vehicle ID to new position
     * @return CompletableFuture with batch processing result
     */
    public CompletableFuture<BatchResult> processBatch(Map<String, Position> updates) {
        if (isShutdown || updates == null || updates.isEmpty()) {
            return CompletableFuture.completedFuture(new BatchResult(0, 0));
        }
        
        return CompletableFuture.supplyAsync(() -> {
            AtomicLong processedCount = new AtomicLong(0);
            AtomicLong errorCount = new AtomicLong(0);
            
            // Process updates in parallel
            updates.entrySet().parallelStream().forEach(entry -> {
                try {
                    String vehicleId = entry.getKey();
                    Position position = entry.getValue();
                    
                    if (vehicleId != null && position != null) {
                        controlCenter.updateVehiclePosition(vehicleId, position);
                        processedCount.incrementAndGet();
                    } else {
                        errorCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    // Log error but continue processing other updates
                    System.err.println("Error processing batch update for vehicle " + 
                        entry.getKey() + ": " + e.getMessage());
                }
            });
            
            return new BatchResult(processedCount.get(), errorCount.get());
        }, executorService);
    }
    
    /**
     * Shutdown the batch processor
     * Stops accepting new batches and waits for existing batches to complete
     */
    public void shutdown() {
        isShutdown = true;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Create default executor service with appropriate thread pool size
     * @return ExecutorService instance
     */
    private static ExecutorService createDefaultExecutor() {
        int corePoolSize = Math.max(2, Runtime.getRuntime().availableProcessors());
        int maxPoolSize = corePoolSize * 2;
        
        return Executors.newFixedThreadPool(maxPoolSize, new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "BatchProcessor-" + threadNumber.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
    }
    
    /**
     * Result of batch processing operation
     */
    public static class BatchResult {
        private final long processedCount;
        private final long errorCount;
        
        public BatchResult(long processedCount, long errorCount) {
            this.processedCount = processedCount;
            this.errorCount = errorCount;
        }
        
        public long getProcessedCount() {
            return processedCount;
        }
        
        public long getErrorCount() {
            return errorCount;
        }
        
        public long getTotalCount() {
            return processedCount + errorCount;
        }
        
        public double getSuccessRate() {
            long total = getTotalCount();
            return total > 0 ? (double) processedCount / total : 0.0;
        }
    }
}

