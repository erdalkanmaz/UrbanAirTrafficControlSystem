package com.airtraffic.map;

import com.airtraffic.model.Position;
import com.airtraffic.model.Route;
import com.airtraffic.model.RouteDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RouteNetwork segment management
 */
@DisplayName("RouteNetwork Segment Management Tests")
class RouteNetworkSegmentTest {

    private RouteNetwork network;
    private Route mainStreet1;
    private Route sideStreet1;

    @BeforeEach
    void setUp() {
        network = new RouteNetwork("Istanbul");
        
        // Create main street
        List<Position> mainWaypoints = new ArrayList<>();
        mainWaypoints.add(new Position(41.0050, 28.9750, 100.0));
        mainWaypoints.add(new Position(41.0100, 28.9800, 100.0));
        mainStreet1 = new Route("Main Street 1", mainWaypoints);
        mainStreet1.setMinAltitude(90.0);
        mainStreet1.setMaxAltitude(110.0);
        network.addMainStreet(mainStreet1);
        
        // Create side street
        List<Position> sideWaypoints = new ArrayList<>();
        sideWaypoints.add(new Position(41.0070, 28.9770, 80.0));
        sideWaypoints.add(new Position(41.0090, 28.9790, 80.0));
        sideStreet1 = new Route("Side Street 1", sideWaypoints);
        sideStreet1.setMinAltitude(70.0);
        sideStreet1.setMaxAltitude(90.0);
        network.addSideStreet(sideStreet1);
    }

    @Test
    @DisplayName("Test createSegmentsForRoute - FORWARD direction")
    void testCreateSegmentsForRouteForward() {
        List<RouteSegment> segments = network.createSegmentsForRoute(
            mainStreet1, 1000.0, RouteDirection.FORWARD, 100.0, 25.0);
        
        assertNotNull(segments);
        assertTrue(segments.size() > 0);
        assertEquals(RouteDirection.FORWARD, segments.get(0).getDirection());
        assertEquals(100.0, segments.get(0).getAltitude());
        assertEquals(25.0, segments.get(0).getSpeedLimit());
    }

    @Test
    @DisplayName("Test createSegmentsForRoute - REVERSE direction")
    void testCreateSegmentsForRouteReverse() {
        List<RouteSegment> segments = network.createSegmentsForRoute(
            mainStreet1, 1000.0, RouteDirection.REVERSE, 105.0, 25.0);
        
        assertNotNull(segments);
        assertTrue(segments.size() > 0);
        assertEquals(RouteDirection.REVERSE, segments.get(0).getDirection());
        assertEquals(105.0, segments.get(0).getAltitude());
    }

    @Test
    @DisplayName("Test createSegmentsForRoute - null route throws exception")
    void testCreateSegmentsForRouteNullRoute() {
        assertThrows(IllegalArgumentException.class, () -> {
            network.createSegmentsForRoute(null, 1000.0, RouteDirection.FORWARD, 100.0, 25.0);
        });
    }

    @Test
    @DisplayName("Test getSegmentsForRoute")
    void testGetSegmentsForRoute() {
        network.createSegmentsForRoute(mainStreet1, 1000.0, RouteDirection.FORWARD, 100.0, 25.0);
        
        List<RouteSegment> segments = network.getSegmentsForRoute(mainStreet1.getId());
        
        assertNotNull(segments);
        assertTrue(segments.size() > 0);
    }

    @Test
    @DisplayName("Test getSegmentsForRoute - non-existent route")
    void testGetSegmentsForRouteNonExistent() {
        List<RouteSegment> segments = network.getSegmentsForRoute("non-existent-id");
        
        assertNotNull(segments);
        assertTrue(segments.isEmpty());
    }

    @Test
    @DisplayName("Test getAllActiveSegments")
    void testGetAllActiveSegments() {
        network.createSegmentsForRoute(mainStreet1, 1000.0, RouteDirection.FORWARD, 100.0, 25.0);
        network.createSegmentsForRoute(sideStreet1, 500.0, RouteDirection.FORWARD, 80.0, 15.0);
        
        List<RouteSegment> allSegments = network.getAllActiveSegments();
        
        assertNotNull(allSegments);
        assertTrue(allSegments.size() > 0);
        
        // All segments should be active
        for (RouteSegment segment : allSegments) {
            assertTrue(segment.isActive());
        }
    }

    @Test
    @DisplayName("Test getSegmentsByDirection - FORWARD")
    void testGetSegmentsByDirectionForward() {
        network.createSegmentsForRoute(mainStreet1, 1000.0, RouteDirection.FORWARD, 100.0, 25.0);
        network.createSegmentsForRoute(sideStreet1, 500.0, RouteDirection.REVERSE, 80.0, 15.0);
        
        List<RouteSegment> forwardSegments = network.getSegmentsByDirection(RouteDirection.FORWARD);
        
        assertNotNull(forwardSegments);
        assertTrue(forwardSegments.size() > 0);
        
        for (RouteSegment segment : forwardSegments) {
            assertEquals(RouteDirection.FORWARD, segment.getDirection());
        }
    }

    @Test
    @DisplayName("Test getSegmentsByDirection - REVERSE")
    void testGetSegmentsByDirectionReverse() {
        network.createSegmentsForRoute(mainStreet1, 1000.0, RouteDirection.FORWARD, 100.0, 25.0);
        network.createSegmentsForRoute(sideStreet1, 500.0, RouteDirection.REVERSE, 80.0, 15.0);
        
        List<RouteSegment> reverseSegments = network.getSegmentsByDirection(RouteDirection.REVERSE);
        
        assertNotNull(reverseSegments);
        assertTrue(reverseSegments.size() > 0);
        
        for (RouteSegment segment : reverseSegments) {
            assertEquals(RouteDirection.REVERSE, segment.getDirection());
        }
    }

    @Test
    @DisplayName("Test findNearestSegment - point on segment")
    void testFindNearestSegmentPointOnSegment() {
        network.createSegmentsForRoute(mainStreet1, 1000.0, RouteDirection.FORWARD, 100.0, 25.0);
        
        // Point near start of route
        Position testPosition = new Position(41.0050, 28.9750, 100.0);
        RouteSegment nearest = network.findNearestSegment(testPosition, 50.0);
        
        assertNotNull(nearest);
    }

    @Test
    @DisplayName("Test findNearestSegment - point far away")
    void testFindNearestSegmentPointFarAway() {
        network.createSegmentsForRoute(mainStreet1, 1000.0, RouteDirection.FORWARD, 100.0, 25.0);
        
        // Point far away
        Position testPosition = new Position(42.0000, 30.0000, 100.0);
        RouteSegment nearest = network.findNearestSegment(testPosition, 10.0);
        
        assertNull(nearest); // Should not find any segment within 10m
    }

    @Test
    @DisplayName("Test findNearestSegment - null position")
    void testFindNearestSegmentNullPosition() {
        network.createSegmentsForRoute(mainStreet1, 1000.0, RouteDirection.FORWARD, 100.0, 25.0);
        
        RouteSegment nearest = network.findNearestSegment(null, 50.0);
        
        assertNull(nearest);
    }
}

