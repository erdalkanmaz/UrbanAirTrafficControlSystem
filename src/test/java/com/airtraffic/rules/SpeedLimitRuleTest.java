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
 * Unit tests for SpeedLimitRule class
 * Tests speed limit rule violations, warnings, and speed validation
 */
@DisplayName("SpeedLimitRule Tests")
class SpeedLimitRuleTest {

    private SpeedLimitRule rule;
    private Vehicle testVehicle;
    private Position testPosition;

    @BeforeEach
    void setUp() {
        rule = new SpeedLimitRule("Test Speed Limit", 20.0); // 20 m/s limit
        testPosition = new Position(41.0082, 28.9784, 100.0);
        testVehicle = new Vehicle(VehicleType.PASSENGER, testPosition);
        testVehicle.setVelocity(15.0);
        testVehicle.setStatus(VehicleStatus.IN_FLIGHT);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        SpeedLimitRule r = new SpeedLimitRule();
        
        assertNotNull(r.getId());
        assertEquals(RuleType.SPEED_LIMIT, r.getRuleType());
        assertEquals(5.0, r.getTolerance(), 0.01);
        assertTrue(r.isActive());
    }

    @Test
    @DisplayName("Test constructor with name and max speed")
    void testConstructorWithNameAndMaxSpeed() {
        SpeedLimitRule r = new SpeedLimitRule("Highway Speed", 30.0);
        
        assertEquals("Highway Speed", r.getName());
        assertEquals(30.0, r.getMaxSpeed(), 0.01);
        assertEquals(RuleType.SPEED_LIMIT, r.getRuleType());
    }

    @Test
    @DisplayName("Test isViolated with speed within limit")
    void testIsViolatedWithinLimit() {
        testVehicle.setVelocity(15.0); // Below 20 m/s limit
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate when speed is within limit");
    }

    @Test
    @DisplayName("Test isViolated with speed exceeding limit")
    void testIsViolatedExceedingLimit() {
        testVehicle.setVelocity(25.0); // Above 20 m/s limit
        
        assertTrue(rule.isViolated(testVehicle, testPosition), 
            "Should violate when speed exceeds limit");
    }

    @Test
    @DisplayName("Test isViolated with speed exactly at limit")
    void testIsViolatedAtLimit() {
        testVehicle.setVelocity(20.0); // Exactly at limit
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate when speed is exactly at limit");
    }

    @Test
    @DisplayName("Test isViolated with minimum speed requirement")
    void testIsViolatedMinimumSpeed() {
        rule.setMinSpeed(10.0);
        testVehicle.setVelocity(5.0); // Below minimum
        testVehicle.setStatus(VehicleStatus.IN_FLIGHT); // In flight status
        
        assertTrue(rule.isViolated(testVehicle, testPosition), 
            "Should violate when speed is below minimum and in flight");
    }

    @Test
    @DisplayName("Test isViolated with minimum speed but not in flight")
    void testIsViolatedMinimumSpeedNotInFlight() {
        rule.setMinSpeed(10.0);
        testVehicle.setVelocity(5.0);
        testVehicle.setStatus(VehicleStatus.IDLE); // Not in flight
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate minimum speed when not in flight");
    }

    @Test
    @DisplayName("Test isViolated with inactive rule")
    void testIsViolatedInactiveRule() {
        rule.setActive(false);
        testVehicle.setVelocity(25.0); // Would normally violate
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate when rule is inactive");
    }

    @Test
    @DisplayName("Test isWarningNeeded when approaching limit")
    void testIsWarningNeededApproachingLimit() {
        rule.setTolerance(5.0);
        // Vehicle at 16 m/s (within 5 m/s tolerance of 20 m/s limit)
        testVehicle.setVelocity(16.0);
        
        assertTrue(rule.isWarningNeeded(testVehicle), 
            "Should warn when approaching speed limit");
    }

    @Test
    @DisplayName("Test isWarningNeeded when at limit")
    void testIsWarningNeededAtLimit() {
        rule.setTolerance(5.0);
        testVehicle.setVelocity(20.0); // Exactly at limit
        
        assertTrue(rule.isWarningNeeded(testVehicle), 
            "Should warn when at speed limit");
    }

    @Test
    @DisplayName("Test isWarningNeeded when well below limit")
    void testIsWarningNeededWellBelowLimit() {
        rule.setTolerance(5.0);
        testVehicle.setVelocity(10.0); // Well below limit
        
        assertFalse(rule.isWarningNeeded(testVehicle), 
            "Should not warn when well below limit");
    }

    @Test
    @DisplayName("Test isWarningNeeded when exceeding limit")
    void testIsWarningNeededExceedingLimit() {
        rule.setTolerance(5.0);
        testVehicle.setVelocity(25.0); // Exceeding limit
        
        assertFalse(rule.isWarningNeeded(testVehicle), 
            "Should not warn when exceeding limit (should be violation)");
    }

    @Test
    @DisplayName("Test isWarningNeeded with zero max speed")
    void testIsWarningNeededZeroMaxSpeed() {
        rule.setMaxSpeed(0.0);
        testVehicle.setVelocity(10.0);
        
        assertFalse(rule.isWarningNeeded(testVehicle), 
            "Should not warn when max speed is zero");
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        rule.setMaxSpeed(25.0);
        rule.setMinSpeed(5.0);
        rule.setTolerance(3.0);
        
        assertEquals(25.0, rule.getMaxSpeed(), 0.01);
        assertEquals(5.0, rule.getMinSpeed(), 0.01);
        assertEquals(3.0, rule.getTolerance(), 0.01);
    }

    @Test
    @DisplayName("Test setMaxSpeed with negative value")
    void testSetMaxSpeedNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            rule.setMaxSpeed(-10.0);
        }, "Should throw exception for negative max speed");
    }

    @Test
    @DisplayName("Test setMaxSpeed with zero")
    void testSetMaxSpeedZero() {
        rule.setMaxSpeed(0.0);
        assertEquals(0.0, rule.getMaxSpeed(), 0.01, 
            "Should allow zero max speed");
    }

    @Test
    @DisplayName("Test edge case: very high speed")
    void testVeryHighSpeed() {
        testVehicle.setVelocity(100.0); // Very high speed
        
        assertTrue(rule.isViolated(testVehicle, testPosition), 
            "Should violate with very high speed");
    }

    @Test
    @DisplayName("Test edge case: zero speed")
    void testZeroSpeed() {
        testVehicle.setVelocity(0.0);
        
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Should not violate with zero speed (unless min speed is set)");
    }

    @Test
    @DisplayName("Test edge case: very small tolerance")
    void testVerySmallTolerance() {
        rule.setTolerance(0.1);
        // Vehicle at 19.95 m/s (within 0.1 tolerance of 20 m/s limit)
        // maxSpeed - tolerance = 20.0 - 0.1 = 19.9
        // So we need speed > 19.9 and <= 20.0
        testVehicle.setVelocity(19.95); // Within tolerance range
        
        assertTrue(rule.isWarningNeeded(testVehicle), 
            "Should warn with very small tolerance when close to limit (actual: " + testVehicle.getVelocity() + ")");
    }

    @Test
    @DisplayName("Test edge case: very large tolerance")
    void testVeryLargeTolerance() {
        rule.setTolerance(10.0);
        testVehicle.setVelocity(12.0); // Well below limit but within large tolerance
        
        assertTrue(rule.isWarningNeeded(testVehicle), 
            "Should warn with large tolerance when within range");
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
}

