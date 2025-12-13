package com.airtraffic.spatial;

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
 * Unit tests for Quadtree spatial indexing
 * Tests spatial indexing for efficient vehicle location queries
 */
@DisplayName("Quadtree Tests")
class QuadtreeTest {

    private Quadtree quadtree;
    private static final double MIN_LAT = 40.0;
    private static final double MAX_LAT = 42.0;
    private static final double MIN_LON = 28.0;
    private static final double MAX_LON = 30.0;

    @BeforeEach
    void setUp() {
        quadtree = new Quadtree(MIN_LAT, MAX_LAT, MIN_LON, MAX_LON);
    }

    @Test
    @DisplayName("Test constructor creates quadtree with correct bounds")
    void testConstructor() {
        assertNotNull(quadtree);
        assertEquals(0, quadtree.size());
    }

    @Test
    @DisplayName("Test insert vehicle into quadtree")
    void testInsert() {
        Vehicle vehicle = createVehicle("vehicle1", 41.0, 29.0, 100.0);
        
        quadtree.insert(vehicle);
        
        assertEquals(1, quadtree.size());
    }

    @Test
    @DisplayName("Test insert multiple vehicles")
    void testInsertMultiple() {
        Vehicle v1 = createVehicle("vehicle1", 41.0, 29.0, 100.0);
        Vehicle v2 = createVehicle("vehicle2", 41.1, 29.1, 100.0);
        Vehicle v3 = createVehicle("vehicle3", 41.2, 29.2, 100.0);
        
        quadtree.insert(v1);
        quadtree.insert(v2);
        quadtree.insert(v3);
        
        assertEquals(3, quadtree.size());
    }

    @Test
    @DisplayName("Test insert null vehicle throws exception")
    void testInsertNull() {
        assertThrows(IllegalArgumentException.class, () -> quadtree.insert(null));
    }

    @Test
    @DisplayName("Test insert vehicle with null position throws exception")
    void testInsertNullPosition() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("vehicle1");
        vehicle.setPosition(null);
        
        assertThrows(IllegalArgumentException.class, () -> quadtree.insert(vehicle));
    }

    @Test
    @DisplayName("Test query returns vehicles in area")
    void testQuery() {
        Vehicle v1 = createVehicle("vehicle1", 41.0, 29.0, 100.0);
        Vehicle v2 = createVehicle("vehicle2", 41.1, 29.1, 100.0);
        Vehicle v3 = createVehicle("vehicle3", 41.5, 29.5, 100.0); // Outside query area
        
        quadtree.insert(v1);
        quadtree.insert(v2);
        quadtree.insert(v3);
        
        Position center = new Position(41.05, 29.05, 100.0);
        double radius = 10000.0; // 10km radius
        
        List<Vehicle> result = quadtree.query(center, radius);
        
        assertEquals(2, result.size());
        assertTrue(result.contains(v1));
        assertTrue(result.contains(v2));
        assertFalse(result.contains(v3));
    }

    @Test
    @DisplayName("Test query with no vehicles returns empty list")
    void testQueryEmpty() {
        Position center = new Position(41.0, 29.0, 100.0);
        double radius = 1000.0;
        
        List<Vehicle> result = quadtree.query(center, radius);
        
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test query with large radius returns all vehicles")
    void testQueryLargeRadius() {
        Vehicle v1 = createVehicle("vehicle1", 41.0, 29.0, 100.0);
        Vehicle v2 = createVehicle("vehicle2", 41.1, 29.1, 100.0);
        Vehicle v3 = createVehicle("vehicle3", 41.5, 29.5, 100.0);
        
        quadtree.insert(v1);
        quadtree.insert(v2);
        quadtree.insert(v3);
        
        Position center = new Position(41.0, 29.0, 100.0);
        double radius = 100000.0; // 100km radius
        
        List<Vehicle> result = quadtree.query(center, radius);
        
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test remove vehicle from quadtree")
    void testRemove() {
        Vehicle vehicle = createVehicle("vehicle1", 41.0, 29.0, 100.0);
        
        quadtree.insert(vehicle);
        assertEquals(1, quadtree.size());
        
        boolean removed = quadtree.remove(vehicle);
        
        assertTrue(removed);
        assertEquals(0, quadtree.size());
    }

    @Test
    @DisplayName("Test remove non-existent vehicle returns false")
    void testRemoveNonExistent() {
        Vehicle vehicle = createVehicle("vehicle1", 41.0, 29.0, 100.0);
        
        boolean removed = quadtree.remove(vehicle);
        
        assertFalse(removed);
    }

    @Test
    @DisplayName("Test remove null vehicle throws exception")
    void testRemoveNull() {
        assertThrows(IllegalArgumentException.class, () -> quadtree.remove(null));
    }

    @Test
    @DisplayName("Test update vehicle position")
    void testUpdate() {
        Vehicle vehicle = createVehicle("vehicle1", 41.0, 29.0, 100.0);
        
        quadtree.insert(vehicle);
        
        Position newPosition = new Position(41.5, 29.5, 150.0);
        vehicle.updatePosition(newPosition);
        
        quadtree.update(vehicle);
        
        Position center = new Position(41.5, 29.5, 100.0);
        List<Vehicle> result = quadtree.query(center, 1000.0);
        
        assertEquals(1, result.size());
        assertEquals(vehicle, result.get(0));
    }

    @Test
    @DisplayName("Test quadtree splits when capacity exceeded")
    void testSplit() {
        // Insert more vehicles than capacity (default capacity is 10)
        for (int i = 0; i < 15; i++) {
            Vehicle vehicle = createVehicle("vehicle" + i, 
                41.0 + (i * 0.01), 29.0 + (i * 0.01), 100.0);
            quadtree.insert(vehicle);
        }
        
        assertEquals(15, quadtree.size());
        
        // Query should still work correctly after split
        Position center = new Position(41.05, 29.05, 100.0);
        List<Vehicle> result = quadtree.query(center, 5000.0);
        
        assertTrue(result.size() > 0);
    }

    @Test
    @DisplayName("Test clear removes all vehicles")
    void testClear() {
        Vehicle v1 = createVehicle("vehicle1", 41.0, 29.0, 100.0);
        Vehicle v2 = createVehicle("vehicle2", 41.1, 29.1, 100.0);
        
        quadtree.insert(v1);
        quadtree.insert(v2);
        assertEquals(2, quadtree.size());
        
        quadtree.clear();
        
        assertEquals(0, quadtree.size());
    }

    @Test
    @DisplayName("Test size returns correct count")
    void testSize() {
        assertEquals(0, quadtree.size());
        
        Vehicle v1 = createVehicle("vehicle1", 41.0, 29.0, 100.0);
        Vehicle v2 = createVehicle("vehicle2", 41.1, 29.1, 100.0);
        
        quadtree.insert(v1);
        assertEquals(1, quadtree.size());
        
        quadtree.insert(v2);
        assertEquals(2, quadtree.size());
        
        quadtree.remove(v1);
        assertEquals(1, quadtree.size());
    }

    @Test
    @DisplayName("Test query with vehicles at boundary")
    void testQueryBoundary() {
        Vehicle v1 = createVehicle("vehicle1", MIN_LAT, MIN_LON, 100.0);
        Vehicle v2 = createVehicle("vehicle2", MAX_LAT, MAX_LON, 100.0);
        
        quadtree.insert(v1);
        quadtree.insert(v2);
        
        Position center = new Position((MIN_LAT + MAX_LAT) / 2, (MIN_LON + MAX_LON) / 2, 100.0);
        double radius = 200000.0; // Large radius to cover entire area
        
        List<Vehicle> result = quadtree.query(center, radius);
        
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test query performance with many vehicles")
    void testQueryPerformance() {
        // Insert 1000 vehicles
        for (int i = 0; i < 1000; i++) {
            Vehicle vehicle = createVehicle("vehicle" + i,
                41.0 + (i % 100) * 0.01, 29.0 + (i % 100) * 0.01, 100.0);
            quadtree.insert(vehicle);
        }
        
        assertEquals(1000, quadtree.size());
        
        // Query should be fast even with many vehicles
        Position center = new Position(41.5, 29.5, 100.0);
        long startTime = System.nanoTime();
        List<Vehicle> result = quadtree.query(center, 5000.0);
        long endTime = System.nanoTime();
        
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        assertTrue(duration < 100, "Query should complete in less than 100ms");
        assertTrue(result.size() > 0);
    }

    // Helper method to create test vehicles
    private Vehicle createVehicle(String id, double lat, double lon, double altitude) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setType(VehicleType.PASSENGER);
        vehicle.setStatus(VehicleStatus.IN_FLIGHT);
        vehicle.setPosition(new Position(lat, lon, altitude));
        return vehicle;
    }
}

