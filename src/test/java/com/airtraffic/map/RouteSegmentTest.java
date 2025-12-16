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
 * Unit tests for RouteSegment class
 */
@DisplayName("RouteSegment Tests")
class RouteSegmentTest {

    private RouteSegment segment;
    private Route parentRoute;
    private Position startPoint;
    private Position endPoint;

    @BeforeEach
    void setUp() {
        List<Position> waypoints = new ArrayList<>();
        waypoints.add(new Position(41.0082, 28.9784, 100.0));
        waypoints.add(new Position(41.0100, 28.9800, 100.0));
        parentRoute = new Route("Test Route", waypoints);
        
        startPoint = new Position(41.0082, 28.9784, 100.0);
        endPoint = new Position(41.0100, 28.9800, 100.0);
        
        segment = new RouteSegment(parentRoute, startPoint, endPoint, 
            RouteDirection.FORWARD, 100.0, 25.0);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        RouteSegment seg = new RouteSegment();
        
        assertNotNull(seg);
        assertNotNull(seg.getSegmentId());
        assertTrue(seg.isActive());
        assertEquals(50, seg.getMaxVehicles());
    }

    @Test
    @DisplayName("Test constructor with parameters")
    void testConstructorWithParameters() {
        assertEquals(parentRoute, segment.getParentRoute());
        assertEquals(startPoint, segment.getStartPoint());
        assertEquals(endPoint, segment.getEndPoint());
        assertEquals(RouteDirection.FORWARD, segment.getDirection());
        assertEquals(100.0, segment.getAltitude());
        assertEquals(25.0, segment.getSpeedLimit());
    }

    @Test
    @DisplayName("Test calculateLength")
    void testCalculateLength() {
        double length = segment.calculateLength();
        
        assertTrue(length > 0);
        // Haversine distance between two points should be approximately correct
        assertTrue(length < 10000); // Should be less than 10km for these coordinates
    }

    @Test
    @DisplayName("Test calculateLength with null points")
    void testCalculateLengthWithNullPoints() {
        RouteSegment seg = new RouteSegment();
        assertEquals(0.0, seg.calculateLength());
        
        seg.setStartPoint(startPoint);
        assertEquals(0.0, seg.calculateLength());
        
        seg.setEndPoint(endPoint);
        assertTrue(seg.calculateLength() > 0);
    }

    @Test
    @DisplayName("Test isOnSegment - point on start")
    void testIsOnSegmentPointOnStart() {
        Position point = new Position(41.0082, 28.9784, 100.0);
        assertTrue(segment.isOnSegment(point, 10.0));
    }

    @Test
    @DisplayName("Test isOnSegment - point on end")
    void testIsOnSegmentPointOnEnd() {
        Position point = new Position(41.0100, 28.9800, 100.0);
        assertTrue(segment.isOnSegment(point, 10.0));
    }

    @Test
    @DisplayName("Test isOnSegment - point far away")
    void testIsOnSegmentPointFarAway() {
        Position point = new Position(41.1000, 29.1000, 100.0); // Far away
        assertFalse(segment.isOnSegment(point, 10.0));
    }

    @Test
    @DisplayName("Test isOnSegment - null position")
    void testIsOnSegmentNullPosition() {
        assertFalse(segment.isOnSegment(null, 10.0));
    }

    @Test
    @DisplayName("Test isOnSegment - null start point")
    void testIsOnSegmentNullStartPoint() {
        segment.setStartPoint(null);
        Position point = new Position(41.0082, 28.9784, 100.0);
        assertFalse(segment.isOnSegment(point, 10.0));
    }

    @Test
    @DisplayName("Test setAltitude - valid value")
    void testSetAltitudeValid() {
        segment.setAltitude(120.0);
        assertEquals(120.0, segment.getAltitude());
    }

    @Test
    @DisplayName("Test setAltitude - negative value throws exception")
    void testSetAltitudeNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            segment.setAltitude(-10.0);
        });
    }

    @Test
    @DisplayName("Test setSpeedLimit - valid value")
    void testSetSpeedLimitValid() {
        segment.setSpeedLimit(30.0);
        assertEquals(30.0, segment.getSpeedLimit());
    }

    @Test
    @DisplayName("Test setSpeedLimit - negative value throws exception")
    void testSetSpeedLimitNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            segment.setSpeedLimit(-10.0);
        });
    }

    @Test
    @DisplayName("Test setMaxVehicles - valid value")
    void testSetMaxVehiclesValid() {
        segment.setMaxVehicles(100);
        assertEquals(100, segment.getMaxVehicles());
    }

    @Test
    @DisplayName("Test setMaxVehicles - negative value throws exception")
    void testSetMaxVehiclesNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            segment.setMaxVehicles(-10);
        });
    }

    @Test
    @DisplayName("Test setDirection")
    void testSetDirection() {
        segment.setDirection(RouteDirection.REVERSE);
        assertEquals(RouteDirection.REVERSE, segment.getDirection());
    }

    @Test
    @DisplayName("Test setActive")
    void testSetActive() {
        segment.setActive(false);
        assertFalse(segment.isActive());
        
        segment.setActive(true);
        assertTrue(segment.isActive());
    }

    @Test
    @DisplayName("Test toString")
    void testToString() {
        String str = segment.toString();
        assertNotNull(str);
        assertTrue(str.contains("RouteSegment"));
        assertTrue(str.contains(segment.getSegmentId().substring(0, 8)));
    }
}

