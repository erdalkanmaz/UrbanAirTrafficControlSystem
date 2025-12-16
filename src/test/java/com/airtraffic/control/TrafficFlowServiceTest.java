package com.airtraffic.control;

import com.airtraffic.map.CityMap;
import com.airtraffic.map.RouteNetwork;
import com.airtraffic.map.RouteSegment;
import com.airtraffic.model.Route;
import com.airtraffic.model.Position;
import com.airtraffic.model.RouteDirection;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrafficFlowService
 */
@DisplayName("TrafficFlowService Tests")
class TrafficFlowServiceTest {

    private TrafficFlowService service;
    private RouteNetwork routeNetwork;
    private CityMap cityMap;
    private com.airtraffic.model.Route mainStreet;
    private Vehicle vehicle1;

    @BeforeEach
    void setUp() {
        service = new TrafficFlowService();
        
        cityMap = new CityMap("Istanbul");
        cityMap.setMinLatitude(40.8);
        cityMap.setMaxLatitude(41.2);
        cityMap.setMinLongitude(28.5);
        cityMap.setMaxLongitude(29.5);
        
        routeNetwork = new RouteNetwork("Istanbul");
        
        // Create main street
        List<Position> waypoints = new ArrayList<>();
        waypoints.add(new Position(41.0050, 28.9750, 100.0));
        waypoints.add(new Position(41.0100, 28.9800, 100.0));
        mainStreet = new Route("Main Street 1", waypoints);
        routeNetwork.addMainStreet(mainStreet);
        
        // Create segments
        routeNetwork.createSegmentsForRoute(mainStreet, 1000.0, RouteDirection.FORWARD, 100.0, 25.0);
        
        service.initialize(routeNetwork, cityMap);
        
        // Create vehicle
        vehicle1 = new Vehicle(VehicleType.PASSENGER, new Position(41.0050, 28.9750, 100.0));
        vehicle1.setVelocity(20.0);
    }

    @Test
    @DisplayName("Test updateVehicleSegment - finds segment")
    void testUpdateVehicleSegmentFindsSegment() {
        RouteSegment segment = service.updateVehicleSegment(vehicle1, 50.0);
        
        assertNotNull(segment);
        assertEquals(segment, vehicle1.getCurrentSegment());
    }

    @Test
    @DisplayName("Test updateVehicleSegment - null vehicle")
    void testUpdateVehicleSegmentNullVehicle() {
        RouteSegment segment = service.updateVehicleSegment(null, 50.0);
        
        assertNull(segment);
    }

    @Test
    @DisplayName("Test updateVehicleSegment - null position")
    void testUpdateVehicleSegmentNullPosition() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPosition(null);
        
        RouteSegment segment = service.updateVehicleSegment(vehicle, 50.0);
        
        assertNull(segment);
    }

    @Test
    @DisplayName("Test checkSegmentCompliance - compliant vehicle")
    void testCheckSegmentComplianceCompliant() {
        RouteSegment segment = service.updateVehicleSegment(vehicle1, 50.0);
        assertNotNull(segment);
        
        boolean compliant = service.checkSegmentCompliance(vehicle1);
        
        assertTrue(compliant);
    }

    @Test
    @DisplayName("Test checkSegmentCompliance - altitude violation")
    void testCheckSegmentComplianceAltitudeViolation() {
        RouteSegment segment = service.updateVehicleSegment(vehicle1, 50.0);
        assertNotNull(segment);
        
        // Vehicle altitude far from segment altitude
        vehicle1.getPosition().setAltitude(150.0); // Segment is 100m
        
        boolean compliant = service.checkSegmentCompliance(vehicle1);
        
        assertFalse(compliant);
    }

    @Test
    @DisplayName("Test checkSegmentCompliance - speed violation")
    void testCheckSegmentComplianceSpeedViolation() {
        RouteSegment segment = service.updateVehicleSegment(vehicle1, 50.0);
        assertNotNull(segment);
        
        // Vehicle speed exceeds segment speed limit
        vehicle1.setVelocity(30.0); // Segment limit is 25 m/s, 30 > 25 * 1.1 = 27.5
        
        boolean compliant = service.checkSegmentCompliance(vehicle1);
        
        assertFalse(compliant);
    }

    @Test
    @DisplayName("Test checkSegmentCompliance - no segment")
    void testCheckSegmentComplianceNoSegment() {
        vehicle1.setCurrentSegment(null);
        
        boolean compliant = service.checkSegmentCompliance(vehicle1);
        
        assertTrue(compliant); // No segment means no compliance check
    }

    @Test
    @DisplayName("Test getVehicleCountForSegment")
    void testGetVehicleCountForSegment() {
        RouteSegment segment = service.updateVehicleSegment(vehicle1, 50.0);
        assertNotNull(segment);
        
        int count = service.getVehicleCountForSegment(segment.getSegmentId());
        
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Test isSegmentAtCapacity - not at capacity")
    void testIsSegmentAtCapacityNotAtCapacity() {
        RouteSegment segment = service.updateVehicleSegment(vehicle1, 50.0);
        assertNotNull(segment);
        segment.setMaxVehicles(10);
        
        boolean atCapacity = service.isSegmentAtCapacity(segment);
        
        assertFalse(atCapacity);
    }

    @Test
    @DisplayName("Test isSegmentAtCapacity - at capacity")
    void testIsSegmentAtCapacityAtCapacity() {
        RouteSegment segment = service.updateVehicleSegment(vehicle1, 50.0);
        assertNotNull(segment);
        segment.setMaxVehicles(1);
        
        boolean atCapacity = service.isSegmentAtCapacity(segment);
        
        assertTrue(atCapacity);
    }

    @Test
    @DisplayName("Test getVehiclesInSegment")
    void testGetVehiclesInSegment() {
        RouteSegment segment = service.updateVehicleSegment(vehicle1, 50.0);
        assertNotNull(segment);
        
        List<String> vehicles = service.getVehiclesInSegment(segment.getSegmentId());
        
        assertEquals(1, vehicles.size());
        assertTrue(vehicles.contains(vehicle1.getId()));
    }

    @Test
    @DisplayName("Test clearSegmentVehicles")
    void testClearSegmentVehicles() {
        RouteSegment segment = service.updateVehicleSegment(vehicle1, 50.0);
        assertNotNull(segment);
        
        service.clearSegmentVehicles();
        
        int count = service.getVehicleCountForSegment(segment.getSegmentId());
        assertEquals(0, count);
    }
}

