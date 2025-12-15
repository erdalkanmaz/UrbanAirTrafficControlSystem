package com.airtraffic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CollisionRisk class
 * Tests collision risk model, risk level determination, and recommended actions
 */
@DisplayName("CollisionRisk Tests")
class CollisionRiskTest {

    private CollisionRisk collisionRisk;

    @BeforeEach
    void setUp() {
        collisionRisk = new CollisionRisk();
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        assertNotNull(collisionRisk.getDetectedAt(), "Should have detection timestamp");
        assertEquals(0.0, collisionRisk.getRiskScore(), 0.001, "Default risk score should be 0.0");
        assertEquals(Double.MAX_VALUE, collisionRisk.getEstimatedTimeToCollision(), 
            "Default time to collision should be MAX_VALUE");
    }

    @Test
    @DisplayName("Test constructor with parameters")
    void testConstructorWithParameters() {
        CollisionRisk risk = new CollisionRisk("vehicle1", "vehicle2", RiskLevel.HIGH, 0.75);
        
        assertEquals("vehicle1", risk.getVehicleId1());
        assertEquals("vehicle2", risk.getVehicleId2());
        assertEquals(RiskLevel.HIGH, risk.getRiskLevel());
        assertEquals(0.75, risk.getRiskScore(), 0.001);
        assertNotNull(risk.getRecommendedAction(), "Should have recommended action");
    }

    @Test
    @DisplayName("Test risk score validation - valid range")
    void testRiskScoreValidRange() {
        collisionRisk.setRiskScore(0.0);
        assertEquals(0.0, collisionRisk.getRiskScore(), 0.001);
        
        collisionRisk.setRiskScore(0.5);
        assertEquals(0.5, collisionRisk.getRiskScore(), 0.001);
        
        collisionRisk.setRiskScore(1.0);
        assertEquals(1.0, collisionRisk.getRiskScore(), 0.001);
    }

    @Test
    @DisplayName("Test risk score validation - negative value")
    void testRiskScoreNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            collisionRisk.setRiskScore(-0.1);
        }, "Should throw exception for negative risk score");
    }

    @Test
    @DisplayName("Test risk score validation - value greater than 1.0")
    void testRiskScoreGreaterThanOne() {
        assertThrows(IllegalArgumentException.class, () -> {
            collisionRisk.setRiskScore(1.1);
        }, "Should throw exception for risk score > 1.0");
    }

    @Test
    @DisplayName("Test risk level LOW recommended action")
    void testLowRiskRecommendedAction() {
        collisionRisk.setRiskLevel(RiskLevel.LOW);
        assertEquals("Continue monitoring", collisionRisk.getRecommendedAction());
    }

    @Test
    @DisplayName("Test risk level MEDIUM recommended action")
    void testMediumRiskRecommendedAction() {
        collisionRisk.setRiskLevel(RiskLevel.MEDIUM);
        assertEquals("Increase separation distance", collisionRisk.getRecommendedAction());
    }

    @Test
    @DisplayName("Test risk level HIGH recommended action")
    void testHighRiskRecommendedAction() {
        collisionRisk.setRiskLevel(RiskLevel.HIGH);
        assertEquals("Immediate course correction required", collisionRisk.getRecommendedAction());
    }

    @Test
    @DisplayName("Test risk level CRITICAL recommended action")
    void testCriticalRiskRecommendedAction() {
        collisionRisk.setRiskLevel(RiskLevel.CRITICAL);
        assertEquals("EMERGENCY: Immediate evasive action required", collisionRisk.getRecommendedAction());
    }

    @Test
    @DisplayName("Test isCritical method - CRITICAL risk")
    void testIsCriticalTrue() {
        collisionRisk.setRiskLevel(RiskLevel.CRITICAL);
        assertTrue(collisionRisk.isCritical(), "CRITICAL risk should return true");
    }

    @Test
    @DisplayName("Test isCritical method - non-CRITICAL risk")
    void testIsCriticalFalse() {
        collisionRisk.setRiskLevel(RiskLevel.HIGH);
        assertFalse(collisionRisk.isCritical(), "HIGH risk should return false");
    }

    @Test
    @DisplayName("Test requiresImmediateAction - HIGH risk")
    void testRequiresImmediateActionHigh() {
        collisionRisk.setRiskLevel(RiskLevel.HIGH);
        assertTrue(collisionRisk.requiresImmediateAction(), "HIGH risk requires immediate action");
    }

    @Test
    @DisplayName("Test requiresImmediateAction - CRITICAL risk")
    void testRequiresImmediateActionCritical() {
        collisionRisk.setRiskLevel(RiskLevel.CRITICAL);
        assertTrue(collisionRisk.requiresImmediateAction(), "CRITICAL risk requires immediate action");
    }

    @Test
    @DisplayName("Test requiresImmediateAction - MEDIUM risk")
    void testRequiresImmediateActionMedium() {
        collisionRisk.setRiskLevel(RiskLevel.MEDIUM);
        assertFalse(collisionRisk.requiresImmediateAction(), "MEDIUM risk does not require immediate action");
    }

    @Test
    @DisplayName("Test requiresImmediateAction - LOW risk")
    void testRequiresImmediateActionLow() {
        collisionRisk.setRiskLevel(RiskLevel.LOW);
        assertFalse(collisionRisk.requiresImmediateAction(), "LOW risk does not require immediate action");
    }

    @Test
    @DisplayName("Test estimated time to collision validation")
    void testEstimatedTimeToCollisionValidation() {
        collisionRisk.setEstimatedTimeToCollision(10.5);
        assertEquals(10.5, collisionRisk.getEstimatedTimeToCollision(), 0.001);
    }

    @Test
    @DisplayName("Test estimated time to collision - negative value")
    void testEstimatedTimeToCollisionNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            collisionRisk.setEstimatedTimeToCollision(-1.0);
        }, "Should throw exception for negative time to collision");
    }

    @Test
    @DisplayName("Test distance setters - valid values")
    void testDistanceSettersValid() {
        collisionRisk.setCurrentDistance(100.0);
        collisionRisk.setHorizontalDistance(80.0);
        collisionRisk.setVerticalDistance(60.0);
        
        assertEquals(100.0, collisionRisk.getCurrentDistance(), 0.001);
        assertEquals(80.0, collisionRisk.getHorizontalDistance(), 0.001);
        assertEquals(60.0, collisionRisk.getVerticalDistance(), 0.001);
    }

    @Test
    @DisplayName("Test distance setters - negative values")
    void testDistanceSettersNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            collisionRisk.setCurrentDistance(-10.0);
        }, "Should throw exception for negative current distance");
        
        assertThrows(IllegalArgumentException.class, () -> {
            collisionRisk.setHorizontalDistance(-10.0);
        }, "Should throw exception for negative horizontal distance");
    }

    @Test
    @DisplayName("Test vertical distance - can be negative")
    void testVerticalDistanceCanBeNegative() {
        // Vertical distance can be negative (height difference)
        collisionRisk.setVerticalDistance(-10.0);
        assertEquals(-10.0, collisionRisk.getVerticalDistance(), 0.001);
    }

    @Test
    @DisplayName("Test equals and hashCode")
    void testEqualsAndHashCode() {
        CollisionRisk risk1 = new CollisionRisk("v1", "v2", RiskLevel.HIGH, 0.5);
        CollisionRisk risk2 = new CollisionRisk("v1", "v2", RiskLevel.HIGH, 0.5);
        CollisionRisk risk3 = new CollisionRisk("v1", "v3", RiskLevel.HIGH, 0.5);
        
        assertEquals(risk1, risk2, "Risks with same vehicle IDs should be equal");
        assertNotEquals(risk1, risk3, "Risks with different vehicle IDs should not be equal");
        assertEquals(risk1.hashCode(), risk2.hashCode(), "Equal risks should have same hashCode");
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        collisionRisk.setVehicleId1("v1");
        collisionRisk.setVehicleId2("v2");
        collisionRisk.setRiskLevel(RiskLevel.CRITICAL);
        collisionRisk.setRiskScore(0.9);
        
        String str = collisionRisk.toString();
        assertTrue(str.contains("v1"), "toString should contain vehicle ID 1");
        assertTrue(str.contains("v2"), "toString should contain vehicle ID 2");
        assertTrue(str.contains("CRITICAL"), "toString should contain risk level");
        assertTrue(str.contains("0.9"), "toString should contain risk score");
    }
}






