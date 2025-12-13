package com.airtraffic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Position class
 * Tests 3D position calculations, distance measurements, and coordinate handling
 */
@DisplayName("Position Tests")
class PositionTest {

    private Position position1;
    private Position position2;
    private Position position3;

    @BeforeEach
    void setUp() {
        // Istanbul coordinates
        position1 = new Position(41.0082, 28.9784, 100.0);
        // Ankara coordinates
        position2 = new Position(39.9334, 32.8597, 150.0);
        // Same location as position1 but different altitude
        position3 = new Position(41.0082, 28.9784, 200.0);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        Position pos = new Position();
        assertNotNull(pos.getTimestamp());
        assertEquals(0.0, pos.getLatitude());
        assertEquals(0.0, pos.getLongitude());
        assertEquals(0.0, pos.getAltitude());
    }

    @Test
    @DisplayName("Test constructor with coordinates")
    void testConstructorWithCoordinates() {
        Position pos = new Position(40.0, 30.0, 100.0);
        assertEquals(40.0, pos.getLatitude());
        assertEquals(30.0, pos.getLongitude());
        assertEquals(100.0, pos.getAltitude());
        assertNotNull(pos.getTimestamp());
    }

    @Test
    @DisplayName("Test constructor with timestamp")
    void testConstructorWithTimestamp() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);
        Position pos = new Position(40.0, 30.0, 100.0, timestamp);
        assertEquals(40.0, pos.getLatitude());
        assertEquals(30.0, pos.getLongitude());
        assertEquals(100.0, pos.getAltitude());
        assertEquals(timestamp, pos.getTimestamp());
    }

    @Test
    @DisplayName("Test horizontal distance calculation")
    void testHorizontalDistance() {
        double distance = position1.horizontalDistanceTo(position2);
        
        // Istanbul to Ankara is approximately 350-360 km
        // Using wider range to account for calculation variations
        assertTrue(distance > 340000 && distance < 380000, 
            "Distance should be approximately 350-360 km (actual: " + distance + " m)");
        assertTrue(distance > 0, "Distance should be positive");
    }

    @Test
    @DisplayName("Test horizontal distance for same location")
    void testHorizontalDistanceSameLocation() {
        Position samePos = new Position(41.0082, 28.9784, 150.0);
        double distance = position1.horizontalDistanceTo(samePos);
        
        // Should be very close to 0 (within 1 meter tolerance)
        assertTrue(distance < 1.0, "Distance should be near zero for same location");
    }

    @Test
    @DisplayName("Test vertical distance calculation")
    void testVerticalDistance() {
        double verticalDist = position1.verticalDistanceTo(position2);
        assertEquals(50.0, verticalDist, 0.01, "Vertical distance should be 50 meters");
    }

    @Test
    @DisplayName("Test vertical distance for same altitude")
    void testVerticalDistanceSameAltitude() {
        Position sameAlt = new Position(40.0, 30.0, 100.0);
        double verticalDist = position1.verticalDistanceTo(sameAlt);
        // Both positions have altitude 100.0, so vertical distance should be 0
        assertEquals(0.0, verticalDist, 0.01, "Vertical distance should be zero for same altitude");
    }

    @Test
    @DisplayName("Test 3D distance calculation")
    void test3DDistance() {
        double distance3D = position1.distance3DTo(position3);
        
        // Should be approximately 100 meters (vertical distance)
        assertTrue(distance3D > 99.0 && distance3D < 101.0, 
            "3D distance should be approximately 100 meters");
    }

    @Test
    @DisplayName("Test 3D distance with horizontal and vertical components")
    void test3DDistanceWithBothComponents() {
        double distance3D = position1.distance3DTo(position2);
        double horizontal = position1.horizontalDistanceTo(position2);
        double vertical = position1.verticalDistanceTo(position2);
        
        // 3D distance should be sqrt(horizontal^2 + vertical^2)
        double expected = Math.sqrt(horizontal * horizontal + vertical * vertical);
        assertEquals(expected, distance3D, 0.01, "3D distance should match calculated value");
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        Position pos = new Position();
        pos.setLatitude(45.0);
        pos.setLongitude(35.0);
        pos.setAltitude(250.0);
        
        assertEquals(45.0, pos.getLatitude());
        assertEquals(35.0, pos.getLongitude());
        assertEquals(250.0, pos.getAltitude());
    }

    @Test
    @DisplayName("Test timestamp setter")
    void testTimestampSetter() {
        Position pos = new Position();
        LocalDateTime newTimestamp = LocalDateTime.of(2024, 6, 15, 14, 30);
        pos.setTimestamp(newTimestamp);
        
        assertEquals(newTimestamp, pos.getTimestamp());
    }

    @Test
    @DisplayName("Test equals method")
    void testEquals() {
        Position pos1 = new Position(40.0, 30.0, 100.0);
        Position pos2 = new Position(40.0, 30.0, 100.0);
        Position pos3 = new Position(40.0, 30.0, 150.0);
        Position pos4 = new Position(41.0, 30.0, 100.0);
        
        assertEquals(pos1, pos2, "Positions with same coordinates should be equal");
        assertNotEquals(pos1, pos3, "Positions with different altitudes should not be equal");
        assertNotEquals(pos1, pos4, "Positions with different latitudes should not be equal");
        assertEquals(pos1, pos1, "Position should equal itself");
    }

    @Test
    @DisplayName("Test equals with null")
    void testEqualsWithNull() {
        Position pos = new Position(40.0, 30.0, 100.0);
        assertNotEquals(null, pos, "Position should not equal null");
    }

    @Test
    @DisplayName("Test hashCode")
    void testHashCode() {
        Position pos1 = new Position(40.0, 30.0, 100.0);
        Position pos2 = new Position(40.0, 30.0, 100.0);
        Position pos3 = new Position(40.0, 30.0, 150.0);
        
        assertEquals(pos1.hashCode(), pos2.hashCode(), 
            "Equal positions should have same hash code");
        assertNotEquals(pos1.hashCode(), pos3.hashCode(), 
            "Different positions should have different hash codes");
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        Position pos = new Position(41.0082, 28.9784, 100.5);
        String str = pos.toString();
        
        // toString format should be: "Position(41.008200, 28.978400, 100.50m)"
        assertNotNull(str, "toString should not be null");
        
        // Verify format structure
        assertTrue(str.startsWith("Position("), 
            "toString should start with 'Position(' (actual: " + str + ")");
        assertTrue(str.endsWith("m)"), 
            "toString should end with 'm)' (actual: " + str + ")");
        
        // Verify it contains the expected values (using more flexible matching)
        // The format uses %.6f for lat/lon, so values will be like 41.008200
        assertTrue(str.contains("41") && str.contains("28") && str.contains("100"), 
            "toString should contain coordinate values (actual: " + str + ")");
        
        // Verify the format matches the expected pattern
        String expectedPattern = String.format("Position(%.6f, %.6f, %.2fm)", 
            pos.getLatitude(), pos.getLongitude(), pos.getAltitude());
        assertEquals(expectedPattern, str, "toString should match expected format");
    }

    @Test
    @DisplayName("Test edge case: extreme coordinates")
    void testExtremeCoordinates() {
        // North Pole
        Position northPole = new Position(90.0, 0.0, 0.0);
        // South Pole
        Position southPole = new Position(-90.0, 0.0, 0.0);
        
        double distance = northPole.horizontalDistanceTo(southPole);
        // Should be approximately half the Earth's circumference
        assertTrue(distance > 20000000 && distance < 21000000, 
            "Distance between poles should be approximately 20,000 km");
    }

    @Test
    @DisplayName("Test edge case: zero altitude")
    void testZeroAltitude() {
        Position pos1 = new Position(40.0, 30.0, 0.0);
        Position pos2 = new Position(40.0, 30.0, 100.0);
        
        double verticalDist = pos1.verticalDistanceTo(pos2);
        assertEquals(100.0, verticalDist, 0.01, "Vertical distance should be 100 meters");
    }

    @Test
    @DisplayName("Test edge case: very high altitude")
    void testVeryHighAltitude() {
        Position ground = new Position(40.0, 30.0, 0.0);
        Position high = new Position(40.0, 30.0, 10000.0); // 10 km altitude
        
        double verticalDist = ground.verticalDistanceTo(high);
        assertEquals(10000.0, verticalDist, 0.01, "Vertical distance should be 10 km");
    }
}







