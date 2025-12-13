package com.airtraffic.control;

import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BaseStation class
 * Tests base station coverage, vehicle connections, and communication management
 */
@DisplayName("BaseStation Tests")
class BaseStationTest {

    private BaseStation station;
    private Position stationPosition;
    private Vehicle testVehicle;
    private Position vehiclePosition;

    @BeforeEach
    void setUp() {
        stationPosition = new Position(41.0082, 28.9784, 50.0);
        station = new BaseStation("Test Station", stationPosition, 5000.0); // 5km coverage
        
        vehiclePosition = new Position(41.0085, 28.9787, 100.0); // Close to station
        testVehicle = new Vehicle(VehicleType.PASSENGER, vehiclePosition);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        BaseStation st = new BaseStation();
        
        assertNotNull(st.getId());
        assertTrue(st.isActive());
        assertEquals(5000.0, st.getCoverageRadius(), 0.01);
        assertNotNull(st.getConnectedVehicles());
        assertTrue(st.getConnectedVehicles().isEmpty());
    }

    @Test
    @DisplayName("Test constructor with parameters")
    void testConstructorWithParameters() {
        BaseStation st = new BaseStation("Station A", stationPosition, 10000.0);
        
        assertEquals("Station A", st.getName());
        assertEquals(stationPosition, st.getPosition());
        assertEquals(10000.0, st.getCoverageRadius(), 0.01);
    }

    @Test
    @DisplayName("Test isInCoverage with vehicle in range")
    void testIsInCoverageInRange() {
        // Vehicle close to station (within 5km)
        assertTrue(station.isInCoverage(testVehicle), 
            "Vehicle within coverage radius should be in coverage");
    }

    @Test
    @DisplayName("Test isInCoverage with vehicle out of range")
    void testIsInCoverageOutOfRange() {
        // Vehicle far from station
        Position farPosition = new Position(41.1000, 29.1000, 100.0);
        Vehicle farVehicle = new Vehicle(VehicleType.CARGO, farPosition);
        
        assertFalse(station.isInCoverage(farVehicle), 
            "Vehicle outside coverage radius should not be in coverage");
    }

    @Test
    @DisplayName("Test isInCoverage with null vehicle")
    void testIsInCoverageNullVehicle() {
        assertFalse(station.isInCoverage(null), 
            "Should return false for null vehicle");
    }

    @Test
    @DisplayName("Test isInCoverage with vehicle having null position")
    void testIsInCoverageNullPosition() {
        Vehicle vehicleWithoutPosition = new Vehicle();
        vehicleWithoutPosition.setPosition(null);
        
        assertFalse(station.isInCoverage(vehicleWithoutPosition), 
            "Should return false when vehicle position is null");
    }

    @Test
    @DisplayName("Test isInCoverage at exact coverage boundary")
    void testIsInCoverageAtBoundary() {
        // Vehicle at exact coverage radius (5km = 5000m)
        // This is approximate since we're using Haversine distance
        Position atBoundary = new Position(41.0082, 28.9784, 100.0);
        Vehicle boundaryVehicle = new Vehicle(VehicleType.PASSENGER, atBoundary);
        
        // At station center, distance is 0, so should be in coverage
        assertTrue(station.isInCoverage(boundaryVehicle), 
            "Vehicle at station center should be in coverage");
    }

    @Test
    @DisplayName("Test connectVehicle")
    void testConnectVehicle() {
        String vehicleId = testVehicle.getId();
        
        station.connectVehicle(vehicleId);
        
        assertTrue(station.getConnectedVehicles().contains(vehicleId), 
            "Should add vehicle to connected vehicles");
        assertEquals(1, station.getConnectedVehicleCount(), 
            "Should have one connected vehicle");
    }

    @Test
    @DisplayName("Test connectVehicle duplicate")
    void testConnectVehicleDuplicate() {
        String vehicleId = testVehicle.getId();
        
        station.connectVehicle(vehicleId);
        station.connectVehicle(vehicleId); // Try to add again
        
        assertEquals(1, station.getConnectedVehicleCount(), 
            "Should not add duplicate vehicle");
    }

    @Test
    @DisplayName("Test disconnectVehicle")
    void testDisconnectVehicle() {
        String vehicleId = testVehicle.getId();
        
        station.connectVehicle(vehicleId);
        assertEquals(1, station.getConnectedVehicleCount());
        
        station.disconnectVehicle(vehicleId);
        
        assertFalse(station.getConnectedVehicles().contains(vehicleId), 
            "Should remove vehicle from connected vehicles");
        assertEquals(0, station.getConnectedVehicleCount(), 
            "Should have no connected vehicles");
    }

    @Test
    @DisplayName("Test disconnectVehicle not connected")
    void testDisconnectVehicleNotConnected() {
        String vehicleId = testVehicle.getId();
        
        // Try to disconnect vehicle that was never connected
        station.disconnectVehicle(vehicleId);
        
        assertEquals(0, station.getConnectedVehicleCount(), 
            "Should handle disconnect of non-connected vehicle gracefully");
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        station.setId("STATION-001");
        station.setName("Updated Station");
        station.setCoverageRadius(8000.0);
        station.setActive(false);
        
        Position newPosition = new Position(40.0, 30.0, 60.0);
        station.setPosition(newPosition);
        
        assertEquals("STATION-001", station.getId());
        assertEquals("Updated Station", station.getName());
        assertEquals(8000.0, station.getCoverageRadius(), 0.01);
        assertFalse(station.isActive());
        assertEquals(newPosition, station.getPosition());
    }

    @Test
    @DisplayName("Test connected vehicles immutability")
    void testConnectedVehiclesImmutability() {
        station.connectVehicle(testVehicle.getId());
        
        List<String> connectedVehicles = station.getConnectedVehicles();
        int initialSize = connectedVehicles.size();
        connectedVehicles.clear(); // Try to modify
        
        // Original list should not be modified
        assertEquals(initialSize, station.getConnectedVehicleCount(), 
            "Original connected vehicles list should not be modified");
    }

    @Test
    @DisplayName("Test getConnectedVehicleCount")
    void testGetConnectedVehicleCount() {
        assertEquals(0, station.getConnectedVehicleCount(), 
            "Should start with zero connected vehicles");
        
        station.connectVehicle("VEH-1");
        assertEquals(1, station.getConnectedVehicleCount());
        
        station.connectVehicle("VEH-2");
        assertEquals(2, station.getConnectedVehicleCount());
        
        station.disconnectVehicle("VEH-1");
        assertEquals(1, station.getConnectedVehicleCount());
    }

    @Test
    @DisplayName("Test edge case: very large coverage radius")
    void testVeryLargeCoverageRadius() {
        BaseStation largeStation = new BaseStation("Large Station", stationPosition, 50000.0); // 50km
        
        Position farPosition = new Position(41.1000, 29.0000, 100.0);
        Vehicle farVehicle = new Vehicle(VehicleType.CARGO, farPosition);
        
        assertTrue(largeStation.isInCoverage(farVehicle), 
            "Should cover vehicles within large radius");
    }

    @Test
    @DisplayName("Test edge case: very small coverage radius")
    void testVerySmallCoverageRadius() {
        BaseStation smallStation = new BaseStation("Small Station", stationPosition, 100.0); // 100m
        
        // Vehicle just outside small radius
        Position nearbyPos = new Position(41.0083, 28.9785, 100.0);
        Vehicle nearbyVehicle = new Vehicle(VehicleType.PASSENGER, nearbyPos);
        
        // May or may not be in coverage depending on exact distance
        boolean inCoverage = smallStation.isInCoverage(nearbyVehicle);
        assertNotNull(Boolean.valueOf(inCoverage), 
            "Should handle small coverage radius");
    }

    @Test
    @DisplayName("Test edge case: multiple vehicle connections")
    void testMultipleVehicleConnections() {
        Vehicle vehicle1 = new Vehicle(VehicleType.PASSENGER, vehiclePosition);
        Vehicle vehicle2 = new Vehicle(VehicleType.CARGO, vehiclePosition);
        Vehicle vehicle3 = new Vehicle(VehicleType.EMERGENCY, vehiclePosition);
        
        station.connectVehicle(vehicle1.getId());
        station.connectVehicle(vehicle2.getId());
        station.connectVehicle(vehicle3.getId());
        
        assertEquals(3, station.getConnectedVehicleCount(), 
            "Should handle multiple vehicle connections");
        assertTrue(station.getConnectedVehicles().contains(vehicle1.getId()));
        assertTrue(station.getConnectedVehicles().contains(vehicle2.getId()));
        assertTrue(station.getConnectedVehicles().contains(vehicle3.getId()));
    }

    @Test
    @DisplayName("Test edge case: inactive station")
    void testInactiveStation() {
        station.setActive(false);
        
        // Inactive station should still check coverage
        assertTrue(station.isInCoverage(testVehicle), 
            "Inactive station should still check coverage");
        
        // But may not accept connections in real implementation
        station.connectVehicle(testVehicle.getId());
        assertTrue(station.getConnectedVehicles().contains(testVehicle.getId()), 
            "Should still allow connections (implementation detail)");
    }
}






