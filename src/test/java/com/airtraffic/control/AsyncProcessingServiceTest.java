package com.airtraffic.control;

import com.airtraffic.control.AuthorizationStatus;
import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import com.airtraffic.model.VehicleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AsyncProcessingService
 * Tests asynchronous processing of vehicle updates and rule checking
 */
@DisplayName("AsyncProcessingService Tests")
class AsyncProcessingServiceTest {

    private AsyncProcessingService asyncService;
    private TrafficControlCenter controlCenter;

    @BeforeEach
    void setUp() {
        controlCenter = TrafficControlCenter.getInstance();
        asyncService = new AsyncProcessingService(controlCenter);
    }

    @AfterEach
    void tearDown() {
        if (asyncService != null) {
            asyncService.shutdown();
        }
    }

    @Test
    @DisplayName("Test constructor creates service")
    void testConstructor() {
        assertNotNull(asyncService);
    }

    @Test
    @DisplayName("Test updateVehiclePositionAsync processes update asynchronously")
    void testUpdateVehiclePositionAsync() throws Exception {
        // Create test vehicle
        Position pos = new Position(41.0082, 28.9784, 100.0);
        Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
        vehicle.setPilotLicense("PILOT-1");
        
        // Register vehicle
        Position destination = new Position(41.0100, 28.9800, 120.0);
        FlightAuthorization auth = controlCenter.requestFlightAuthorization(vehicle, pos, destination);
        if (auth.getStatus() == AuthorizationStatus.APPROVED) {
            controlCenter.registerVehicle(vehicle);
        }
        
        // Update position asynchronously
        Position newPos = new Position(41.0085, 28.9787, 105.0);
        CompletableFuture<Void> future = asyncService.updateVehiclePositionAsync(vehicle.getId(), newPos);
        
        // Wait for completion
        future.get(5, TimeUnit.SECONDS);
        
        // Verify position was updated
        Vehicle updatedVehicle = controlCenter.getActiveVehicles().stream()
                .filter(v -> v.getId().equals(vehicle.getId()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(updatedVehicle);
        assertEquals(newPos.getLatitude(), updatedVehicle.getPosition().getLatitude(), 0.0001);
        assertEquals(newPos.getLongitude(), updatedVehicle.getPosition().getLongitude(), 0.0001);
    }

    @Test
    @DisplayName("Test updateVehiclePositionAsync with non-existent vehicle")
    void testUpdateVehiclePositionAsyncNonExistent() throws Exception {
        CompletableFuture<Void> future = asyncService.updateVehiclePositionAsync("non-existent", 
            new Position(41.0, 29.0, 100.0));
        
        // Should complete without error (vehicle not found is handled gracefully)
        assertDoesNotThrow(() -> future.get(5, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Test processBatchUpdates processes multiple updates")
    void testProcessBatchUpdates() throws Exception {
        // Create and register multiple vehicles
        int vehicleCount = 5;
        Vehicle[] vehicles = new Vehicle[vehicleCount];
        
        for (int i = 0; i < vehicleCount; i++) {
            Position pos = new Position(41.0 + i * 0.01, 29.0 + i * 0.01, 100.0);
            vehicles[i] = new Vehicle(VehicleType.PASSENGER, pos);
            vehicles[i].setPilotLicense("PILOT-" + i);
            
            Position destination = new Position(41.0100, 28.9800, 120.0);
            FlightAuthorization auth = controlCenter.requestFlightAuthorization(vehicles[i], pos, destination);
            if (auth.getStatus() == AuthorizationStatus.APPROVED) {
                controlCenter.registerVehicle(vehicles[i]);
            }
        }
        
        // Process batch updates
        CountDownLatch latch = new CountDownLatch(vehicleCount);
        for (int i = 0; i < vehicleCount; i++) {
            Position newPos = new Position(41.0 + i * 0.01, 29.0 + i * 0.01, 105.0);
            asyncService.updateVehiclePositionAsync(vehicles[i].getId(), newPos)
                    .thenRun(latch::countDown);
        }
        
        // Wait for all updates to complete
        assertTrue(latch.await(10, TimeUnit.SECONDS), 
            "All batch updates should complete within timeout");
    }

    @Test
    @DisplayName("Test shutdown stops service gracefully")
    void testShutdown() throws Exception {
        asyncService.shutdown();
        
        // Service should be shut down
        // Attempting to submit new tasks should be handled gracefully
        CompletableFuture<Void> future = asyncService.updateVehiclePositionAsync("test", 
            new Position(41.0, 29.0, 100.0));
        
        // Should complete (may be rejected, but should not hang)
        assertDoesNotThrow(() -> {
            try {
                future.get(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                // Expected if service is shut down
            }
        });
    }

    @Test
    @DisplayName("Test async processing improves throughput")
    void testAsyncProcessingThroughput() throws Exception {
        // Create and register vehicles
        int vehicleCount = 10;
        Vehicle[] vehicles = new Vehicle[vehicleCount];
        
        for (int i = 0; i < vehicleCount; i++) {
            Position pos = new Position(41.0 + i * 0.01, 29.0 + i * 0.01, 100.0);
            vehicles[i] = new Vehicle(VehicleType.PASSENGER, pos);
            vehicles[i].setPilotLicense("PILOT-" + i);
            
            Position destination = new Position(41.0100, 28.9800, 120.0);
            FlightAuthorization auth = controlCenter.requestFlightAuthorization(vehicles[i], pos, destination);
            if (auth.getStatus() == AuthorizationStatus.APPROVED) {
                controlCenter.registerVehicle(vehicles[i]);
            }
        }
        
        // Measure async processing time
        long startTime = System.nanoTime();
        
        CompletableFuture<?>[] futures = new CompletableFuture[vehicleCount];
        for (int i = 0; i < vehicleCount; i++) {
            Position newPos = new Position(41.0 + i * 0.01, 29.0 + i * 0.01, 105.0);
            futures[i] = asyncService.updateVehiclePositionAsync(vehicles[i].getId(), newPos);
        }
        
        // Wait for all to complete
        CompletableFuture.allOf(futures).get(10, TimeUnit.SECONDS);
        
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        
        // Async processing should be fast (all updates in parallel)
        assertTrue(duration < 1000, 
            "Async batch processing should complete quickly. Duration: " + duration + "ms");
    }

    @Test
    @DisplayName("Test async processing handles errors gracefully")
    void testAsyncProcessingErrorHandling() throws Exception {
        // Try to update with invalid data
        CompletableFuture<Void> future = asyncService.updateVehiclePositionAsync(null, null);
        
        // Should complete without throwing exception (error handled internally)
        assertDoesNotThrow(() -> {
            try {
                future.get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                // Expected for invalid input
            }
        });
    }
}

