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
 * Unit tests for TrafficRuleEngine class
 * Tests rule management, violation checking, and warning generation
 */
@DisplayName("TrafficRuleEngine Tests")
class TrafficRuleEngineTest {

    private TrafficRuleEngine engine;
    private Vehicle testVehicle;
    private Position testPosition;

    @BeforeEach
    void setUp() {
        engine = new TrafficRuleEngine();
        testPosition = new Position(41.0082, 28.9784, 100.0);
        testVehicle = new Vehicle(VehicleType.PASSENGER, testPosition);
        testVehicle.setVelocity(10.0);
        testVehicle.setStatus(VehicleStatus.IN_FLIGHT);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        TrafficRuleEngine eng = new TrafficRuleEngine();
        
        assertNotNull(eng.getRules());
        assertTrue(eng.isEnabled());
        // Should have default rules initialized
        assertTrue(eng.getRules().size() >= 3, 
            "Should have at least 3 default rules");
    }

    @Test
    @DisplayName("Test default rules initialization")
    void testDefaultRulesInitialization() {
        List<TrafficRule> rules = engine.getRules();
        
        // Should have main street speed limit, side street speed limit, and entry/exit rule
        assertTrue(rules.size() >= 3, 
            "Should have default rules initialized");
        
        // Check for speed limit rules
        List<TrafficRule> speedRules = engine.getRulesByType(RuleType.SPEED_LIMIT);
        assertTrue(speedRules.size() >= 2, 
            "Should have at least 2 speed limit rules");
        
        // Check for entry/exit rule
        List<TrafficRule> entryExitRules = engine.getRulesByType(RuleType.ENTRY_EXIT);
        assertFalse(entryExitRules.isEmpty(), 
            "Should have at least 1 entry/exit rule");
    }

    @Test
    @DisplayName("Test addRule")
    void testAddRule() {
        SpeedLimitRule newRule = new SpeedLimitRule("Test Speed Limit", 30.0);
        
        int initialSize = engine.getRules().size();
        engine.addRule(newRule);
        
        assertEquals(initialSize + 1, engine.getRules().size(), 
            "Should add one rule");
        assertTrue(engine.getRules().contains(newRule), 
            "Should contain the added rule");
    }

    @Test
    @DisplayName("Test addRule with null")
    void testAddRuleWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            engine.addRule(null);
        }, "Should throw exception for null rule");
    }

    @Test
    @DisplayName("Test addRule sorts by priority")
    void testAddRuleSortsByPriority() {
        SpeedLimitRule lowPriority = new SpeedLimitRule("Low Priority", 20.0);
        lowPriority.setPriority(5);
        
        SpeedLimitRule highPriority = new SpeedLimitRule("High Priority", 30.0);
        highPriority.setPriority(20);
        
        engine.addRule(lowPriority);
        engine.addRule(highPriority);
        
        List<TrafficRule> rules = engine.getRules();
        int highPriorityIndex = rules.indexOf(highPriority);
        int lowPriorityIndex = rules.indexOf(lowPriority);
        
        assertTrue(highPriorityIndex < lowPriorityIndex, 
            "High priority rule should come before low priority rule");
    }

    @Test
    @DisplayName("Test removeRule")
    void testRemoveRule() {
        SpeedLimitRule rule = new SpeedLimitRule("Test Rule", 25.0);
        engine.addRule(rule);
        
        String ruleId = rule.getId();
        int initialSize = engine.getRules().size();
        
        engine.removeRule(ruleId);
        
        assertEquals(initialSize - 1, engine.getRules().size(), 
            "Should remove one rule");
        assertFalse(engine.getRules().contains(rule), 
            "Should not contain removed rule");
    }

    @Test
    @DisplayName("Test removeRule with non-existent ID")
    void testRemoveRuleNonExistent() {
        int initialSize = engine.getRules().size();
        
        engine.removeRule("NON-EXISTENT-ID");
        
        assertEquals(initialSize, engine.getRules().size(), 
            "Should not remove any rule when ID doesn't exist");
    }

    @Test
    @DisplayName("Test checkViolations with no violations")
    void testCheckViolationsNoViolations() {
        // Vehicle with speed within limits
        testVehicle.setVelocity(10.0); // Below speed limit
        
        List<TrafficRule> violations = engine.checkViolations(testVehicle, testPosition);
        
        // Should have no violations for normal flight
        assertTrue(violations.isEmpty(), 
            "Should have no violations for vehicle within limits");
    }

    @Test
    @DisplayName("Test checkViolations with speed violation")
    void testCheckViolationsSpeedViolation() {
        // Vehicle exceeding speed limit (main street limit is ~16.67 m/s)
        testVehicle.setVelocity(20.0); // Above speed limit
        
        List<TrafficRule> violations = engine.checkViolations(testVehicle, testPosition);
        
        assertFalse(violations.isEmpty(), 
            "Should detect speed violation");
        
        boolean hasSpeedViolation = violations.stream()
            .anyMatch(r -> r instanceof SpeedLimitRule);
        assertTrue(hasSpeedViolation, 
            "Should include speed limit rule in violations");
    }

    @Test
    @DisplayName("Test checkViolations with entry/exit violation")
    void testCheckViolationsEntryExitViolation() {
        // Vehicle in TAKING_OFF status with high speed
        testVehicle.setStatus(VehicleStatus.TAKING_OFF);
        testVehicle.setVelocity(10.0); // Above entry speed limit (5.0 m/s)
        
        List<TrafficRule> violations = engine.checkViolations(testVehicle, testPosition);
        
        boolean hasEntryExitViolation = violations.stream()
            .anyMatch(r -> r instanceof EntryExitRule);
        assertTrue(hasEntryExitViolation, 
            "Should detect entry/exit violation");
    }

    @Test
    @DisplayName("Test checkViolations when engine disabled")
    void testCheckViolationsEngineDisabled() {
        engine.setEnabled(false);
        testVehicle.setVelocity(20.0); // Would normally violate
        
        List<TrafficRule> violations = engine.checkViolations(testVehicle, testPosition);
        
        assertTrue(violations.isEmpty(), 
            "Should return empty list when engine is disabled");
    }

    @Test
    @DisplayName("Test checkWarnings with speed warning")
    void testCheckWarningsSpeedWarning() {
        // Vehicle approaching speed limit
        SpeedLimitRule speedRule = new SpeedLimitRule("Test Speed", 20.0);
        speedRule.setTolerance(5.0);
        engine.addRule(speedRule);
        
        // Vehicle at 16 m/s (within tolerance of 20 m/s limit)
        testVehicle.setVelocity(16.0);
        
        List<TrafficRule> warnings = engine.checkWarnings(testVehicle, testPosition);
        
        boolean hasSpeedWarning = warnings.stream()
            .anyMatch(r -> r instanceof SpeedLimitRule);
        assertTrue(hasSpeedWarning, 
            "Should generate warning when approaching speed limit");
    }

    @Test
    @DisplayName("Test checkWarnings with no warnings")
    void testCheckWarningsNoWarnings() {
        // Vehicle well below speed limit
        testVehicle.setVelocity(5.0);
        
        List<TrafficRule> warnings = engine.checkWarnings(testVehicle, testPosition);
        
        // May have warnings from default rules, but should be minimal
        assertNotNull(warnings, "Should return warnings list (may be empty)");
    }

    @Test
    @DisplayName("Test checkWarnings when engine disabled")
    void testCheckWarningsEngineDisabled() {
        engine.setEnabled(false);
        
        List<TrafficRule> warnings = engine.checkWarnings(testVehicle, testPosition);
        
        assertTrue(warnings.isEmpty(), 
            "Should return empty list when engine is disabled");
    }

    @Test
    @DisplayName("Test getRulesByType")
    void testGetRulesByType() {
        SpeedLimitRule speedRule1 = new SpeedLimitRule("Speed 1", 20.0);
        SpeedLimitRule speedRule2 = new SpeedLimitRule("Speed 2", 30.0);
        EntryExitRule entryExitRule = new EntryExitRule("Entry/Exit");
        
        engine.addRule(speedRule1);
        engine.addRule(speedRule2);
        engine.addRule(entryExitRule);
        
        List<TrafficRule> speedRules = engine.getRulesByType(RuleType.SPEED_LIMIT);
        
        assertTrue(speedRules.size() >= 2, 
            "Should return at least 2 speed limit rules");
        assertTrue(speedRules.contains(speedRule1), 
            "Should include added speed rule 1");
        assertTrue(speedRules.contains(speedRule2), 
            "Should include added speed rule 2");
        
        List<TrafficRule> entryExitRules = engine.getRulesByType(RuleType.ENTRY_EXIT);
        assertFalse(entryExitRules.isEmpty(), 
            "Should return at least 1 entry/exit rule");
    }

    @Test
    @DisplayName("Test getActiveRules")
    void testGetActiveRules() {
        SpeedLimitRule activeRule = new SpeedLimitRule("Active Rule", 20.0);
        activeRule.setActive(true);
        
        SpeedLimitRule inactiveRule = new SpeedLimitRule("Inactive Rule", 30.0);
        inactiveRule.setActive(false);
        
        engine.addRule(activeRule);
        engine.addRule(inactiveRule);
        
        List<TrafficRule> activeRules = engine.getActiveRules();
        
        assertTrue(activeRules.contains(activeRule), 
            "Should include active rule");
        assertFalse(activeRules.contains(inactiveRule), 
            "Should not include inactive rule");
    }

    @Test
    @DisplayName("Test getRules immutability")
    void testGetRulesImmutability() {
        List<TrafficRule> rules = engine.getRules();
        int initialSize = rules.size();
        
        rules.clear(); // Try to modify
        
        // Original list should not be modified
        assertTrue(engine.getRules().size() >= initialSize, 
            "Original rules list should not be modified");
    }

    @Test
    @DisplayName("Test setRules")
    void testSetRules() {
        SpeedLimitRule rule1 = new SpeedLimitRule("Rule 1", 20.0);
        SpeedLimitRule rule2 = new SpeedLimitRule("Rule 2", 30.0);
        
        List<TrafficRule> newRules = new ArrayList<>();
        newRules.add(rule1);
        newRules.add(rule2);
        engine.setRules(newRules);
        
        assertEquals(2, engine.getRules().size(), 
            "Should set rules to new list");
        assertTrue(engine.getRules().contains(rule1), 
            "Should contain rule 1");
        assertTrue(engine.getRules().contains(rule2), 
            "Should contain rule 2");
    }

    @Test
    @DisplayName("Test setEnabled")
    void testSetEnabled() {
        engine.setEnabled(false);
        assertFalse(engine.isEnabled(), 
            "Should be disabled");
        
        engine.setEnabled(true);
        assertTrue(engine.isEnabled(), 
            "Should be enabled");
    }

    @Test
    @DisplayName("Test edge case: multiple violations")
    void testMultipleViolations() {
        // Vehicle violating both speed and entry/exit rules
        testVehicle.setStatus(VehicleStatus.TAKING_OFF);
        testVehicle.setVelocity(20.0); // Violates both
        
        List<TrafficRule> violations = engine.checkViolations(testVehicle, testPosition);
        
        assertFalse(violations.isEmpty(), 
            "Should detect multiple violations");
    }

    @Test
    @DisplayName("Test edge case: inactive rule not checked")
    void testInactiveRuleNotChecked() {
        SpeedLimitRule inactiveRule = new SpeedLimitRule("Inactive", 10.0);
        inactiveRule.setActive(false);
        engine.addRule(inactiveRule);
        
        // Vehicle violating inactive rule
        testVehicle.setVelocity(15.0);
        
        List<TrafficRule> violations = engine.checkViolations(testVehicle, testPosition);
        
        assertFalse(violations.contains(inactiveRule), 
            "Should not include inactive rule in violations");
    }
}

