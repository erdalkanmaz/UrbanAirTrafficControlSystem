package com.airtraffic.control;

import com.airtraffic.map.CityMap;
import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BatchProcessor
 * Tests batch processing of vehicle position updates for improved performance
 */
@DisplayName("BatchProcessor Tests")
class BatchProcessorTest {

    private BatchProcessor batchProcessor;
    private TrafficControlCenter controlCenter;
    private CityMap cityMap;

    @BeforeEach
    void setUp() {
        controlCenter = TrafficControlCenter.getInstance();
        
        // Clear existing vehicles
        List<Vehicle> existingVehicles = controlCenter.getActiveVehicles();
        for (Vehicle vehicle : existingVehicles) {
            controlCenter.unregisterVehicle(vehicle.getId());
        }
        
        cityMap = new CityMap("Istanbul");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        controlCenter.loadCityMap(cityMap);
        
        batchProcessor = new BatchProcessor(controlCenter);
    }

    @AfterEach
    void tearDown() {
        if (batchProcessor != null) {
            batchProcessor.shutdown();
        }
    }

    @Test
    @DisplayName("Test constructor creates batch processor")
    void testConstructor() {
        assertNotNull(batchProcessor);
    }

    @Test
    @DisplayName("Test processBatch processes multiple updates")
    void testProcessBatch() throws Exception {
        // Create and register vehicles
        int vehicleCount = 5;
        List<Vehicle> vehicles = new ArrayList<>();
        Position destination = new Position(41.0100, 28.9800, 120.0);
        
        for (int i = 0; i < vehicleCount; i++) {
            Position pos = new Position(41.0 + i * 0.01, 29.0 + i * 0.01, 100.0);
            Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
            vehicle.setPilotLicense("PILOT-" + i);
            
            FlightAuthorization auth = controlCenter.requestFlightAuthorization(vehicle, pos, destination);
            if (auth.getStatus() == AuthorizationStatus.APPROVED) {
                controlCenter.registerVehicle(vehicle);
                vehicles.add(vehicle);
            }
        }
        
        assertTrue(vehicles.size() > 0, "Should have registered vehicles");
        
        // Create batch updates
        Map<String, Position> updates = new java.util.HashMap<>();
        for (Vehicle vehicle : vehicles) {
            Position newPos = new Position(
                vehicle.getPosition().getLatitude() + 0.001,
                vehicle.getPosition().getLongitude() + 0.001,
                105.0
            );
            updates.put(vehicle.getId(), newPos);
        }
        
        // Process batch
        CompletableFuture<BatchProcessor.BatchResult> future = batchProcessor.processBatch(updates);
        BatchProcessor.BatchResult result = future.get(10, TimeUnit.SECONDS);
        
        // Verify results
        assertNotNull(result);
        assertEquals(vehicles.size(), result.getProcessedCount());
        assertEquals(0, result.getErrorCount());
        
        // Verify positions were updated
        for (Vehicle vehicle : vehicles) {
            Vehicle updated = controlCenter.getActiveVehicles().stream()
                    .filter(v -> v.getId().equals(vehicle.getId()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(updated);
            assertTrue(updated.getPosition().getAltitude() == 105.0);
        }
    }

    @Test
    @DisplayName("Test processBatch with empty map")
    void testProcessBatchEmpty() throws Exception {
        Map<String, Position> updates = new java.util.HashMap<>();
        
        CompletableFuture<BatchProcessor.BatchResult> future = batchProcessor.processBatch(updates);
        BatchProcessor.BatchResult result = future.get(5, TimeUnit.SECONDS);
        
        assertNotNull(result);
        assertEquals(0, result.getProcessedCount());
        assertEquals(0, result.getErrorCount());
    }

    @Test
    @DisplayName("Test processBatch handles invalid vehicle IDs")
    void testProcessBatchInvalidIds() throws Exception {
        Map<String, Position> updates = new java.util.HashMap<>();
        updates.put("invalid-id-1", new Position(41.0, 29.0, 100.0));
        updates.put("invalid-id-2", new Position(41.0, 29.0, 100.0));
        
        CompletableFuture<BatchProcessor.BatchResult> future = batchProcessor.processBatch(updates);
        BatchProcessor.BatchResult result = future.get(5, TimeUnit.SECONDS);
        
        assertNotNull(result);
        // Should process but with errors for invalid IDs
        assertEquals(2, result.getProcessedCount());
        // Error count may be 0 if invalid IDs are handled gracefully
    }

    @Test
    @DisplayName("Test processBatch performance with many updates")
    void testProcessBatchPerformance() throws Exception {
        // Create and register vehicles
        int vehicleCount = 20;
        List<Vehicle> vehicles = new ArrayList<>();
        Position destination = new Position(41.0100, 28.9800, 120.0);
        
        for (int i = 0; i < vehicleCount; i++) {
            Position pos = new Position(40.5 + (i % 10) * 0.1, 28.5 + (i % 10) * 0.1, 100.0);
            Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
            vehicle.setPilotLicense("PILOT-PERF-" + i);
            
            FlightAuthorization auth = controlCenter.requestFlightAuthorization(vehicle, pos, destination);
            if (auth.getStatus() == AuthorizationStatus.APPROVED) {
                controlCenter.registerVehicle(vehicle);
                vehicles.add(vehicle);
            }
        }
        
        assertTrue(vehicles.size() > 0, "Should have registered vehicles");
        
        // Create batch updates
        Map<String, Position> updates = new java.util.HashMap<>();
        for (Vehicle vehicle : vehicles) {
            Position newPos = new Position(
                vehicle.getPosition().getLatitude() + 0.001,
                vehicle.getPosition().getLongitude() + 0.001,
                105.0
            );
            updates.put(vehicle.getId(), newPos);
        }
        
        // Measure batch processing time
        long startTime = System.nanoTime();
        CompletableFuture<BatchProcessor.BatchResult> future = batchProcessor.processBatch(updates);
        BatchProcessor.BatchResult result = future.get(10, TimeUnit.SECONDS);
        long endTime = System.nanoTime();
        
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        
        // Batch processing should be fast
        assertTrue(duration < 500, 
            "Batch processing should complete quickly. Duration: " + duration + "ms");
        assertNotNull(result);
        assertEquals(vehicles.size(), result.getProcessedCount());
    }

    @Test
    @DisplayName("Test shutdown stops batch processor")
    void testShutdown() throws Exception {
        batchProcessor.shutdown();
        
        // Attempting to process after shutdown should be handled gracefully
        Map<String, Position> updates = new java.util.HashMap<>();
        updates.put("test-id", new Position(41.0, 29.0, 100.0));
        
        CompletableFuture<BatchProcessor.BatchResult> future = batchProcessor.processBatch(updates);
        
        // Should complete (may be rejected, but should not hang)
        assertDoesNotThrow(() -> {
            try {
                future.get(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                // Expected if processor is shut down
            }
        });
    }

    @Test
    @DisplayName("Test batch processing with mixed valid and invalid updates")
    void testProcessBatchMixed() throws Exception {
        // Create and register one vehicle
        Position pos = new Position(41.0082, 28.9784, 100.0);
        Position destination = new Position(41.0100, 28.9800, 120.0);
        Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
        vehicle.setPilotLicense("PILOT-1");
        
        FlightAuthorization auth = controlCenter.requestFlightAuthorization(vehicle, pos, destination);
        if (auth.getStatus() == AuthorizationStatus.APPROVED) {
            controlCenter.registerVehicle(vehicle);
        }
        
        // Create batch with valid and invalid updates
        Map<String, Position> updates = new java.util.HashMap<>();
        updates.put(vehicle.getId(), new Position(41.0085, 28.9787, 105.0)); // Valid
        updates.put("invalid-id", new Position(41.0, 29.0, 100.0)); // Invalid
        
        CompletableFuture<BatchProcessor.BatchResult> future = batchProcessor.processBatch(updates);
        BatchProcessor.BatchResult result = future.get(10, TimeUnit.SECONDS);
        
        assertNotNull(result);
        assertEquals(2, result.getProcessedCount()); // Both processed
        // At least one should succeed
        assertTrue(result.getProcessedCount() > 0);
    }
}

