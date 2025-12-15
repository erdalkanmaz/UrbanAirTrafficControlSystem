package com.airtraffic.standards;

import com.airtraffic.map.CityMap;
import com.airtraffic.model.AltitudeLayer;
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
 * Unit tests for ICAOStandardsCompliance
 * Tests ICAO Annex 2 compliance checks, separation standards, and communication requirements
 */
@DisplayName("ICAOStandardsCompliance Tests")
class ICAOStandardsComplianceTest {

    private ICAOStandardsCompliance compliance;
    private Vehicle vehicle1;
    private Vehicle vehicle2;
    private Position position1;
    private Position position2;

    @BeforeEach
    void setUp() {
        compliance = new ICAOStandardsCompliance();
        
        position1 = new Position(41.0082, 28.9784, 100.0);
        position2 = new Position(41.0085, 28.9790, 120.0); // Safe separation
        
        vehicle1 = new Vehicle(VehicleType.PASSENGER, position1);
        vehicle1.setVelocity(10.0);
        vehicle1.setStatus(VehicleStatus.IN_FLIGHT);
        
        vehicle2 = new Vehicle(VehicleType.CARGO, position2);
        vehicle2.setVelocity(10.0);
        vehicle2.setStatus(VehicleStatus.IN_FLIGHT);
    }

    @Test
    @DisplayName("Test checkSeparationStandards - valid separation")
    void testCheckSeparationStandardsValid() {
        ComplianceResult result = compliance.checkSeparationStandards(vehicle1, vehicle2);
        
        assertTrue(result.isCompliant(), "Should be compliant with safe separation");
        assertTrue(result.getViolations().isEmpty(), "Should have no violations");
    }

    @Test
    @DisplayName("Test checkSeparationStandards - horizontal violation")
    void testCheckSeparationStandardsHorizontalViolation() {
        // Vehicles too close horizontally
        Position closePos = new Position(41.0082, 28.9784, 100.0); // Same location
        Vehicle closeVehicle = new Vehicle(VehicleType.CARGO, closePos);
        
        ComplianceResult result = compliance.checkSeparationStandards(vehicle1, closeVehicle);
        
        assertFalse(result.isCompliant(), "Should not be compliant with horizontal violation");
        assertFalse(result.getViolations().isEmpty(), "Should have violations");
        assertTrue(result.getViolations().get(0).contains("Horizontal separation violation"),
            "Should report horizontal separation violation");
    }

    @Test
    @DisplayName("Test checkSeparationStandards - vertical violation")
    void testCheckSeparationStandardsVerticalViolation() {
        // Vehicles too close vertically (but with safe horizontal separation)
        // Use different horizontal position to avoid horizontal violation
        Position closeAltPos = new Position(41.0085, 28.9790, 105.0); // 5m vertical difference, but >50m horizontal
        Vehicle closeVehicle = new Vehicle(VehicleType.CARGO, closeAltPos);
        
        ComplianceResult result = compliance.checkSeparationStandards(vehicle1, closeVehicle);
        
        assertFalse(result.isCompliant(), "Should not be compliant with vertical violation");
        assertFalse(result.getViolations().isEmpty(), "Should have violations");
        // Check if any violation contains "Vertical separation violation"
        boolean hasVerticalViolation = result.getViolations().stream()
            .anyMatch(v -> v.contains("Vertical separation violation"));
        assertTrue(hasVerticalViolation,
            "Should report vertical separation violation");
    }

    @Test
    @DisplayName("Test checkSeparationStandards - null vehicles")
    void testCheckSeparationStandardsNullVehicles() {
        ComplianceResult result1 = compliance.checkSeparationStandards(null, vehicle2);
        assertFalse(result1.isCompliant(), "Should not be compliant with null vehicle1");
        
        ComplianceResult result2 = compliance.checkSeparationStandards(vehicle1, null);
        assertFalse(result2.isCompliant(), "Should not be compliant with null vehicle2");
    }

    @Test
    @DisplayName("Test checkSeparationStandards - vehicles with null positions")
    void testCheckSeparationStandardsNullPositions() {
        Vehicle v1 = new Vehicle();
        Vehicle v2 = new Vehicle();
        
        ComplianceResult result = compliance.checkSeparationStandards(v1, v2);
        assertFalse(result.isCompliant(), "Should not be compliant with null positions");
        assertFalse(result.getViolations().isEmpty(), "Should report violation");
    }

    @Test
    @DisplayName("Test checkFlightRulesCompliance - valid vehicle")
    void testCheckFlightRulesComplianceValid() {
        ComplianceResult result = compliance.checkFlightRulesCompliance(vehicle1);
        
        assertTrue(result.isCompliant(), "Should be compliant for valid vehicle");
    }

    @Test
    @DisplayName("Test checkFlightRulesCompliance - null vehicle")
    void testCheckFlightRulesComplianceNullVehicle() {
        ComplianceResult result = compliance.checkFlightRulesCompliance(null);
        
        assertFalse(result.isCompliant(), "Should not be compliant with null vehicle");
        assertTrue(result.getViolations().get(0).contains("null"),
            "Should report null vehicle violation");
    }

    @Test
    @DisplayName("Test checkFlightRulesCompliance - negative altitude")
    void testCheckFlightRulesComplianceNegativeAltitude() {
        Position negAltPos = new Position(41.0082, 28.9784, -10.0);
        Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, negAltPos);
        vehicle.setStatus(VehicleStatus.IN_FLIGHT);
        
        ComplianceResult result = compliance.checkFlightRulesCompliance(vehicle);
        
        assertFalse(result.isCompliant(), "Should not be compliant with negative altitude");
    }

    @Test
    @DisplayName("Test validateCommunicationRequirements - within range")
    void testValidateCommunicationRequirementsWithinRange() {
        Position baseStationPos = new Position(41.0082, 28.9784, 50.0); // Same location
        List<Position> baseStations = new ArrayList<>();
        baseStations.add(baseStationPos);
        
        assertTrue(compliance.validateCommunicationRequirements(vehicle1, baseStations),
            "Should be within communication range");
    }

    @Test
    @DisplayName("Test validateCommunicationRequirements - out of range")
    void testValidateCommunicationRequirementsOutOfRange() {
        // Base station far away (> 5km)
        Position farBaseStationPos = new Position(41.5, 29.5, 50.0);
        List<Position> baseStations = new ArrayList<>();
        baseStations.add(farBaseStationPos);
        
        assertFalse(compliance.validateCommunicationRequirements(vehicle1, baseStations),
            "Should be out of communication range");
    }

    @Test
    @DisplayName("Test validateCommunicationRequirements - null vehicle")
    void testValidateCommunicationRequirementsNullVehicle() {
        List<Position> baseStations = new ArrayList<>();
        baseStations.add(new Position(41.0082, 28.9784, 50.0));
        
        assertFalse(compliance.validateCommunicationRequirements(null, baseStations),
            "Should return false for null vehicle");
    }

    @Test
    @DisplayName("Test validateCommunicationRequirements - empty base stations")
    void testValidateCommunicationRequirementsEmptyBaseStations() {
        List<Position> baseStations = new ArrayList<>();
        
        assertFalse(compliance.validateCommunicationRequirements(vehicle1, baseStations),
            "Should return false when no base stations");
    }

    @Test
    @DisplayName("Test checkAllSeparationStandards - multiple vehicles")
    void testCheckAllSeparationStandardsMultipleVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        
        // Add a third vehicle too close to vehicle1
        Position closePos = new Position(41.0082, 28.9784, 100.0);
        Vehicle closeVehicle = new Vehicle(VehicleType.PASSENGER, closePos);
        vehicles.add(closeVehicle);
        
        List<ComplianceResult> results = compliance.checkAllSeparationStandards(vehicles);
        
        assertFalse(results.isEmpty(), "Should detect separation violations");
    }

    @Test
    @DisplayName("Test checkAllSeparationStandards - insufficient vehicles")
    void testCheckAllSeparationStandardsInsufficientVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(vehicle1);
        
        List<ComplianceResult> results = compliance.checkAllSeparationStandards(vehicles);
        
        assertTrue(results.isEmpty(), "Should return empty list with less than 2 vehicles");
    }

    @Test
    @DisplayName("Test getMinHorizontalSeparation")
    void testGetMinHorizontalSeparation() {
        double minSep = ICAOStandardsCompliance.getMinHorizontalSeparation();
        assertEquals(50.0, minSep, 0.001, "Minimum horizontal separation should be 50.0 meters");
    }

    @Test
    @DisplayName("Test getMinVerticalSeparation")
    void testGetMinVerticalSeparation() {
        double minSep = ICAOStandardsCompliance.getMinVerticalSeparation();
        assertEquals(10.0, minSep, 0.001, "Minimum vertical separation should be 10.0 meters");
    }

    @Test
    @DisplayName("Test getCommunicationRange")
    void testGetCommunicationRange() {
        double range = ICAOStandardsCompliance.getCommunicationRange();
        assertEquals(5000.0, range, 0.001, "Communication range should be 5000.0 meters");
    }

    // ========== Altitude Layer Integration Tests ==========

    @Test
    @DisplayName("Test checkSeparationStandards with CityMap - vehicles in different layers should be compliant")
    void testCheckSeparationStandardsDifferentLayers() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Vehicle 1 in LOW layer (30m)
        Position pos1 = new Position(41.0082, 28.9784, 30.0);
        Vehicle v1 = new Vehicle(VehicleType.CARGO, pos1);
        
        // Vehicle 2 in MEDIUM layer (90m) - same horizontal position
        Position pos2 = new Position(41.0082, 28.9784, 90.0);
        Vehicle v2 = new Vehicle(VehicleType.PASSENGER, pos2);
        
        // Without CityMap - should detect violation (same horizontal position, vertical < 10m separation)
        ComplianceResult resultWithoutMap = compliance.checkSeparationStandards(v1, v2);
        assertFalse(resultWithoutMap.isCompliant(), 
            "Should detect violation without layer consideration");
        
        // With CityMap - should be compliant (different layers provide sufficient separation)
        ComplianceResult resultWithMap = compliance.checkSeparationStandards(v1, v2, cityMap);
        assertTrue(resultWithMap.isCompliant(), 
            "Should be compliant when vehicles are in different layers");
    }

    @Test
    @DisplayName("Test checkSeparationStandards with CityMap - vehicles in same layer should follow normal rules")
    void testCheckSeparationStandardsSameLayer() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Both vehicles in MEDIUM layer (90m and 95m) - too close vertically
        Position pos1 = new Position(41.0082, 28.9784, 90.0);
        Vehicle v1 = new Vehicle(VehicleType.CARGO, pos1);
        
        Position pos2 = new Position(41.0082, 28.9784, 95.0); // Same horizontal, 5m vertical difference
        Vehicle v2 = new Vehicle(VehicleType.PASSENGER, pos2);
        
        ComplianceResult result = compliance.checkSeparationStandards(v1, v2, cityMap);
        assertFalse(result.isCompliant(), 
            "Should detect violation when vehicles are in same layer and too close");
    }

    @Test
    @DisplayName("Test checkSeparationStandards with CityMap - null CityMap should work as before")
    void testCheckSeparationStandardsNullCityMap() {
        Position samePos = new Position(41.0082, 28.9784, 100.0);
        Vehicle v2 = new Vehicle(VehicleType.CARGO, samePos);
        
        // Should work without CityMap (backward compatibility)
        ComplianceResult result = compliance.checkSeparationStandards(vehicle1, v2, null);
        assertFalse(result.isCompliant(), "Should work without CityMap");
    }

    @Test
    @DisplayName("Test checkSeparationStandards with CityMap - LOW and HIGH layers")
    void testCheckSeparationStandardsLowAndHighLayers() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Vehicle 1 in LOW layer (30m)
        Position pos1 = new Position(41.0082, 28.9784, 30.0);
        Vehicle v1 = new Vehicle(VehicleType.CARGO, pos1);
        
        // Vehicle 2 in HIGH layer (150m) - same horizontal position
        Position pos2 = new Position(41.0082, 28.9784, 150.0);
        Vehicle v2 = new Vehicle(VehicleType.EMERGENCY, pos2);
        
        ComplianceResult result = compliance.checkSeparationStandards(v1, v2, cityMap);
        assertTrue(result.isCompliant(), 
            "Should be compliant when vehicles are in LOW and HIGH layers (120m separation)");
    }

    @Test
    @DisplayName("Test checkFlightRulesCompliance with CityMap - vehicle should be in appropriate layer")
    void testCheckFlightRulesComplianceWithCityMap() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Vehicle in MEDIUM layer (90m)
        Position pos = new Position(41.0082, 28.9784, 90.0);
        Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
        vehicle.setStatus(VehicleStatus.IN_FLIGHT);
        
        ComplianceResult result = compliance.checkFlightRulesCompliance(vehicle, cityMap);
        assertTrue(result.isCompliant(), 
            "Should be compliant when vehicle is in appropriate layer");
    }

    @Test
    @DisplayName("Test checkFlightRulesCompliance with CityMap - vehicle in restricted zone")
    void testCheckFlightRulesComplianceRestrictedZone() {
        CityMap cityMap = new CityMap("Test City");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Add restricted zone
        com.airtraffic.map.RestrictedZone zone = new com.airtraffic.map.RestrictedZone(
            "Test Zone", com.airtraffic.map.RestrictedZoneType.GOVERNMENT);
        zone.setMinAltitude(0.0);
        zone.setMaxAltitude(200.0);
        zone.addBoundaryPoint(new Position(41.0050, 28.9750, 0.0));
        zone.addBoundaryPoint(new Position(41.0100, 28.9750, 0.0));
        zone.addBoundaryPoint(new Position(41.0100, 28.9800, 0.0));
        zone.addBoundaryPoint(new Position(41.0050, 28.9800, 0.0));
        cityMap.addRestrictedZone(zone);
        
        // Vehicle in restricted zone
        Position pos = new Position(41.0075, 28.9775, 100.0);
        Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
        vehicle.setStatus(VehicleStatus.IN_FLIGHT);
        
        ComplianceResult result = compliance.checkFlightRulesCompliance(vehicle, cityMap);
        assertFalse(result.isCompliant(), 
            "Should not be compliant when vehicle is in restricted zone");
    }
}

