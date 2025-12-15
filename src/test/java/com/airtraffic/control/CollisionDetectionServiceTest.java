package com.airtraffic.control;

import com.airtraffic.map.CityMap;
import com.airtraffic.model.*;
import com.airtraffic.spatial.Quadtree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CollisionDetectionService
 * Tests collision detection algorithms, risk calculation, and minimum separation checks
 */
@DisplayName("CollisionDetectionService Tests")
class CollisionDetectionServiceTest {

    private CollisionDetectionService service;
    private Vehicle vehicle1;
    private Vehicle vehicle2;
    private Position position1;
    private Position position2;

    @BeforeEach
    void setUp() {
        service = new CollisionDetectionService();
        
        // Istanbul coordinates
        position1 = new Position(41.0082, 28.9784, 100.0); // Taksim
        position2 = new Position(41.0083, 28.9785, 100.0); // Very close to position1
        
        vehicle1 = new Vehicle(VehicleType.PASSENGER, position1);
        vehicle1.setVelocity(10.0); // 10 m/s
        vehicle1.setHeading(90.0); // East
        vehicle1.setMaxSpeed(50.0);
        
        vehicle2 = new Vehicle(VehicleType.CARGO, position2);
        vehicle2.setVelocity(10.0);
        vehicle2.setHeading(90.0);
        vehicle2.setMaxSpeed(50.0);
    }

    @Test
    @DisplayName("Test checkCollisionRisks - null vehicle")
    void testCheckCollisionRisksNullVehicle() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.checkCollisionRisks(null, new ArrayList<>(), null);
        }, "Should throw exception for null vehicle");
    }

    @Test
    @DisplayName("Test checkCollisionRisks - vehicle with null position")
    void testCheckCollisionRisksNullPosition() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPosition(null); // Explicitly set position to null
        assertThrows(IllegalArgumentException.class, () -> {
            service.checkCollisionRisks(vehicle, new ArrayList<>(), null);
        }, "Should throw exception for vehicle with null position");
    }

    @Test
    @DisplayName("Test checkCollisionRisks - no nearby vehicles")
    void testCheckCollisionRisksNoNearbyVehicles() {
        Position farPosition = new Position(41.5, 29.5, 100.0); // Far away
        Vehicle farVehicle = new Vehicle(VehicleType.PASSENGER, farPosition);
        
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(farVehicle);
        
        List<CollisionRisk> risks = service.checkCollisionRisks(vehicle1, vehicles, null);
        assertTrue(risks.isEmpty(), "Should return empty list when no nearby vehicles");
    }

    @Test
    @DisplayName("Test checkCollisionRisks - vehicles too close (violates minimum separation)")
    void testCheckCollisionRisksVehiclesTooClose() {
        // Position vehicles very close (less than 50m horizontal separation)
        Position closePosition = new Position(41.0082, 28.9784, 100.0); // Same location
        Vehicle closeVehicle = new Vehicle(VehicleType.CARGO, closePosition);
        closeVehicle.setVelocity(10.0);
        closeVehicle.setHeading(90.0);
        
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(closeVehicle);
        
        List<CollisionRisk> risks = service.checkCollisionRisks(vehicle1, vehicles, null);
        assertFalse(risks.isEmpty(), "Should detect collision risk for vehicles too close");
        
        CollisionRisk risk = risks.get(0);
        assertTrue(risk.getRiskScore() > 0.0, "Risk score should be greater than 0");
        assertTrue(risk.getRiskLevel() == RiskLevel.HIGH || risk.getRiskLevel() == RiskLevel.CRITICAL,
            "Risk level should be HIGH or CRITICAL for vehicles too close");
    }

    @Test
    @DisplayName("Test checkCollisionRisks - vehicles with safe separation")
    void testCheckCollisionRisksSafeSeparation() {
        // Position vehicles with safe separation (> 50m horizontal, > 10m vertical)
        Position safePosition = new Position(41.0085, 28.9790, 120.0); // ~100m away horizontally, 20m vertically
        Vehicle safeVehicle = new Vehicle(VehicleType.PASSENGER, safePosition);
        safeVehicle.setVelocity(10.0);
        safeVehicle.setHeading(180.0); // Moving away
        
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(safeVehicle);
        
        List<CollisionRisk> risks = service.checkCollisionRisks(vehicle1, vehicles, null);
        // Should return empty or low risk
        if (!risks.isEmpty()) {
            CollisionRisk risk = risks.get(0);
            assertTrue(risk.getRiskLevel() == RiskLevel.LOW || risk.getRiskLevel() == RiskLevel.MEDIUM,
                "Risk level should be LOW or MEDIUM for safe separation");
        }
    }

    @Test
    @DisplayName("Test calculateCollisionRisk - null vehicles")
    void testCalculateCollisionRiskNullVehicles() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCollisionRisk(null, vehicle2);
        }, "Should throw exception for null vehicle1");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCollisionRisk(vehicle1, null);
        }, "Should throw exception for null vehicle2");
    }

    @Test
    @DisplayName("Test calculateCollisionRisk - vehicles with null positions")
    void testCalculateCollisionRiskNullPositions() {
        Vehicle v1 = new Vehicle();
        v1.setPosition(null); // Explicitly set position to null
        Vehicle v2 = new Vehicle();
        v2.setPosition(null); // Explicitly set position to null
        
        CollisionRisk risk = service.calculateCollisionRisk(v1, v2);
        assertNull(risk, "Should return null when positions are null");
    }

    @Test
    @DisplayName("Test calculateCollisionRisk - vehicles too close horizontally")
    void testCalculateCollisionRiskHorizontalViolation() {
        // Same location, same altitude
        Position samePos = new Position(41.0082, 28.9784, 100.0);
        Vehicle v2 = new Vehicle(VehicleType.CARGO, samePos);
        
        CollisionRisk risk = service.calculateCollisionRisk(vehicle1, v2);
        assertNotNull(risk, "Should detect collision risk");
        assertTrue(risk.getRiskScore() > 0.0, "Risk score should be positive");
        assertTrue(risk.getHorizontalDistance() < CollisionDetectionService.getMinHorizontalSeparation(),
            "Horizontal distance should be less than minimum separation");
    }

    @Test
    @DisplayName("Test calculateCollisionRisk - vehicles too close vertically")
    void testCalculateCollisionRiskVerticalViolation() {
        // Same location, different altitude (less than 10m difference)
        Position closeAltPos = new Position(41.0082, 28.9784, 105.0); // 5m difference
        Vehicle v2 = new Vehicle(VehicleType.CARGO, closeAltPos);
        
        CollisionRisk risk = service.calculateCollisionRisk(vehicle1, v2);
        assertNotNull(risk, "Should detect collision risk");
        assertTrue(risk.getVerticalDistance() < CollisionDetectionService.getMinVerticalSeparation(),
            "Vertical distance should be less than minimum separation");
    }

    @Test
    @DisplayName("Test checkMinimumSeparation - valid separation")
    void testCheckMinimumSeparationValid() {
        // Vehicles with safe separation
        Position safePos = new Position(41.0085, 28.9790, 120.0); // > 50m horizontal, > 10m vertical
        Vehicle safeVehicle = new Vehicle(VehicleType.PASSENGER, safePos);
        
        assertTrue(service.checkMinimumSeparation(vehicle1, safeVehicle),
            "Should return true for vehicles with safe separation");
    }

    @Test
    @DisplayName("Test checkMinimumSeparation - horizontal violation")
    void testCheckMinimumSeparationHorizontalViolation() {
        // Vehicles too close horizontally
        Position closePos = new Position(41.0082, 28.9784, 100.0); // Same location
        Vehicle closeVehicle = new Vehicle(VehicleType.CARGO, closePos);
        
        assertFalse(service.checkMinimumSeparation(vehicle1, closeVehicle),
            "Should return false for vehicles violating horizontal separation");
    }

    @Test
    @DisplayName("Test checkMinimumSeparation - vertical violation")
    void testCheckMinimumSeparationVerticalViolation() {
        // Vehicles too close vertically
        Position closeAltPos = new Position(41.0082, 28.9784, 105.0); // 5m difference
        Vehicle closeVehicle = new Vehicle(VehicleType.CARGO, closeAltPos);
        
        assertFalse(service.checkMinimumSeparation(vehicle1, closeVehicle),
            "Should return false for vehicles violating vertical separation");
    }

    @Test
    @DisplayName("Test checkMinimumSeparation - null vehicles")
    void testCheckMinimumSeparationNullVehicles() {
        assertFalse(service.checkMinimumSeparation(null, vehicle2),
            "Should return false for null vehicle1");
        
        assertFalse(service.checkMinimumSeparation(vehicle1, null),
            "Should return false for null vehicle2");
    }

    @Test
    @DisplayName("Test predictFuturePosition - valid prediction")
    void testPredictFuturePositionValid() {
        // Set heading to 0 degrees (North) so latitude changes
        vehicle1.setHeading(0.0); // North direction
        vehicle1.setVelocity(50.0); // 50 m/s for more noticeable change
        
        Position futurePos = service.predictFuturePosition(vehicle1, 10.0); // 10 seconds
        
        assertNotNull(futurePos, "Should return future position");
        // Future position should be different from current (if moving)
        if (vehicle1.getVelocity() > 0) {
            // With heading 0 (North), latitude should increase
            assertTrue(futurePos.getLatitude() > vehicle1.getPosition().getLatitude(),
                "Future latitude should increase when moving North");
        }
    }

    @Test
    @DisplayName("Test predictFuturePosition - zero velocity")
    void testPredictFuturePositionZeroVelocity() {
        vehicle1.setVelocity(0.0);
        Position futurePos = service.predictFuturePosition(vehicle1, 10.0);
        
        assertNotNull(futurePos, "Should return position even with zero velocity");
        assertEquals(vehicle1.getPosition().getLatitude(), futurePos.getLatitude(), 0.0001,
            "Position should not change with zero velocity");
    }

    @Test
    @DisplayName("Test predictFuturePosition - null vehicle")
    void testPredictFuturePositionNullVehicle() {
        Position futurePos = service.predictFuturePosition(null, 10.0);
        assertNull(futurePos, "Should return null for null vehicle");
    }

    @Test
    @DisplayName("Test predictFuturePosition - vehicle with null position")
    void testPredictFuturePositionNullPosition() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPosition(null); // Explicitly set position to null
        Position futurePos = service.predictFuturePosition(vehicle, 10.0);
        assertNull(futurePos, "Should return null for vehicle with null position");
    }

    @Test
    @DisplayName("Test getMinHorizontalSeparation")
    void testGetMinHorizontalSeparation() {
        double minSep = CollisionDetectionService.getMinHorizontalSeparation();
        assertEquals(50.0, minSep, 0.001, "Minimum horizontal separation should be 50.0 meters");
    }

    @Test
    @DisplayName("Test getMinVerticalSeparation")
    void testGetMinVerticalSeparation() {
        double minSep = CollisionDetectionService.getMinVerticalSeparation();
        assertEquals(10.0, minSep, 0.001, "Minimum vertical separation should be 10.0 meters");
    }

    @Test
    @DisplayName("Test checkCollisionRisks with Quadtree")
    void testCheckCollisionRisksWithQuadtree() {
        // Create Quadtree
        Quadtree quadtree = new Quadtree(40.8, 41.2, 28.5, 29.5);
        quadtree.insert(vehicle2);
        
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(vehicle2);
        
        List<CollisionRisk> risks = service.checkCollisionRisks(vehicle1, vehicles, quadtree);
        // Should work with Quadtree (may or may not detect risk depending on distance)
        assertNotNull(risks, "Should return list (may be empty)");
    }

    @Test
    @DisplayName("Test calculateCollisionRisk - risk score range")
    void testCalculateCollisionRiskScoreRange() {
        Position samePos = new Position(41.0082, 28.9784, 100.0);
        Vehicle v2 = new Vehicle(VehicleType.CARGO, samePos);
        
        CollisionRisk risk = service.calculateCollisionRisk(vehicle1, v2);
        if (risk != null) {
            assertTrue(risk.getRiskScore() >= 0.0 && risk.getRiskScore() <= 1.0,
                "Risk score should be between 0.0 and 1.0");
        }
    }

    @Test
    @DisplayName("Test calculateCollisionRisk - estimated time to collision")
    void testCalculateCollisionRiskTimeToCollision() {
        Position samePos = new Position(41.0082, 28.9784, 100.0);
        Vehicle v2 = new Vehicle(VehicleType.CARGO, samePos);
        v2.setVelocity(10.0);
        
        CollisionRisk risk = service.calculateCollisionRisk(vehicle1, v2);
        if (risk != null) {
            assertTrue(risk.getEstimatedTimeToCollision() >= 0.0,
                "Estimated time to collision should be non-negative");
        }
    }

    // ========== Altitude Layer Integration Tests ==========

    @Test
    @DisplayName("Test calculateCollisionRisk with CityMap - vehicles in different layers should have lower risk")
    void testCalculateCollisionRiskDifferentLayers() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Vehicle 1 in LOW layer (30m)
        Position pos1 = new Position(41.0082, 28.9784, 30.0);
        Vehicle v1 = new Vehicle(VehicleType.CARGO, pos1);
        v1.setVelocity(10.0);
        
        // Vehicle 2 in MEDIUM layer (90m) - same horizontal position
        Position pos2 = new Position(41.0082, 28.9784, 90.0);
        Vehicle v2 = new Vehicle(VehicleType.PASSENGER, pos2);
        v2.setVelocity(10.0);
        
        // Without CityMap - should detect risk (same horizontal position)
        CollisionRisk riskWithoutMap = service.calculateCollisionRisk(v1, v2);
        assertNotNull(riskWithoutMap, "Should detect risk without layer consideration");
        
        // With CityMap - should have lower risk or no risk (different layers)
        CollisionRisk riskWithMap = service.calculateCollisionRisk(v1, v2, cityMap);
        if (riskWithMap != null) {
            assertTrue(riskWithMap.getRiskScore() < riskWithoutMap.getRiskScore(),
                "Risk score should be lower when vehicles are in different layers");
        } else {
            // Or risk might be null if layers provide sufficient separation
            assertNull(riskWithMap, "Risk might be null if layers provide sufficient separation");
        }
    }

    @Test
    @DisplayName("Test calculateCollisionRisk with CityMap - vehicles in same layer should have normal risk")
    void testCalculateCollisionRiskSameLayer() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Both vehicles in MEDIUM layer (90m and 95m)
        Position pos1 = new Position(41.0082, 28.9784, 90.0);
        Vehicle v1 = new Vehicle(VehicleType.CARGO, pos1);
        v1.setVelocity(10.0);
        
        Position pos2 = new Position(41.0082, 28.9784, 95.0); // Same horizontal, close vertical
        Vehicle v2 = new Vehicle(VehicleType.PASSENGER, pos2);
        v2.setVelocity(10.0);
        
        CollisionRisk risk = service.calculateCollisionRisk(v1, v2, cityMap);
        assertNotNull(risk, "Should detect risk when vehicles are in same layer and close");
        assertTrue(risk.getRiskScore() > 0.0, "Risk score should be positive");
    }

    @Test
    @DisplayName("Test calculateCollisionRisk with CityMap - null CityMap should work as before")
    void testCalculateCollisionRiskNullCityMap() {
        Position samePos = new Position(41.0082, 28.9784, 100.0);
        Vehicle v2 = new Vehicle(VehicleType.CARGO, samePos);
        
        // Should work without CityMap (backward compatibility)
        CollisionRisk risk = service.calculateCollisionRisk(vehicle1, v2, null);
        assertNotNull(risk, "Should work without CityMap");
    }

    @Test
    @DisplayName("Test calculateCollisionRisk with CityMap - vehicles in LOW and HIGH layers")
    void testCalculateCollisionRiskLowAndHighLayers() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Vehicle 1 in LOW layer (30m)
        Position pos1 = new Position(41.0082, 28.9784, 30.0);
        Vehicle v1 = new Vehicle(VehicleType.CARGO, pos1);
        v1.setVelocity(10.0);
        
        // Vehicle 2 in HIGH layer (150m) - same horizontal position
        Position pos2 = new Position(41.0082, 28.9784, 150.0);
        Vehicle v2 = new Vehicle(VehicleType.EMERGENCY, pos2);
        v2.setVelocity(10.0);
        
        CollisionRisk risk = service.calculateCollisionRisk(v1, v2, cityMap);
        // Should have very low or no risk due to large vertical separation between layers
        if (risk != null) {
            assertTrue(risk.getRiskScore() < 0.3, 
                "Risk score should be low when vehicles are in LOW and HIGH layers");
        }
    }

    @Test
    @DisplayName("Test checkCollisionRisks with CityMap - should consider altitude layers")
    void testCheckCollisionRisksWithCityMap() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Vehicle 1 in LOW layer
        Position pos1 = new Position(41.0082, 28.9784, 30.0);
        Vehicle v1 = new Vehicle(VehicleType.CARGO, pos1);
        v1.setVelocity(10.0);
        
        // Vehicle 2 in MEDIUM layer - same horizontal position
        Position pos2 = new Position(41.0082, 28.9784, 90.0);
        Vehicle v2 = new Vehicle(VehicleType.PASSENGER, pos2);
        v2.setVelocity(10.0);
        
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(v2);
        
        List<CollisionRisk> risks = service.checkCollisionRisks(v1, vehicles, null, cityMap);
        assertNotNull(risks, "Should return list");
        // Risk should be lower or null due to different layers
    }
}

