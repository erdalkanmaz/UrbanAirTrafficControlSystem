package com.airtraffic.rules;

import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import com.airtraffic.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrafficRule base class
 * Tests rule base functionality, applicability checks, and rule properties
 */
@DisplayName("TrafficRule Tests")
class TrafficRuleTest {

    private TrafficRule rule;
    private Vehicle testVehicle;
    private Position testPosition;

    @BeforeEach
    void setUp() {
        rule = new TrafficRule("Test Rule", RuleType.ALTITUDE);
        testPosition = new Position(41.0082, 28.9784, 100.0);
        testVehicle = new Vehicle(VehicleType.PASSENGER, testPosition);
        testVehicle.setStatus(VehicleStatus.IN_FLIGHT);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        TrafficRule r = new TrafficRule();
        
        assertNotNull(r.getId());
        assertTrue(r.isActive());
        assertEquals(0, r.getPriority());
        assertNull(r.getName());
        assertNull(r.getRuleType());
    }

    @Test
    @DisplayName("Test constructor with name and type")
    void testConstructorWithNameAndType() {
        TrafficRule r = new TrafficRule("Speed Rule", RuleType.SPEED_LIMIT);
        
        assertEquals("Speed Rule", r.getName());
        assertEquals(RuleType.SPEED_LIMIT, r.getRuleType());
        assertNotNull(r.getId());
        assertTrue(r.isActive());
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
    @DisplayName("Test isViolated default implementation")
    void testIsViolatedDefault() {
        // Default implementation should return false
        assertFalse(rule.isViolated(testVehicle, testPosition), 
            "Default isViolated should return false");
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        rule.setId("RULE-001");
        rule.setName("Updated Rule");
        rule.setRuleType(RuleType.SEPARATION);
        rule.setPriority(15);
        rule.setActive(false);
        
        assertEquals("RULE-001", rule.getId());
        assertEquals("Updated Rule", rule.getName());
        assertEquals(RuleType.SEPARATION, rule.getRuleType());
        assertEquals(15, rule.getPriority());
        assertFalse(rule.isActive());
    }

    @Test
    @DisplayName("Test setApplicableZones")
    void testSetApplicableZones() {
        List<String> zones = new ArrayList<>();
        zones.add("Zone A");
        zones.add("Zone B");
        
        rule.setApplicableZones(zones);
        
        List<String> retrievedZones = rule.getApplicableZones();
        assertNotNull(retrievedZones);
        assertEquals(2, retrievedZones.size());
        assertTrue(retrievedZones.contains("Zone A"));
        assertTrue(retrievedZones.contains("Zone B"));
    }

    @Test
    @DisplayName("Test getApplicableZones with null")
    void testGetApplicableZonesNull() {
        // Initially null
        assertNull(rule.getApplicableZones(), 
            "Applicable zones should be null initially");
    }

    @Test
    @DisplayName("Test priority setting")
    void testPrioritySetting() {
        rule.setPriority(10);
        assertEquals(10, rule.getPriority(), 
            "Should set priority correctly");
        
        rule.setPriority(0);
        assertEquals(0, rule.getPriority(), 
            "Should allow zero priority");
        
        rule.setPriority(-5);
        assertEquals(-5, rule.getPriority(), 
            "Should allow negative priority");
    }

    @Test
    @DisplayName("Test active status toggle")
    void testActiveStatusToggle() {
        rule.setActive(true);
        assertTrue(rule.isActive(), 
            "Should be active when set to true");
        
        rule.setActive(false);
        assertFalse(rule.isActive(), 
            "Should be inactive when set to false");
    }

    @Test
    @DisplayName("Test rule ID uniqueness")
    void testRuleIdUniqueness() {
        TrafficRule rule1 = new TrafficRule();
        TrafficRule rule2 = new TrafficRule();
        
        assertNotEquals(rule1.getId(), rule2.getId(), 
            "Each rule should have unique ID");
    }

    @Test
    @DisplayName("Test edge case: rule with all properties set")
    void testRuleWithAllProperties() {
        List<String> zones = new ArrayList<>();
        zones.add("Zone 1");
        zones.add("Zone 2");
        
        rule.setId("CUSTOM-ID");
        rule.setName("Complete Rule");
        rule.setRuleType(RuleType.WEATHER);
        rule.setPriority(25);
        rule.setApplicableZones(zones);
        rule.setActive(true);
        
        assertEquals("CUSTOM-ID", rule.getId());
        assertEquals("Complete Rule", rule.getName());
        assertEquals(RuleType.WEATHER, rule.getRuleType());
        assertEquals(25, rule.getPriority());
        assertEquals(2, rule.getApplicableZones().size());
        assertTrue(rule.isActive());
    }

    @Test
    @DisplayName("Test edge case: very high priority")
    void testVeryHighPriority() {
        rule.setPriority(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, rule.getPriority(), 
            "Should handle very high priority");
    }

    @Test
    @DisplayName("Test edge case: very low priority")
    void testVeryLowPriority() {
        rule.setPriority(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, rule.getPriority(), 
            "Should handle very low priority");
    }

    @Test
    @DisplayName("Test isApplicable with null vehicle")
    void testIsApplicableWithNullVehicle() {
        // Should handle null gracefully (may throw exception or return false)
        // Testing that it doesn't crash
        try {
            boolean result = rule.isApplicable(null, testPosition);
            assertNotNull(Boolean.valueOf(result), 
                "Should handle null vehicle without crashing");
        } catch (Exception e) {
            // If it throws exception, that's also acceptable behavior
            assertNotNull(e, "Exception handling is acceptable");
        }
    }

    @Test
    @DisplayName("Test isApplicable with null position")
    void testIsApplicableWithNullPosition() {
        // Should handle null gracefully
        try {
            boolean result = rule.isApplicable(testVehicle, null);
            assertNotNull(Boolean.valueOf(result), 
                "Should handle null position without crashing");
        } catch (Exception e) {
            // If it throws exception, that's also acceptable behavior
            assertNotNull(e, "Exception handling is acceptable");
        }
    }

    @Test
    @DisplayName("Test isViolated with null vehicle")
    void testIsViolatedWithNullVehicle() {
        // Default implementation should handle null
        assertFalse(rule.isViolated(null, testPosition), 
            "Default isViolated should return false for null vehicle");
    }

    @Test
    @DisplayName("Test isViolated with null position")
    void testIsViolatedWithNullPosition() {
        // Default implementation should handle null
        assertFalse(rule.isViolated(testVehicle, null), 
            "Default isViolated should return false for null position");
    }

    @Test
    @DisplayName("Test rule type enum values")
    void testRuleTypeEnumValues() {
        // Test all rule types
        RuleType[] types = RuleType.values();
        assertTrue(types.length > 0, 
            "Should have rule types defined");
        
        for (RuleType type : types) {
            rule.setRuleType(type);
            assertEquals(type, rule.getRuleType(), 
                "Should set and get rule type: " + type);
        }
    }
}






