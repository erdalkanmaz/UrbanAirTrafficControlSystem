package com.airtraffic.control;

import com.airtraffic.model.Position;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Asynchronous processing service for vehicle updates and rule checking
 * 
 * This service provides non-blocking, parallel processing of vehicle position updates,
 * significantly improving throughput when dealing with thousands of vehicles.
 * 
 * Performance benefits:
 * - Parallel processing of multiple updates
 * - Non-blocking operations
 * - Better resource utilization
 * - Improved scalability
 */
public class AsyncProcessingService {
    
    private final TrafficControlCenter controlCenter;
    private final ExecutorService executorService;
    private volatile boolean isShutdown = false;
    
    /**
     * Create async processing service with default thread pool
     * @param controlCenter Traffic control center instance
     */
    public AsyncProcessingService(TrafficControlCenter controlCenter) {
        this(controlCenter, createDefaultExecutor());
    }
    
    /**
     * Create async processing service with custom executor
     * @param controlCenter Traffic control center instance
     * @param executorService Custom executor service
     */
    public AsyncProcessingService(TrafficControlCenter controlCenter, ExecutorService executorService) {
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
     * Update vehicle position asynchronously
     * @param vehicleId Vehicle ID
     * @param newPosition New position
     * @return CompletableFuture that completes when update is processed
     */
    public CompletableFuture<Void> updateVehiclePositionAsync(String vehicleId, Position newPosition) {
        if (isShutdown) {
            return CompletableFuture.completedFuture(null);
        }
        
        return CompletableFuture.runAsync(() -> {
            try {
                if (vehicleId != null && newPosition != null) {
                    controlCenter.updateVehiclePosition(vehicleId, newPosition);
                }
            } catch (Exception e) {
                // Log error but don't fail the future
                System.err.println("Error processing async vehicle update: " + e.getMessage());
            }
        }, executorService);
    }
    
    /**
     * Process multiple vehicle position updates in parallel
     * @param updates Array of vehicle ID and position pairs
     * @return CompletableFuture that completes when all updates are processed
     */
    public CompletableFuture<Void> processBatchUpdates(VehicleUpdate[] updates) {
        if (isShutdown || updates == null || updates.length == 0) {
            return CompletableFuture.completedFuture(null);
        }
        
        CompletableFuture<?>[] futures = new CompletableFuture[updates.length];
        
        for (int i = 0; i < updates.length; i++) {
            VehicleUpdate update = updates[i];
            if (update != null) {
                futures[i] = updateVehiclePositionAsync(update.getVehicleId(), update.getPosition());
            } else {
                futures[i] = CompletableFuture.completedFuture(null);
            }
        }
        
        return CompletableFuture.allOf(futures);
    }
    
    /**
     * Shutdown the async processing service
     * Stops accepting new tasks and waits for existing tasks to complete
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
                Thread thread = new Thread(r, "AsyncProcessing-" + threadNumber.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
    }
    
    /**
     * Inner class for vehicle update data
     */
    public static class VehicleUpdate {
        private final String vehicleId;
        private final Position position;
        
        public VehicleUpdate(String vehicleId, Position position) {
            this.vehicleId = vehicleId;
            this.position = position;
        }
        
        public String getVehicleId() {
            return vehicleId;
        }
        
        public Position getPosition() {
            return position;
        }
    }
}

