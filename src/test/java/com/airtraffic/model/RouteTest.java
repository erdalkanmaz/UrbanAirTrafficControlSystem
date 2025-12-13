package com.airtraffic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Route class
 * Tests route waypoint management, distance calculations, and proximity checks
 */
@DisplayName("Route Tests")
class RouteTest {

    private Route route;
    private List<Position> waypoints;

    @BeforeEach
    void setUp() {
        route = new Route();
        waypoints = new ArrayList<>();
        
        // Create a simple route with 3 waypoints
        waypoints.add(new Position(41.0082, 28.9784, 100.0)); // Istanbul
        waypoints.add(new Position(40.7667, 29.9167, 120.0)); // Bursa
        waypoints.add(new Position(40.1833, 29.0667, 110.0)); // Yalova
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        Route r = new Route();
        
        assertNotNull(r.getId(), "Route should have an ID");
        assertTrue(r.getWaypoints().isEmpty(), "Default route should have no waypoints");
        assertTrue(r.getRestrictions().isEmpty(), "Default route should have no restrictions");
        assertTrue(r.isActive(), "Default route should be active");
    }

    @Test
    @DisplayName("Test constructor with name and waypoints")
    void testConstructorWithNameAndWaypoints() {
        Route r = new Route("Test Route", waypoints);
        
        assertEquals("Test Route", r.getName());
        assertEquals(3, r.getWaypoints().size());
        assertEquals(waypoints.get(0), r.getWaypoints().get(0));
    }

    @Test
    @DisplayName("Test add waypoint")
    void testAddWaypoint() {
        Position newWaypoint = new Position(40.0, 30.0, 100.0);
        route.addWaypoint(newWaypoint);
        
        assertEquals(1, route.getWaypoints().size());
        assertEquals(newWaypoint, route.getWaypoints().get(0));
    }

    @Test
    @DisplayName("Test add null waypoint")
    void testAddNullWaypoint() {
        assertThrows(IllegalArgumentException.class, () -> {
            route.addWaypoint(null);
        }, "Should throw exception for null waypoint");
    }

    @Test
    @DisplayName("Test calculate total distance with multiple waypoints")
    void testCalculateTotalDistance() {
        route.setWaypoints(waypoints);
        double distance = route.calculateTotalDistance();
        
        assertTrue(distance > 0, "Distance should be positive");
        // Istanbul to Bursa is approximately 150-200 km
        // Total route should be more than that
        assertTrue(distance > 150000, "Total distance should be significant");
    }

    @Test
    @DisplayName("Test calculate total distance with no waypoints")
    void testCalculateTotalDistanceNoWaypoints() {
        double distance = route.calculateTotalDistance();
        assertEquals(0.0, distance, 0.01, "Distance should be zero with no waypoints");
    }

    @Test
    @DisplayName("Test calculate total distance with single waypoint")
    void testCalculateTotalDistanceSingleWaypoint() {
        route.addWaypoint(new Position(40.0, 30.0, 100.0));
        double distance = route.calculateTotalDistance();
        
        assertEquals(0.0, distance, 0.01, "Distance should be zero with single waypoint");
    }

    @Test
    @DisplayName("Test calculate total distance with two waypoints")
    void testCalculateTotalDistanceTwoWaypoints() {
        List<Position> twoWaypoints = new ArrayList<>();
        twoWaypoints.add(new Position(41.0082, 28.9784, 100.0)); // Istanbul
        twoWaypoints.add(new Position(40.7667, 29.9167, 120.0)); // Bursa
        
        route.setWaypoints(twoWaypoints);
        double distance = route.calculateTotalDistance();
        
        // Istanbul to Bursa is approximately 100-150 km
        assertTrue(distance > 0, "Distance should be positive (actual: " + distance + " m)");
        assertTrue(distance > 50000, "Distance should be significant (Istanbul-Bursa ~100-150 km, actual: " + distance + " m)");
    }

    @Test
    @DisplayName("Test is near route - position on waypoint")
    void testIsNearRouteOnWaypoint() {
        route.setWaypoints(waypoints);
        Position testPosition = new Position(41.0082, 28.9784, 100.0);
        
        assertTrue(route.isNearRoute(testPosition, 1000.0), 
            "Position on waypoint should be near route");
    }

    @Test
    @DisplayName("Test is near route - position far from route")
    void testIsNearRouteFar() {
        route.setWaypoints(waypoints);
        // Position far from route (e.g., in another city)
        Position testPosition = new Position(39.9334, 32.8597, 100.0); // Ankara
        
        assertFalse(route.isNearRoute(testPosition, 1000.0), 
            "Position far from route should not be near");
    }

    @Test
    @DisplayName("Test is near route with small threshold")
    void testIsNearRouteSmallThreshold() {
        route.setWaypoints(waypoints);
        Position testPosition = new Position(41.0082, 28.9784, 100.0);
        
        assertTrue(route.isNearRoute(testPosition, 1.0), 
            "Position on waypoint should be near even with small threshold");
    }

    @Test
    @DisplayName("Test is near route with empty waypoints")
    void testIsNearRouteEmptyWaypoints() {
        Position testPosition = new Position(41.0082, 28.9784, 100.0);
        
        assertFalse(route.isNearRoute(testPosition, 1000.0), 
            "Empty route should not have any position near");
    }

    @Test
    @DisplayName("Test speed limit setter")
    void testSpeedLimitSetter() {
        route.setSpeedLimit(50.0);
        assertEquals(50.0, route.getSpeedLimit(), 0.01);
    }

    @Test
    @DisplayName("Test speed limit setter with negative value")
    void testSpeedLimitSetterNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            route.setSpeedLimit(-10.0);
        }, "Should throw exception for negative speed limit");
    }

    @Test
    @DisplayName("Test speed limit setter with zero")
    void testSpeedLimitSetterZero() {
        route.setSpeedLimit(0.0);
        assertEquals(0.0, route.getSpeedLimit(), 0.01);
    }

    @Test
    @DisplayName("Test altitude limits")
    void testAltitudeLimits() {
        route.setMinAltitude(100.0);
        route.setMaxAltitude(500.0);
        
        assertEquals(100.0, route.getMinAltitude(), 0.01);
        assertEquals(500.0, route.getMaxAltitude(), 0.01);
    }

    @Test
    @DisplayName("Test restrictions management")
    void testRestrictions() {
        route.addRestriction("No night flights");
        route.addRestriction("Weather dependent");
        
        List<String> restrictions = route.getRestrictions();
        assertEquals(2, restrictions.size());
        assertTrue(restrictions.contains("No night flights"));
        assertTrue(restrictions.contains("Weather dependent"));
    }

    @Test
    @DisplayName("Test set restrictions")
    void testSetRestrictions() {
        List<String> newRestrictions = new ArrayList<>();
        newRestrictions.add("Restriction 1");
        newRestrictions.add("Restriction 2");
        
        route.setRestrictions(newRestrictions);
        
        List<String> restrictions = route.getRestrictions();
        assertEquals(2, restrictions.size());
        assertTrue(restrictions.contains("Restriction 1"));
        assertTrue(restrictions.contains("Restriction 2"));
    }

    @Test
    @DisplayName("Test restrictions immutability")
    void testRestrictionsImmutability() {
        route.addRestriction("Test restriction");
        List<String> restrictions = route.getRestrictions();
        restrictions.add("Should not be added");
        
        // Original route should not be modified
        assertEquals(1, route.getRestrictions().size());
    }

    @Test
    @DisplayName("Test waypoints immutability")
    void testWaypointsImmutability() {
        route.setWaypoints(waypoints);
        List<Position> waypointsCopy = route.getWaypoints();
        waypointsCopy.add(new Position(40.0, 30.0, 100.0));
        
        // Original route should not be modified
        assertEquals(3, route.getWaypoints().size());
    }

    @Test
    @DisplayName("Test active status")
    void testActiveStatus() {
        route.setActive(true);
        assertTrue(route.isActive());
        
        route.setActive(false);
        assertFalse(route.isActive());
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        route.setId("ROUTE-001");
        route.setName("Test Route Name");
        route.setSpeedLimit(40.0);
        route.setMinAltitude(100.0);
        route.setMaxAltitude(500.0);
        route.setActive(false);
        
        assertEquals("ROUTE-001", route.getId());
        assertEquals("Test Route Name", route.getName());
        assertEquals(40.0, route.getSpeedLimit(), 0.01);
        assertEquals(100.0, route.getMinAltitude(), 0.01);
        assertEquals(500.0, route.getMaxAltitude(), 0.01);
        assertFalse(route.isActive());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        route.setId("ROUTE-001");
        route.setName("Test Route");
        route.setWaypoints(waypoints);
        
        String str = route.toString();
        
        assertTrue(str.contains("ROUTE-001"), "toString should contain route ID");
        assertTrue(str.contains("Test Route"), "toString should contain route name");
        assertTrue(str.contains("3"), "toString should contain waypoint count");
    }

    @Test
    @DisplayName("Test route with many waypoints")
    void testRouteWithManyWaypoints() {
        List<Position> manyWaypoints = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            manyWaypoints.add(new Position(40.0 + i * 0.1, 30.0 + i * 0.1, 100.0 + i * 10));
        }
        
        route.setWaypoints(manyWaypoints);
        double distance = route.calculateTotalDistance();
        
        assertEquals(10, route.getWaypoints().size());
        assertTrue(distance > 0, "Distance should be positive");
    }
}







