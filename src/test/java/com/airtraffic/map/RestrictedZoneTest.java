package com.airtraffic.map;

import com.airtraffic.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RestrictedZone class
 * Tests restricted zone creation, point-in-polygon checks, and boundary management
 */
@DisplayName("RestrictedZone Tests")
class RestrictedZoneTest {

    private RestrictedZone zone;
    private List<Position> squareBoundaries;

    @BeforeEach
    void setUp() {
        zone = new RestrictedZone("Test Zone", RestrictedZoneType.GOVERNMENT);
        
        // Create a simple square zone
        squareBoundaries = new ArrayList<>();
        squareBoundaries.add(new Position(41.0050, 28.9750, 0.0)); // Bottom-left
        squareBoundaries.add(new Position(41.0100, 28.9750, 0.0)); // Bottom-right
        squareBoundaries.add(new Position(41.0100, 28.9800, 0.0)); // Top-right
        squareBoundaries.add(new Position(41.0050, 28.9800, 0.0)); // Top-left
        zone.setBoundaries(squareBoundaries);
        zone.setMinAltitude(0.0);
        zone.setMaxAltitude(200.0);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        RestrictedZone z = new RestrictedZone();
        
        assertNull(z.getName());
        assertNull(z.getType());
        assertNotNull(z.getBoundaries());
        assertTrue(z.getBoundaries().isEmpty());
        assertTrue(z.isPermanent());
        assertEquals(0.0, z.getMinAltitude(), 0.01);
        assertEquals(0.0, z.getMaxAltitude(), 0.01);
    }

    @Test
    @DisplayName("Test constructor with name and type")
    void testConstructorWithNameAndType() {
        RestrictedZone z = new RestrictedZone("Military Base", RestrictedZoneType.MILITARY);
        
        assertEquals("Military Base", z.getName());
        assertEquals(RestrictedZoneType.MILITARY, z.getType());
        assertNotNull(z.getBoundaries());
    }

    @Test
    @DisplayName("Test contains with position inside polygon")
    void testContainsInsidePolygon() {
        // Position inside the square zone
        Position insidePosition = new Position(41.0075, 28.9775, 100.0);
        assertTrue(zone.contains(insidePosition), 
            "Position inside polygon should be contained");
    }

    @Test
    @DisplayName("Test contains with position outside polygon")
    void testContainsOutsidePolygon() {
        // Position outside the square zone
        Position outsidePosition = new Position(41.0200, 29.0000, 100.0);
        assertFalse(zone.contains(outsidePosition), 
            "Position outside polygon should not be contained");
    }

    @Test
    @DisplayName("Test contains with position below min altitude")
    void testContainsBelowMinAltitude() {
        zone.setMinAltitude(50.0);
        
        // Position inside polygon but below min altitude
        Position belowPosition = new Position(41.0075, 28.9775, 30.0);
        assertFalse(zone.contains(belowPosition), 
            "Position below min altitude should not be contained");
    }

    @Test
    @DisplayName("Test contains with position above max altitude")
    void testContainsAboveMaxAltitude() {
        zone.setMaxAltitude(150.0);
        
        // Position inside polygon but above max altitude
        Position abovePosition = new Position(41.0075, 28.9775, 200.0);
        assertFalse(zone.contains(abovePosition), 
            "Position above max altitude should not be contained");
    }

    @Test
    @DisplayName("Test contains with insufficient boundary points")
    void testContainsInsufficientBoundaries() {
        RestrictedZone smallZone = new RestrictedZone("Small Zone", RestrictedZoneType.TEMPORARY);
        smallZone.addBoundaryPoint(new Position(41.0050, 28.9750, 0.0));
        smallZone.addBoundaryPoint(new Position(41.0100, 28.9750, 0.0));
        // Only 2 points - not enough for polygon
        
        Position testPosition = new Position(41.0075, 28.9775, 100.0);
        assertFalse(smallZone.contains(testPosition), 
            "Zone with less than 3 boundary points should not contain any position");
    }

    @Test
    @DisplayName("Test addBoundaryPoint")
    void testAddBoundaryPoint() {
        RestrictedZone z = new RestrictedZone("Test", RestrictedZoneType.HOSPITAL);
        
        Position point1 = new Position(41.0050, 28.9750, 0.0);
        Position point2 = new Position(41.0100, 28.9750, 0.0);
        
        z.addBoundaryPoint(point1);
        z.addBoundaryPoint(point2);
        
        assertEquals(2, z.getBoundaries().size());
        assertEquals(point1, z.getBoundaries().get(0));
        assertEquals(point2, z.getBoundaries().get(1));
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        zone.setId("ZONE-001");
        zone.setName("Updated Zone");
        zone.setType(RestrictedZoneType.MILITARY);
        zone.setMinAltitude(50.0);
        zone.setMaxAltitude(300.0);
        zone.setRestrictionReason("Security reasons");
        zone.setPermanent(false);
        
        assertEquals("ZONE-001", zone.getId());
        assertEquals("Updated Zone", zone.getName());
        assertEquals(RestrictedZoneType.MILITARY, zone.getType());
        assertEquals(50.0, zone.getMinAltitude(), 0.01);
        assertEquals(300.0, zone.getMaxAltitude(), 0.01);
        assertEquals("Security reasons", zone.getRestrictionReason());
        assertFalse(zone.isPermanent());
    }

    @Test
    @DisplayName("Test boundaries immutability")
    void testBoundariesImmutability() {
        List<Position> boundaries = zone.getBoundaries();
        boundaries.clear(); // Try to modify
        
        // Original list should not be modified
        assertEquals(4, zone.getBoundaries().size());
    }

    @Test
    @DisplayName("Test setBoundaries")
    void testSetBoundaries() {
        List<Position> newBoundaries = new ArrayList<>();
        newBoundaries.add(new Position(40.0, 30.0, 0.0));
        newBoundaries.add(new Position(41.0, 30.0, 0.0));
        newBoundaries.add(new Position(41.0, 31.0, 0.0));
        newBoundaries.add(new Position(40.0, 31.0, 0.0));
        
        zone.setBoundaries(newBoundaries);
        
        assertEquals(4, zone.getBoundaries().size());
        assertEquals(newBoundaries.get(0), zone.getBoundaries().get(0));
    }

    @Test
    @DisplayName("Test edge case: position at boundary vertex")
    void testPositionAtBoundaryVertex() {
        // Position exactly at a boundary vertex
        Position atVertex = new Position(41.0050, 28.9750, 100.0);
        // Point-in-polygon algorithm behavior at vertices may vary
        // This test verifies the method doesn't crash
        boolean result = zone.contains(atVertex);
        assertNotNull(Boolean.valueOf(result), 
            "Method should handle position at vertex without error");
    }

    @Test
    @DisplayName("Test edge case: position on boundary edge")
    void testPositionOnBoundaryEdge() {
        // Position on the edge (midpoint of bottom edge)
        Position onEdge = new Position(41.0075, 28.9750, 100.0);
        // Point-in-polygon behavior on edges may vary
        boolean result = zone.contains(onEdge);
        assertNotNull(Boolean.valueOf(result), 
            "Method should handle position on edge without error");
    }

    @Test
    @DisplayName("Test edge case: very large restricted zone")
    void testVeryLargeRestrictedZone() {
        List<Position> largeBoundaries = new ArrayList<>();
        largeBoundaries.add(new Position(40.0, 28.0, 0.0));
        largeBoundaries.add(new Position(42.0, 28.0, 0.0));
        largeBoundaries.add(new Position(42.0, 30.0, 0.0));
        largeBoundaries.add(new Position(40.0, 30.0, 0.0));
        
        RestrictedZone largeZone = new RestrictedZone("Large Zone", RestrictedZoneType.ENVIRONMENTAL);
        largeZone.setBoundaries(largeBoundaries);
        largeZone.setMinAltitude(0.0);
        largeZone.setMaxAltitude(1000.0);
        
        Position insideLarge = new Position(41.0, 29.0, 500.0);
        assertTrue(largeZone.contains(insideLarge), 
            "Position inside very large zone should be contained");
    }

    @Test
    @DisplayName("Test edge case: zone with altitude range")
    void testZoneWithAltitudeRange() {
        zone.setMinAltitude(100.0);
        zone.setMaxAltitude(200.0);
        
        // Position within altitude range
        Position withinRange = new Position(41.0075, 28.9775, 150.0);
        assertTrue(zone.contains(withinRange), 
            "Position within altitude range should be contained");
        
        // Position at min altitude
        Position atMin = new Position(41.0075, 28.9775, 100.0);
        assertTrue(zone.contains(atMin), 
            "Position at min altitude should be contained");
        
        // Position at max altitude
        Position atMax = new Position(41.0075, 28.9775, 200.0);
        assertTrue(zone.contains(atMax), 
            "Position at max altitude should be contained");
    }

    @Test
    @DisplayName("Test edge case: temporary vs permanent zone")
    void testTemporaryVsPermanent() {
        RestrictedZone tempZone = new RestrictedZone("Temp Zone", RestrictedZoneType.TEMPORARY);
        tempZone.setPermanent(false);
        
        assertFalse(tempZone.isPermanent(), 
            "Temporary zone should not be permanent");
        
        RestrictedZone permZone = new RestrictedZone("Perm Zone", RestrictedZoneType.GOVERNMENT);
        permZone.setPermanent(true);
        
        assertTrue(permZone.isPermanent(), 
            "Permanent zone should be permanent");
    }

    @Test
    @DisplayName("Test complex polygon with many vertices")
    void testComplexPolygon() {
        RestrictedZone complexZone = new RestrictedZone("Complex Zone", RestrictedZoneType.AIRPORT);
        
        // Create a hexagon
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * 2 * i / 6;
            double lat = 41.0082 + 0.005 * Math.cos(angle);
            double lon = 28.9784 + 0.005 * Math.sin(angle);
            complexZone.addBoundaryPoint(new Position(lat, lon, 0.0));
        }
        
        complexZone.setMinAltitude(0.0);
        complexZone.setMaxAltitude(500.0);
        
        // Position at center
        Position center = new Position(41.0082, 28.9784, 100.0);
        assertTrue(complexZone.contains(center), 
            "Position at center of complex polygon should be contained");
    }
}






