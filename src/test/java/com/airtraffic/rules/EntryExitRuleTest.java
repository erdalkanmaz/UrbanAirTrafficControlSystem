package com.airtraffic.rules;

import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import com.airtraffic.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EntryExitRule class
 * Tests entry/exit rule violations, altitude calculations, and speed limits
 */
@DisplayName("EntryExitRule Tests")
class EntryExitRuleTest {

    private EntryExitRule rule;
    private Vehicle testVehicle;
    private Position testPosition;

    @BeforeEach
    void setUp() {
        rule = new EntryExitRule("Test Entry/Exit Rule");
        testPosition = new Position(41.0082, 28.9784, 100.0);
        testVehicle = new Vehicle(VehicleType.PASSENGER, testPosition);
        testVehicle.setVelocity(3.0);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        EntryExitRule r = new EntryExitRule();
        
        assertNotNull(r.getId());
        assertEquals(RuleType.ENTRY_EXIT, r.getRuleType());
        assertEquals(10.0, r.getEntryAltitudeOffset(), 0.01);
        assertEquals(10.0, r.getExitAltitudeOffset(), 0.01);
        assertEquals(5.0, r.getEntrySpeedLimit(), 0.01);
        assertEquals(3.0, r.getExitSpeedLimit(), 0.01);
        assertTrue(r.isActive());
    }

    @Test
    @DisplayName("Test constructor with name")
    void testConstructorWithName() {
        EntryExitRule r = new EntryExitRule("Custom Entry/Exit");
        
        assertEquals("Custom Entry/Exit", r.getName());
        assertEquals(RuleType.ENTRY_EXIT, r.getRuleType());
    }

    @Test
    @DisplayName("Test isViolated with entry speed violation")
    void testIsViolatedEntrySpeedViolation() {
        testVehicle.setStatus(VehicleStatus.TAKING_OFF);
        testVehicle.setVelocity(10.0); // Above entry speed limit (5.0 m/s)
        
        assertTrue(rule.isViolated(testVehicle, testPosition), 
            "Should violate when entry speed exceeds limit");
    }

    @Test
    @DisplayName("Test isViolated with entry speed within limit")
    void testIsViolatedEntrySpeedWithinLimit() {
        testVehicle.setStatus(VehicleStatus.TAKING_OFF);
        testVehicle.setVelocity(4.0); // Within entry speed limit
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate when entry speed is within limit");
    }

    @Test
    @DisplayName("Test isViolated with exit speed violation")
    void testIsViolatedExitSpeedViolation() {
        testVehicle.setStatus(VehicleStatus.LANDING);
        testVehicle.setVelocity(5.0); // Above exit speed limit (3.0 m/s)
        
        assertTrue(rule.isViolated(testVehicle, testPosition), 
            "Should violate when exit speed exceeds limit");
    }

    @Test
    @DisplayName("Test isViolated with exit speed within limit")
    void testIsViolatedExitSpeedWithinLimit() {
        testVehicle.setStatus(VehicleStatus.LANDING);
        testVehicle.setVelocity(2.0); // Within exit speed limit
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate when exit speed is within limit");
    }

    @Test
    @DisplayName("Test isViolated with PREPARING status")
    void testIsViolatedPreparingStatus() {
        testVehicle.setStatus(VehicleStatus.PREPARING);
        testVehicle.setVelocity(10.0); // Above entry speed limit
        
        assertTrue(rule.isViolated(testVehicle, testPosition), 
            "Should violate when PREPARING status with high speed");
    }

    @Test
    @DisplayName("Test isViolated with IN_FLIGHT status")
    void testIsViolatedInFlightStatus() {
        testVehicle.setStatus(VehicleStatus.IN_FLIGHT);
        testVehicle.setVelocity(10.0); // High speed, but not entry/exit
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate when IN_FLIGHT (not entry/exit)");
    }

    @Test
    @DisplayName("Test isViolated with inactive rule")
    void testIsViolatedInactiveRule() {
        rule.setActive(false);
        testVehicle.setStatus(VehicleStatus.TAKING_OFF);
        testVehicle.setVelocity(10.0); // Would normally violate
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate when rule is inactive");
    }

    @Test
    @DisplayName("Test getEntryAltitude")
    void testGetEntryAltitude() {
        double trafficAltitude = 100.0;
        double entryAltitude = rule.getEntryAltitude(trafficAltitude);
        
        // Should be traffic altitude + entry offset (10.0)
        assertEquals(110.0, entryAltitude, 0.01, 
            "Entry altitude should be traffic altitude + offset");
    }

    @Test
    @DisplayName("Test getExitAltitude")
    void testGetExitAltitude() {
        double trafficAltitude = 100.0;
        double exitAltitude = rule.getExitAltitude(trafficAltitude);
        
        // Should be traffic altitude - exit offset (10.0)
        assertEquals(90.0, exitAltitude, 0.01, 
            "Exit altitude should be traffic altitude - offset");
    }

    @Test
    @DisplayName("Test getEntryAltitude with different offsets")
    void testGetEntryAltitudeDifferentOffset() {
        rule.setEntryAltitudeOffset(20.0);
        double trafficAltitude = 100.0;
        
        double entryAltitude = rule.getEntryAltitude(trafficAltitude);
        assertEquals(120.0, entryAltitude, 0.01, 
            "Entry altitude should use custom offset");
    }

    @Test
    @DisplayName("Test getExitAltitude with different offsets")
    void testGetExitAltitudeDifferentOffset() {
        rule.setExitAltitudeOffset(15.0);
        double trafficAltitude = 100.0;
        
        double exitAltitude = rule.getExitAltitude(trafficAltitude);
        assertEquals(85.0, exitAltitude, 0.01, 
            "Exit altitude should use custom offset");
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        rule.setEntryAltitudeOffset(15.0);
        rule.setExitAltitudeOffset(12.0);
        rule.setEntrySpeedLimit(6.0);
        rule.setExitSpeedLimit(4.0);
        
        assertEquals(15.0, rule.getEntryAltitudeOffset(), 0.01);
        assertEquals(12.0, rule.getExitAltitudeOffset(), 0.01);
        assertEquals(6.0, rule.getEntrySpeedLimit(), 0.01);
        assertEquals(4.0, rule.getExitSpeedLimit(), 0.01);
    }

    @Test
    @DisplayName("Test edge case: zero traffic altitude")
    void testZeroTrafficAltitude() {
        double entryAltitude = rule.getEntryAltitude(0.0);
        assertEquals(10.0, entryAltitude, 0.01, 
            "Entry altitude should handle zero traffic altitude");
        
        double exitAltitude = rule.getExitAltitude(0.0);
        assertEquals(-10.0, exitAltitude, 0.01, 
            "Exit altitude should handle zero traffic altitude (may be negative)");
    }

    @Test
    @DisplayName("Test edge case: very high traffic altitude")
    void testVeryHighTrafficAltitude() {
        double trafficAltitude = 10000.0;
        double entryAltitude = rule.getEntryAltitude(trafficAltitude);
        assertEquals(10010.0, entryAltitude, 0.01, 
            "Entry altitude should handle very high traffic altitude");
    }

    @Test
    @DisplayName("Test edge case: entry speed exactly at limit")
    void testEntrySpeedAtLimit() {
        testVehicle.setStatus(VehicleStatus.TAKING_OFF);
        testVehicle.setVelocity(5.0); // Exactly at entry speed limit
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate when entry speed is exactly at limit");
    }

    @Test
    @DisplayName("Test edge case: exit speed exactly at limit")
    void testExitSpeedAtLimit() {
        testVehicle.setStatus(VehicleStatus.LANDING);
        testVehicle.setVelocity(3.0); // Exactly at exit speed limit
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate when exit speed is exactly at limit");
    }

    @Test
    @DisplayName("Test edge case: zero entry speed")
    void testZeroEntrySpeed() {
        testVehicle.setStatus(VehicleStatus.TAKING_OFF);
        testVehicle.setVelocity(0.0);
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate with zero entry speed");
    }

    @Test
    @DisplayName("Test edge case: zero exit speed")
    void testZeroExitSpeed() {
        testVehicle.setStatus(VehicleStatus.LANDING);
        testVehicle.setVelocity(0.0);
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate with zero exit speed");
    }

    @Test
    @DisplayName("Test isApplicable with active rule")
    void testIsApplicableActive() {
        rule.setActive(true);
        
        assertTrue(rule.isApplicable(testVehicle, testPosition), 
            "Should be applicable when rule is active");
    }

    @Test
    @DisplayName("Test isApplicable with inactive rule")
    void testIsApplicableInactive() {
        rule.setActive(false);
        
        assertFalse(rule.isApplicable(testVehicle, testPosition), 
            "Should not be applicable when rule is inactive");
    }

    @Test
    @DisplayName("Test with different vehicle statuses")
    void testDifferentVehicleStatuses() {
        // Test with various statuses
        VehicleStatus[] nonEntryExitStatuses = {
            VehicleStatus.IDLE, VehicleStatus.IN_FLIGHT, 
            VehicleStatus.PARKED, VehicleStatus.MAINTENANCE, VehicleStatus.EMERGENCY
        };
        
        for (VehicleStatus status : nonEntryExitStatuses) {
            testVehicle.setStatus(status);
            testVehicle.setVelocity(10.0);
            
            assertFalse(rule.isViolated(testVehicle, testPosition), 
                "Should not violate for status: " + status);
        }
    }
}






