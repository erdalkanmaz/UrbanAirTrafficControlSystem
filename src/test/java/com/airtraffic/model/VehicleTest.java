package com.airtraffic.model;

import com.airtraffic.map.CityMap;
import com.airtraffic.map.Obstacle;
import com.airtraffic.map.ObstacleType;
import com.airtraffic.map.RestrictedZone;
import com.airtraffic.map.RestrictedZoneType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Vehicle class
 * Tests VTOL vehicle model, position updates, velocity management, and status handling
 */
@DisplayName("Vehicle Tests")
class VehicleTest {

    private Vehicle vehicle;
    private Position testPosition;

    @BeforeEach
    void setUp() {
        testPosition = new Position(41.0082, 28.9784, 100.0);
        vehicle = new Vehicle(VehicleType.CARGO, testPosition);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        Vehicle v = new Vehicle();
        
        assertNotNull(v.getId(), "Vehicle should have an ID");
        assertEquals(VehicleStatus.IDLE, v.getStatus(), "Default status should be IDLE");
        assertNotNull(v.getPosition(), "Vehicle should have a position");
        assertNotNull(v.getLastUpdateTime(), "Vehicle should have last update time");
        assertEquals(AutomationLevel.MANUAL, v.getAutomationLevel(), 
            "Default automation level should be MANUAL");
    }

    @Test
    @DisplayName("Test constructor with type and position")
    void testConstructorWithTypeAndPosition() {
        Vehicle v = new Vehicle(VehicleType.PASSENGER, testPosition);
        
        assertEquals(VehicleType.PASSENGER, v.getType());
        assertEquals(testPosition, v.getPosition());
        assertEquals(100.0, v.getAltitude(), 0.01);
    }

    @Test
    @DisplayName("Test position update")
    void testUpdatePosition() {
        Position newPosition = new Position(41.0090, 28.9790, 150.0);
        LocalDateTime beforeUpdate = vehicle.getLastUpdateTime();
        
        // Small delay to ensure timestamp difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        vehicle.updatePosition(newPosition);
        
        assertEquals(newPosition, vehicle.getPosition());
        assertEquals(150.0, vehicle.getAltitude(), 0.01);
        assertTrue(vehicle.getLastUpdateTime().isAfter(beforeUpdate) || 
                   vehicle.getLastUpdateTime().equals(beforeUpdate));
    }

    @Test
    @DisplayName("Test velocity update")
    void testUpdateVelocity() {
        vehicle.setMaxSpeed(50.0);
        vehicle.updateVelocity(30.0);
        
        assertEquals(30.0, vehicle.getVelocity(), 0.01);
    }

    @Test
    @DisplayName("Test velocity update with negative value")
    void testUpdateVelocityNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            vehicle.updateVelocity(-10.0);
        }, "Should throw exception for negative velocity");
    }

    @Test
    @DisplayName("Test velocity update exceeding max speed")
    void testUpdateVelocityExceedingMaxSpeed() {
        vehicle.setMaxSpeed(50.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            vehicle.updateVelocity(60.0);
        }, "Should throw exception when velocity exceeds max speed");
    }

    @Test
    @DisplayName("Test velocity update when max speed is zero")
    void testUpdateVelocityWithZeroMaxSpeed() {
        vehicle.setMaxSpeed(0.0);
        vehicle.updateVelocity(30.0);
        
        assertEquals(30.0, vehicle.getVelocity(), 0.01);
    }

    @Test
    @DisplayName("Test low fuel detection")
    void testLowFuel() {
        vehicle.setFuelLevel(25.0);
        assertFalse(vehicle.isLowFuel(), "25% fuel should not be low");
        
        vehicle.setFuelLevel(20.0);
        assertFalse(vehicle.isLowFuel(), "20% fuel should not be low (threshold is < 20%)");
        
        vehicle.setFuelLevel(19.0);
        assertTrue(vehicle.isLowFuel(), "19% fuel should be low");
        
        vehicle.setFuelLevel(10.0);
        assertTrue(vehicle.isLowFuel(), "10% fuel should be low");
        
        vehicle.setFuelLevel(0.0);
        assertTrue(vehicle.isLowFuel(), "0% fuel should be low");
    }

    @Test
    @DisplayName("Test emergency mode")
    void testEnterEmergencyMode() {
        vehicle.setStatus(VehicleStatus.IN_FLIGHT);
        LocalDateTime beforeEmergency = vehicle.getLastUpdateTime();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        vehicle.enterEmergencyMode();
        
        assertEquals(VehicleStatus.EMERGENCY, vehicle.getStatus());
        assertTrue(vehicle.getLastUpdateTime().isAfter(beforeEmergency) || 
                   vehicle.getLastUpdateTime().equals(beforeEmergency));
    }

    @Test
    @DisplayName("Test heading normalization")
    void testHeadingNormalization() {
        vehicle.setHeading(0.0);
        assertEquals(0.0, vehicle.getHeading(), 0.01);
        
        vehicle.setHeading(180.0);
        assertEquals(180.0, vehicle.getHeading(), 0.01);
        
        vehicle.setHeading(360.0);
        assertEquals(0.0, vehicle.getHeading(), 0.01);
        
        vehicle.setHeading(450.0);
        assertEquals(90.0, vehicle.getHeading(), 0.01);
        
        vehicle.setHeading(-90.0);
        assertEquals(270.0, vehicle.getHeading(), 0.01);
        
        vehicle.setHeading(-180.0);
        assertEquals(180.0, vehicle.getHeading(), 0.01);
    }

    @Test
    @DisplayName("Test altitude setter with max altitude constraint")
    void testSetAltitudeWithMaxAltitude() {
        vehicle.setMaxAltitude(500.0);
        
        vehicle.setAltitude(300.0);
        assertEquals(300.0, vehicle.getAltitude(), 0.01);
        
        assertThrows(IllegalArgumentException.class, () -> {
            vehicle.setAltitude(600.0);
        }, "Should throw exception when altitude exceeds max altitude");
    }

    @Test
    @DisplayName("Test altitude setter updates position")
    void testSetAltitudeUpdatesPosition() {
        vehicle.setAltitude(200.0);
        
        assertEquals(200.0, vehicle.getAltitude(), 0.01);
        assertEquals(200.0, vehicle.getPosition().getAltitude(), 0.01);
    }

    @Test
    @DisplayName("Test fuel level validation")
    void testFuelLevelValidation() {
        vehicle.setFuelLevel(50.0);
        assertEquals(50.0, vehicle.getFuelLevel(), 0.01);
        
        vehicle.setFuelLevel(0.0);
        assertEquals(0.0, vehicle.getFuelLevel(), 0.01);
        
        vehicle.setFuelLevel(100.0);
        assertEquals(100.0, vehicle.getFuelLevel(), 0.01);
        
        assertThrows(IllegalArgumentException.class, () -> {
            vehicle.setFuelLevel(-1.0);
        }, "Should throw exception for negative fuel level");
        
        assertThrows(IllegalArgumentException.class, () -> {
            vehicle.setFuelLevel(101.0);
        }, "Should throw exception for fuel level > 100%");
    }

    @Test
    @DisplayName("Test status setter updates timestamp")
    void testStatusSetterUpdatesTimestamp() {
        LocalDateTime beforeStatusChange = vehicle.getLastUpdateTime();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        vehicle.setStatus(VehicleStatus.IN_FLIGHT);
        
        assertEquals(VehicleStatus.IN_FLIGHT, vehicle.getStatus());
        assertTrue(vehicle.getLastUpdateTime().isAfter(beforeStatusChange) || 
                   vehicle.getLastUpdateTime().equals(beforeStatusChange));
    }

    @Test
    @DisplayName("Test position setter updates altitude")
    void testPositionSetterUpdatesAltitude() {
        Position newPosition = new Position(40.0, 30.0, 250.0);
        vehicle.setPosition(newPosition);
        
        assertEquals(newPosition, vehicle.getPosition());
        assertEquals(250.0, vehicle.getAltitude(), 0.01);
    }

    @Test
    @DisplayName("Test position setter with null position")
    void testPositionSetterWithNull() {
        vehicle.setPosition(null);
        assertNull(vehicle.getPosition());
    }

    @Test
    @DisplayName("Test all getters and setters")
    void testGettersAndSetters() {
        Vehicle v = new Vehicle();
        
        v.setId("TEST-001");
        v.setType(VehicleType.EMERGENCY);
        v.setVelocity(25.0);
        v.setHeading(90.0);
        v.setPilotLicense("PILOT-12345");
        v.setAutomationLevel(AutomationLevel.FULL_AUTO);
        v.setRegistrationNumber("REG-ABC123");
        v.setMaxSpeed(60.0);
        v.setMaxAltitude(1000.0);
        v.setWeight(500.0);
        v.setManufacturer("TestManufacturer");
        v.setModel("TestModel");
        
        assertEquals("TEST-001", v.getId());
        assertEquals(VehicleType.EMERGENCY, v.getType());
        assertEquals(25.0, v.getVelocity(), 0.01);
        assertEquals(90.0, v.getHeading(), 0.01);
        assertEquals("PILOT-12345", v.getPilotLicense());
        assertEquals(AutomationLevel.FULL_AUTO, v.getAutomationLevel());
        assertEquals("REG-ABC123", v.getRegistrationNumber());
        assertEquals(60.0, v.getMaxSpeed(), 0.01);
        assertEquals(1000.0, v.getMaxAltitude(), 0.01);
        assertEquals(500.0, v.getWeight(), 0.01);
        assertEquals("TestManufacturer", v.getManufacturer());
        assertEquals("TestModel", v.getModel());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        vehicle.setId("VEH-001");
        vehicle.setType(VehicleType.CARGO);
        vehicle.setStatus(VehicleStatus.IN_FLIGHT);
        vehicle.setVelocity(30.0);
        
        String str = vehicle.toString();
        
        assertTrue(str.contains("VEH-001"), "toString should contain vehicle ID");
        assertTrue(str.contains("CARGO"), "toString should contain vehicle type");
        assertTrue(str.contains("IN_FLIGHT"), "toString should contain status");
        // toString format: "Vehicle[id=VEH-001, type=CARGO, status=IN_FLIGHT, pos=Position(...), speed=30.00 m/s]"
        assertTrue(str.contains("30.00") || str.contains("speed=30"), "toString should contain velocity (actual: " + str + ")");
    }

    // ========== getCurrentLayer Tests ==========

    @Test
    @DisplayName("Test getCurrentLayer for LOW layer")
    void testGetCurrentLayerLowLayer() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        Position lowPosition = new Position(41.0082, 28.9784, 30.0); // 30m altitude
        Vehicle lowVehicle = new Vehicle(VehicleType.CARGO, lowPosition);
        
        AltitudeLayer layer = lowVehicle.getCurrentLayer(cityMap);
        
        assertEquals(AltitudeLayer.LAYER_1_LOW, layer, 
            "Vehicle at 30m should be in LOW layer");
    }

    @Test
    @DisplayName("Test getCurrentLayer for MEDIUM layer")
    void testGetCurrentLayerMediumLayer() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        Position mediumPosition = new Position(41.0082, 28.9784, 90.0); // 90m altitude
        Vehicle mediumVehicle = new Vehicle(VehicleType.PASSENGER, mediumPosition);
        
        AltitudeLayer layer = mediumVehicle.getCurrentLayer(cityMap);
        
        assertEquals(AltitudeLayer.LAYER_2_MEDIUM, layer, 
            "Vehicle at 90m should be in MEDIUM layer");
    }

    @Test
    @DisplayName("Test getCurrentLayer for HIGH layer")
    void testGetCurrentLayerHighLayer() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        Position highPosition = new Position(41.0082, 28.9784, 150.0); // 150m altitude
        Vehicle highVehicle = new Vehicle(VehicleType.EMERGENCY, highPosition);
        
        AltitudeLayer layer = highVehicle.getCurrentLayer(cityMap);
        
        assertEquals(AltitudeLayer.LAYER_3_HIGH, layer, 
            "Vehicle at 150m should be in HIGH layer");
    }

    @Test
    @DisplayName("Test getCurrentLayer returns null for vehicle outside boundaries")
    void testGetCurrentLayerOutsideBoundaries() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.5);
        cityMap.setMaxLatitude(41.5);
        cityMap.setMinLongitude(28.5);
        cityMap.setMaxLongitude(29.5);
        
        Position outsidePosition = new Position(40.0, 30.0, 100.0); // Outside boundaries
        Vehicle outsideVehicle = new Vehicle(VehicleType.CARGO, outsidePosition);
        
        AltitudeLayer layer = outsideVehicle.getCurrentLayer(cityMap);
        
        assertNull(layer, "Vehicle outside boundaries should return null");
    }

    @Test
    @DisplayName("Test getCurrentLayer returns null for vehicle at obstacle")
    void testGetCurrentLayerAtObstacle() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        Position obstaclePosition = new Position(41.0082, 28.9784, 50.0);
        Obstacle obstacle = new Obstacle("Test Building", ObstacleType.BUILDING, 
            obstaclePosition, 50.0); // 50m high building
        obstacle.setRadius(100.0);
        cityMap.addObstacle(obstacle);
        
        // Vehicle at obstacle (same location, altitude below obstacle top)
        Position vehiclePosition = new Position(41.0082, 28.9784, 80.0); // Below obstacle top (50+50=100m)
        Vehicle vehicleAtObstacle = new Vehicle(VehicleType.CARGO, vehiclePosition);
        
        AltitudeLayer layer = vehicleAtObstacle.getCurrentLayer(cityMap);
        
        assertNull(layer, "Vehicle at obstacle should return null");
    }

    @Test
    @DisplayName("Test getCurrentLayer returns layer for vehicle above obstacle")
    void testGetCurrentLayerAboveObstacle() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        Position obstaclePosition = new Position(41.0082, 28.9784, 50.0);
        Obstacle obstacle = new Obstacle("Test Building", ObstacleType.BUILDING, 
            obstaclePosition, 50.0); // 50m high building (top at 100m)
        obstacle.setRadius(100.0);
        cityMap.addObstacle(obstacle);
        
        // Vehicle above obstacle (same horizontal location, altitude above obstacle top)
        Position vehiclePosition = new Position(41.0082, 28.9784, 110.0); // Above obstacle top
        Vehicle vehicleAboveObstacle = new Vehicle(VehicleType.PASSENGER, vehiclePosition);
        
        AltitudeLayer layer = vehicleAboveObstacle.getCurrentLayer(cityMap);
        
        assertEquals(AltitudeLayer.LAYER_2_MEDIUM, layer, 
            "Vehicle above obstacle should return appropriate layer");
    }

    @Test
    @DisplayName("Test getCurrentLayer returns null for vehicle in restricted zone")
    void testGetCurrentLayerInRestrictedZone() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        RestrictedZone zone = new RestrictedZone("Test Zone", RestrictedZoneType.GOVERNMENT);
        zone.setMinAltitude(0.0);
        zone.setMaxAltitude(200.0);
        zone.addBoundaryPoint(new Position(41.0050, 28.9750, 0.0));
        zone.addBoundaryPoint(new Position(41.0100, 28.9750, 0.0));
        zone.addBoundaryPoint(new Position(41.0100, 28.9800, 0.0));
        zone.addBoundaryPoint(new Position(41.0050, 28.9800, 0.0));
        cityMap.addRestrictedZone(zone);
        
        Position vehiclePosition = new Position(41.0075, 28.9775, 100.0);
        Vehicle vehicleInZone = new Vehicle(VehicleType.CARGO, vehiclePosition);
        
        AltitudeLayer layer = vehicleInZone.getCurrentLayer(cityMap);
        
        assertNull(layer, "Vehicle in restricted zone should return null");
    }

    @Test
    @DisplayName("Test getCurrentLayer with null CityMap")
    void testGetCurrentLayerNullCityMap() {
        assertThrows(NullPointerException.class, () -> {
            vehicle.getCurrentLayer(null);
        }, "Null CityMap should throw NullPointerException");
    }

    @Test
    @DisplayName("Test getCurrentLayer with null position")
    void testGetCurrentLayerNullPosition() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        Vehicle vehicleWithNullPosition = new Vehicle();
        vehicleWithNullPosition.setPosition(null);
        
        assertThrows(NullPointerException.class, () -> {
            vehicleWithNullPosition.getCurrentLayer(cityMap);
        }, "Vehicle with null position should throw NullPointerException");
    }
}







