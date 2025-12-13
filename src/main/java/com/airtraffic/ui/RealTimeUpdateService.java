package com.airtraffic.ui;

import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Real-time update service for UI components
 * 
 * This service periodically updates UI components (VehicleListView, SystemStatusPanel, MapVisualization)
 * to reflect the current state of the traffic control system in real-time.
 * 
 * Features:
 * - Configurable update interval (default: 1 second)
 * - Thread-safe UI updates using Platform.runLater()
 * - Graceful start/stop
 * - Automatic cleanup on shutdown
 */
public class RealTimeUpdateService {
    
    private final VehicleListView vehicleListView;
    private final SystemStatusPanel systemStatusPanel;
    private final MapVisualization mapVisualization;
    
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> updateTask;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private long updateIntervalMs = 1000; // Default: 1 second
    
    /**
     * Create real-time update service
     * @param vehicleListView Vehicle list view to update
     * @param systemStatusPanel System status panel to update
     * @param mapVisualization Map visualization to update
     */
    public RealTimeUpdateService(VehicleListView vehicleListView, 
                                 SystemStatusPanel systemStatusPanel,
                                 MapVisualization mapVisualization) {
        if (vehicleListView == null) {
            throw new IllegalArgumentException("VehicleListView cannot be null");
        }
        if (systemStatusPanel == null) {
            throw new IllegalArgumentException("SystemStatusPanel cannot be null");
        }
        if (mapVisualization == null) {
            throw new IllegalArgumentException("MapVisualization cannot be null");
        }
        
        this.vehicleListView = vehicleListView;
        this.systemStatusPanel = systemStatusPanel;
        this.mapVisualization = mapVisualization;
    }
    
    /**
     * Start real-time updates with default interval (1 second)
     */
    public void start() {
        start(updateIntervalMs);
    }
    
    /**
     * Start real-time updates with custom interval
     * @param intervalMs Update interval in milliseconds
     */
    public void start(long intervalMs) {
        if (intervalMs <= 0) {
            throw new IllegalArgumentException("Update interval must be positive");
        }
        
        if (isRunning.get()) {
            // Already running, stop first
            stop();
        }
        
        this.updateIntervalMs = intervalMs;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "RealTimeUpdateService");
            thread.setDaemon(true);
            return thread;
        });
        
        isRunning.set(true);
        
        // Schedule periodic updates
        updateTask = scheduler.scheduleAtFixedRate(
            this::performUpdate,
            0,
            intervalMs,
            TimeUnit.MILLISECONDS
        );
    }
    
    /**
     * Stop real-time updates
     */
    public void stop() {
        if (!isRunning.get()) {
            return;
        }
        
        isRunning.set(false);
        
        if (updateTask != null) {
            updateTask.cancel(false);
            updateTask = null;
        }
        
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            scheduler = null;
        }
    }
    
    /**
     * Check if service is running
     * @return true if service is running
     */
    public boolean isRunning() {
        return isRunning.get();
    }
    
    /**
     * Get current update interval
     * @return Update interval in milliseconds
     */
    public long getUpdateInterval() {
        return updateIntervalMs;
    }
    
    /**
     * Perform a single update cycle
     * This method is called periodically by the scheduler
     */
    private void performUpdate() {
        if (!isRunning.get()) {
            return;
        }
        
        // Update UI components on JavaFX Application Thread
        Platform.runLater(() -> {
            try {
                // Update vehicle list
                if (vehicleListView != null) {
                    vehicleListView.refresh();
                }
                
                // Update system status
                if (systemStatusPanel != null) {
                    systemStatusPanel.refresh();
                }
                
                // Update map visualization
                if (mapVisualization != null) {
                    mapVisualization.render();
                }
            } catch (Exception e) {
                // Log error but don't stop updates
                System.err.println("Error during real-time update: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Manually trigger an update (useful for immediate refresh)
     */
    public void updateNow() {
        if (isRunning.get()) {
            performUpdate();
        }
    }
}

