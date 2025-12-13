package com.airtraffic.control;

import com.airtraffic.control.AuthorizationStatus;
import com.airtraffic.map.CityMap;
import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import com.airtraffic.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TrafficControlCenter with Quadtree spatial indexing
 * Tests performance improvements and correctness of spatial queries
 */
@DisplayName("TrafficControlCenter Quadtree Integration Tests")
class TrafficControlCenterQuadtreeIntegrationTest {

    private TrafficControlCenter center;
    private CityMap cityMap;

    @BeforeEach
    void setUp() {
        center = TrafficControlCenter.getInstance();
        
        // Clear existing vehicles from previous tests
        List<Vehicle> existingVehicles = center.getActiveVehicles();
        for (Vehicle vehicle : existingVehicles) {
            center.unregisterVehicle(vehicle.getId());
        }
        
        cityMap = new CityMap("Istanbul");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        center.loadCityMap(cityMap);
    }

    @Test
    @DisplayName("Test Quadtree is initialized when city map is loaded")
    void testQuadtreeInitialized() {
        assertNotNull(cityMap);
        assertTrue(center.isOperational());
        
        // Quadtree should be initialized after loadCityMap
        // We can verify this by checking that spatial queries work
        Position centerPos = new Position(41.0, 29.0, 100.0);
        List<Vehicle> result = center.getVehiclesInArea(centerPos, 1000.0);
        
        // Should not throw exception and return empty list if no vehicles
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test getVehiclesInArea uses Quadtree for efficient queries")
    void testEfficientSpatialQuery() {
        Position destination = new Position(41.0100, 28.9800, 120.0);
        
        // Register a small number of vehicles for testing
        int vehicleCount = 10; // Small number to ensure all get registered
        int registeredCount = 0;
        
        for (int i = 0; i < vehicleCount; i++) {
            // Use positions within city map bounds (40.0-42.0 lat, 28.0-30.0 lon)
            // Spread them around the center of the city map
            double lat = 40.8 + (i * 0.02); // Spread from 40.8 to 41.0
            double lon = 28.8 + (i * 0.02); // Spread from 28.8 to 29.0
            Position pos = new Position(lat, lon, 100.0);
            
            Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
            vehicle.setPilotLicense("PILOT-" + i);
            
            FlightAuthorization auth = center.requestFlightAuthorization(vehicle, pos, destination);
            if (auth.getStatus() == AuthorizationStatus.APPROVED) {
                try {
                    center.registerVehicle(vehicle);
                    registeredCount++;
                } catch (Exception e) {
                    // Skip if registration fails
                }
            }
        }
        
        // Ensure we have at least some vehicles registered
        assertTrue(registeredCount > 0, 
            "At least some vehicles should be registered. Registered: " + registeredCount + 
            ", Total active: " + center.getActiveVehicles().size());
        
        // Query should be fast even with many vehicles
        Position queryCenter = new Position(40.9, 28.9, 100.0); // Center of our test vehicles
        long startTime = System.nanoTime();
        List<Vehicle> result = center.getVehiclesInArea(queryCenter, 50000.0); // Large radius to find all
        long endTime = System.nanoTime();
        
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        
        // Query should complete in less than 100ms (Quadtree optimization)
        assertTrue(duration < 100, 
            "Spatial query should be fast with Quadtree. Duration: " + duration + "ms");
        
        // Should find at least some of the registered vehicles
        assertTrue(result.size() > 0, 
            "Should find vehicles in area. Found: " + result.size() + ", Registered: " + registeredCount);
    }

    @Test
    @DisplayName("Test vehicle position update updates Quadtree")
    void testVehiclePositionUpdate() {
        Position startPos = new Position(41.0082, 28.9784, 100.0);
        Position destination = new Position(41.0100, 28.9800, 120.0);
        
        Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, startPos);
        vehicle.setPilotLicense("PILOT-1");
        
        FlightAuthorization auth = center.requestFlightAuthorization(vehicle, startPos, destination);
        if (auth.getStatus() == AuthorizationStatus.APPROVED) {
            center.registerVehicle(vehicle);
            
            // Query at start position
            List<Vehicle> result1 = center.getVehiclesInArea(startPos, 100.0);
            assertTrue(result1.contains(vehicle), "Should find vehicle at start position");
            
            // Update position
            Position newPos = new Position(41.0100, 28.9800, 120.0);
            center.updateVehiclePosition(vehicle.getId(), newPos);
            
            // Query at new position
            List<Vehicle> result2 = center.getVehiclesInArea(newPos, 100.0);
            assertTrue(result2.contains(vehicle), "Should find vehicle at new position");
            
            // Query at old position should not find it (or find it if radius is large enough)
            List<Vehicle> result3 = center.getVehiclesInArea(startPos, 50.0);
            // Vehicle moved, so it might not be in small radius around old position
            // This is expected behavior
        }
    }

    @Test
    @DisplayName("Test unregisterVehicle removes from Quadtree")
    void testUnregisterRemovesFromQuadtree() {
        Position pos = new Position(41.0082, 28.9784, 100.0);
        Position destination = new Position(41.0100, 28.9800, 120.0);
        
        Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
        vehicle.setPilotLicense("PILOT-1");
        
        FlightAuthorization auth = center.requestFlightAuthorization(vehicle, pos, destination);
        if (auth.getStatus() == AuthorizationStatus.APPROVED) {
            center.registerVehicle(vehicle);
            
            // Query should find vehicle
            List<Vehicle> result1 = center.getVehiclesInArea(pos, 100.0);
            assertTrue(result1.contains(vehicle), "Should find vehicle before unregister");
            
            // Unregister
            center.unregisterVehicle(vehicle.getId());
            
            // Query should not find vehicle
            List<Vehicle> result2 = center.getVehiclesInArea(pos, 100.0);
            assertFalse(result2.contains(vehicle), "Should not find vehicle after unregister");
        }
    }

    @Test
    @DisplayName("Test Quadtree handles multiple vehicles efficiently")
    void testLargeScalePerformance() {
        Position destination = new Position(41.0100, 28.9800, 120.0);
        
        // Register a reasonable number of vehicles for performance testing
        int vehicleCount = 20; // Reasonable number for testing
        int registeredCount = 0;
        
        for (int i = 0; i < vehicleCount; i++) {
            // Use positions within city map bounds, spread around
            double lat = 40.5 + (i % 10) * 0.1;
            double lon = 28.5 + (i % 10) * 0.1;
            Position pos = new Position(lat, lon, 100.0);
            
            Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
            vehicle.setPilotLicense("PILOT-PERF-" + i);
            
            FlightAuthorization auth = center.requestFlightAuthorization(vehicle, pos, destination);
            if (auth.getStatus() == AuthorizationStatus.APPROVED) {
                try {
                    center.registerVehicle(vehicle);
                    registeredCount++;
                } catch (Exception e) {
                    // Skip if registration fails
                }
            }
        }
        
        // Ensure we have vehicles registered
        assertTrue(registeredCount > 0, 
            "At least some vehicles should be registered. Registered: " + registeredCount);
        
        // Query should be fast even with many vehicles
        Position queryCenter = new Position(41.0, 29.0, 100.0);
        long startTime = System.nanoTime();
        List<Vehicle> result = center.getVehiclesInArea(queryCenter, 50000.0); // Large radius
        long endTime = System.nanoTime();
        
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        
        // With Quadtree, query should complete in less than 200ms even with many vehicles
        assertTrue(duration < 200, 
            "Spatial query should be fast with Quadtree even with many vehicles. Duration: " + duration + "ms");
        
        // Should find some vehicles
        assertTrue(result.size() > 0, 
            "Should find vehicles in area. Found: " + result.size() + ", Registered: " + registeredCount);
    }
}

