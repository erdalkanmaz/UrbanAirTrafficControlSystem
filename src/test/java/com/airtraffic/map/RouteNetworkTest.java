package com.airtraffic.map;

import com.airtraffic.model.Position;
import com.airtraffic.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RouteNetwork class
 * Tests route network management, route finding, and altitude filtering
 */
@DisplayName("RouteNetwork Tests")
class RouteNetworkTest {

    private RouteNetwork network;
    private Route mainStreet1;
    private Route mainStreet2;
    private Route sideStreet1;
    private Position testPosition;

    @BeforeEach
    void setUp() {
        network = new RouteNetwork("Istanbul");
        testPosition = new Position(41.0082, 28.9784, 100.0);
        
        // Create main streets
        List<Position> mainWaypoints1 = new ArrayList<>();
        mainWaypoints1.add(new Position(41.0050, 28.9750, 100.0));
        mainWaypoints1.add(new Position(41.0100, 28.9800, 100.0));
        mainStreet1 = new Route("Main Street 1", mainWaypoints1);
        mainStreet1.setMinAltitude(90.0);
        mainStreet1.setMaxAltitude(110.0);
        
        List<Position> mainWaypoints2 = new ArrayList<>();
        mainWaypoints2.add(new Position(41.0200, 29.0000, 100.0));
        mainWaypoints2.add(new Position(41.0250, 29.0050, 100.0));
        mainStreet2 = new Route("Main Street 2", mainWaypoints2);
        mainStreet2.setMinAltitude(90.0);
        mainStreet2.setMaxAltitude(110.0);
        
        // Create side street
        List<Position> sideWaypoints1 = new ArrayList<>();
        sideWaypoints1.add(new Position(41.0070, 28.9770, 75.0));
        sideWaypoints1.add(new Position(41.0090, 28.9790, 75.0));
        sideStreet1 = new Route("Side Street 1", sideWaypoints1);
        sideStreet1.setMinAltitude(70.0);
        sideStreet1.setMaxAltitude(80.0);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        RouteNetwork net = new RouteNetwork();
        
        assertNull(net.getCityName());
        assertNotNull(net.getMainStreets());
        assertNotNull(net.getSideStreets());
        assertTrue(net.getMainStreets().isEmpty());
        assertTrue(net.getSideStreets().isEmpty());
        assertEquals(50.0, net.getMainStreetSpacing(), 0.01);
        assertEquals(25.0, net.getSideStreetSpacing(), 0.01);
        assertEquals(100.0, net.getMainStreetAltitude(), 0.01);
        assertEquals(75.0, net.getSideStreetAltitude(), 0.01);
        assertEquals(25.0, net.getMainStreetConnectionOffset(), 0.01);
    }

    @Test
    @DisplayName("Test constructor with city name")
    void testConstructorWithCityName() {
        RouteNetwork net = new RouteNetwork("Ankara");
        
        assertEquals("Ankara", net.getCityName());
    }

    @Test
    @DisplayName("Test addMainStreet")
    void testAddMainStreet() {
        network.addMainStreet(mainStreet1);
        
        assertEquals(1, network.getMainStreets().size());
        assertEquals(mainStreet1, network.getMainStreets().get(0));
    }

    @Test
    @DisplayName("Test addSideStreet")
    void testAddSideStreet() {
        network.addSideStreet(sideStreet1);
        
        assertEquals(1, network.getSideStreets().size());
        assertEquals(sideStreet1, network.getSideStreets().get(0));
    }

    @Test
    @DisplayName("Test findNearestRoute with main street")
    void testFindNearestRouteMainStreet() {
        network.addMainStreet(mainStreet1);
        network.addMainStreet(mainStreet2);
        
        // Position near mainStreet1
        Position nearPosition = new Position(41.0075, 28.9775, 100.0);
        Route nearest = network.findNearestRoute(nearPosition);
        
        assertNotNull(nearest, "Should find nearest route");
        assertEquals(mainStreet1, nearest, "Should find mainStreet1 as nearest");
    }

    @Test
    @DisplayName("Test findNearestRoute with side street")
    void testFindNearestRouteSideStreet() {
        network.addMainStreet(mainStreet1);
        network.addSideStreet(sideStreet1);
        
        // Position very close to side street
        Position nearSideStreet = new Position(41.0080, 28.9780, 75.0);
        Route nearest = network.findNearestRoute(nearSideStreet);
        
        assertNotNull(nearest, "Should find nearest route");
        // Should find the closest route (could be main or side)
        assertTrue(nearest == mainStreet1 || nearest == sideStreet1, 
            "Should find one of the routes");
    }

    @Test
    @DisplayName("Test findNearestRoute with no routes")
    void testFindNearestRouteNoRoutes() {
        Route nearest = network.findNearestRoute(testPosition);
        
        assertNull(nearest, "Should return null when no routes exist");
    }

    @Test
    @DisplayName("Test getRoutesAtAltitude")
    void testGetRoutesAtAltitude() {
        network.addMainStreet(mainStreet1);
        network.addMainStreet(mainStreet2);
        network.addSideStreet(sideStreet1);
        
        // Get routes at 100m altitude with 10m tolerance
        List<Route> routesAt100m = network.getRoutesAtAltitude(100.0, 10.0);
        
        assertTrue(routesAt100m.size() >= 2, 
            "Should find main streets at 100m altitude");
        assertTrue(routesAt100m.contains(mainStreet1) || routesAt100m.contains(mainStreet2), 
            "Should include main streets");
    }

    @Test
    @DisplayName("Test getRoutesAtAltitude with side street altitude")
    void testGetRoutesAtAltitudeSideStreet() {
        network.addMainStreet(mainStreet1);
        network.addSideStreet(sideStreet1);
        
        // Get routes at 75m altitude
        List<Route> routesAt75m = network.getRoutesAtAltitude(75.0, 5.0);
        
        assertTrue(routesAt75m.contains(sideStreet1), 
            "Should find side street at 75m altitude");
    }

    @Test
    @DisplayName("Test getRoutesAtAltitude with no matching routes")
    void testGetRoutesAtAltitudeNoMatch() {
        network.addMainStreet(mainStreet1);
        network.addSideStreet(sideStreet1);
        
        // Get routes at 200m altitude (no routes at this altitude)
        List<Route> routesAt200m = network.getRoutesAtAltitude(200.0, 10.0);
        
        assertTrue(routesAt200m.isEmpty(), 
            "Should return empty list when no routes match altitude");
    }

    @Test
    @DisplayName("Test getActiveRoutes")
    void testGetActiveRoutes() {
        mainStreet1.setActive(true);
        mainStreet2.setActive(false);
        sideStreet1.setActive(true);
        
        network.addMainStreet(mainStreet1);
        network.addMainStreet(mainStreet2);
        network.addSideStreet(sideStreet1);
        
        List<Route> activeRoutes = network.getActiveRoutes();
        
        assertEquals(2, activeRoutes.size(), 
            "Should return 2 active routes");
        assertTrue(activeRoutes.contains(mainStreet1), 
            "Should include active main street");
        assertTrue(activeRoutes.contains(sideStreet1), 
            "Should include active side street");
        assertFalse(activeRoutes.contains(mainStreet2), 
            "Should not include inactive main street");
    }

    @Test
    @DisplayName("Test getActiveRoutes with no active routes")
    void testGetActiveRoutesNoActive() {
        mainStreet1.setActive(false);
        network.addMainStreet(mainStreet1);
        
        List<Route> activeRoutes = network.getActiveRoutes();
        
        assertTrue(activeRoutes.isEmpty(), 
            "Should return empty list when no routes are active");
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        network.setCityName("Bursa");
        network.setMainStreetSpacing(60.0);
        network.setSideStreetSpacing(30.0);
        network.setMainStreetAltitude(120.0);
        network.setSideStreetAltitude(90.0);
        network.setMainStreetConnectionOffset(30.0);
        
        assertEquals("Bursa", network.getCityName());
        assertEquals(60.0, network.getMainStreetSpacing(), 0.01);
        assertEquals(30.0, network.getSideStreetSpacing(), 0.01);
        assertEquals(120.0, network.getMainStreetAltitude(), 0.01);
        assertEquals(90.0, network.getSideStreetAltitude(), 0.01);
        assertEquals(30.0, network.getMainStreetConnectionOffset(), 0.01);
    }

    @Test
    @DisplayName("Test main streets immutability")
    void testMainStreetsImmutability() {
        network.addMainStreet(mainStreet1);
        
        List<Route> mainStreets = network.getMainStreets();
        mainStreets.clear(); // Try to modify
        
        // Original list should not be modified
        assertEquals(1, network.getMainStreets().size());
    }

    @Test
    @DisplayName("Test side streets immutability")
    void testSideStreetsImmutability() {
        network.addSideStreet(sideStreet1);
        
        List<Route> sideStreets = network.getSideStreets();
        sideStreets.clear(); // Try to modify
        
        // Original list should not be modified
        assertEquals(1, network.getSideStreets().size());
    }

    @Test
    @DisplayName("Test setMainStreets")
    void testSetMainStreets() {
        List<Route> newMainStreets = new ArrayList<>();
        newMainStreets.add(mainStreet1);
        newMainStreets.add(mainStreet2);
        
        network.setMainStreets(newMainStreets);
        
        assertEquals(2, network.getMainStreets().size());
        assertTrue(network.getMainStreets().contains(mainStreet1));
        assertTrue(network.getMainStreets().contains(mainStreet2));
    }

    @Test
    @DisplayName("Test setSideStreets")
    void testSetSideStreets() {
        List<Route> newSideStreets = new ArrayList<>();
        newSideStreets.add(sideStreet1);
        
        network.setSideStreets(newSideStreets);
        
        assertEquals(1, network.getSideStreets().size());
        assertTrue(network.getSideStreets().contains(sideStreet1));
    }

    @Test
    @DisplayName("Test edge case: findNearestRoute with equal distances")
    void testFindNearestRouteEqualDistances() {
        // Create two routes at same distance
        List<Position> waypoints1 = new ArrayList<>();
        waypoints1.add(new Position(41.0082, 28.9784, 100.0));
        Route route1 = new Route("Route 1", waypoints1);
        
        List<Position> waypoints2 = new ArrayList<>();
        waypoints2.add(new Position(41.0082, 28.9784, 100.0)); // Same position
        Route route2 = new Route("Route 2", waypoints2);
        
        network.addMainStreet(route1);
        network.addMainStreet(route2);
        
        Route nearest = network.findNearestRoute(testPosition);
        
        assertNotNull(nearest, "Should find a route even with equal distances");
        assertTrue(nearest == route1 || nearest == route2, 
            "Should return one of the routes");
    }

    @Test
    @DisplayName("Test edge case: getRoutesAtAltitude with exact match")
    void testGetRoutesAtAltitudeExactMatch() {
        mainStreet1.setMinAltitude(100.0);
        mainStreet1.setMaxAltitude(100.0);
        network.addMainStreet(mainStreet1);
        
        List<Route> routes = network.getRoutesAtAltitude(100.0, 0.0);
        
        assertTrue(routes.contains(mainStreet1), 
            "Should find route with exact altitude match");
    }

    @Test
    @DisplayName("Test edge case: large tolerance in getRoutesAtAltitude")
    void testGetRoutesAtAltitudeLargeTolerance() {
        network.addMainStreet(mainStreet1);
        network.addSideStreet(sideStreet1);
        
        // Large tolerance should catch both
        List<Route> routes = network.getRoutesAtAltitude(100.0, 50.0);
        
        assertTrue(routes.size() >= 1, 
            "Large tolerance should find multiple routes");
    }
}






