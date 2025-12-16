package com.airtraffic.model;

import com.airtraffic.control.BaseStation;
import com.airtraffic.control.FlightAuthorization;
import com.airtraffic.map.CityMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SystemState class
 */
@DisplayName("SystemState Tests")
class SystemStateTest {

    private SystemState systemState;
    private CityMap cityMap;
    private List<Vehicle> vehicles;
    private List<BaseStation> baseStations;
    private Map<String, FlightAuthorization> authorizations;

    @BeforeEach
    void setUp() {
        systemState = new SystemState();
        
        // Create sample city map
        cityMap = new CityMap("Istanbul");
        cityMap.setMinLatitude(40.8);
        cityMap.setMaxLatitude(41.2);
        cityMap.setMinLongitude(28.5);
        cityMap.setMaxLongitude(29.5);
        
        // Create sample vehicles
        vehicles = new ArrayList<>();
        Vehicle vehicle1 = new Vehicle(VehicleType.CARGO, new Position(41.0, 29.0, 100.0));
        vehicle1.setId("V1");
        Vehicle vehicle2 = new Vehicle(VehicleType.PASSENGER, new Position(41.1, 29.1, 120.0));
        vehicle2.setId("V2");
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        
        // Create sample base stations
        baseStations = new ArrayList<>();
        BaseStation station1 = new BaseStation("BS1", new Position(41.0, 29.0, 50.0), 5000.0);
        baseStations.add(station1);
        
        // Create sample authorizations
        authorizations = new HashMap<>();
        FlightAuthorization auth1 = new FlightAuthorization("V1", 
            new Position(41.0, 29.0, 100.0), 
            new Position(41.1, 29.1, 100.0));
        authorizations.put("V1", auth1);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        SystemState state = new SystemState();
        
        assertNotNull(state);
        assertNotNull(state.getVehicles());
        assertNotNull(state.getBaseStations());
        assertNotNull(state.getAuthorizations());
        assertTrue(state.getVehicles().isEmpty());
        assertTrue(state.getBaseStations().isEmpty());
        assertTrue(state.getAuthorizations().isEmpty());
        assertTrue(state.getTimestamp() > 0);
    }

    @Test
    @DisplayName("Test constructor with parameters")
    void testConstructorWithParameters() {
        SystemState state = new SystemState(cityMap, vehicles, baseStations, 
            authorizations, "CENTER-1", true);
        
        assertEquals(cityMap, state.getCityMap());
        assertEquals(2, state.getVehicles().size());
        assertEquals(1, state.getBaseStations().size());
        assertEquals(1, state.getAuthorizations().size());
        assertEquals("CENTER-1", state.getCenterId());
        assertTrue(state.isOperational());
    }

    @Test
    @DisplayName("Test constructor with null parameters")
    void testConstructorWithNullParameters() {
        SystemState state = new SystemState(null, null, null, null, null, false);
        
        assertNull(state.getCityMap());
        assertNotNull(state.getVehicles());
        assertNotNull(state.getBaseStations());
        assertNotNull(state.getAuthorizations());
        assertTrue(state.getVehicles().isEmpty());
        assertTrue(state.getBaseStations().isEmpty());
        assertTrue(state.getAuthorizations().isEmpty());
        assertNull(state.getCenterId());
        assertFalse(state.isOperational());
    }

    @Test
    @DisplayName("Test setCityMap")
    void testSetCityMap() {
        systemState.setCityMap(cityMap);
        assertEquals(cityMap, systemState.getCityMap());
    }

    @Test
    @DisplayName("Test setVehicles")
    void testSetVehicles() {
        systemState.setVehicles(vehicles);
        assertEquals(2, systemState.getVehicles().size());
        assertEquals(vehicles.get(0).getId(), systemState.getVehicles().get(0).getId());
    }

    @Test
    @DisplayName("Test setVehicles with null")
    void testSetVehiclesWithNull() {
        systemState.setVehicles(vehicles);
        systemState.setVehicles(null);
        assertNotNull(systemState.getVehicles());
        assertTrue(systemState.getVehicles().isEmpty());
    }

    @Test
    @DisplayName("Test setBaseStations")
    void testSetBaseStations() {
        systemState.setBaseStations(baseStations);
        assertEquals(1, systemState.getBaseStations().size());
        assertEquals(baseStations.get(0).getId(), systemState.getBaseStations().get(0).getId());
    }

    @Test
    @DisplayName("Test setBaseStations with null")
    void testSetBaseStationsWithNull() {
        systemState.setBaseStations(baseStations);
        systemState.setBaseStations(null);
        assertNotNull(systemState.getBaseStations());
        assertTrue(systemState.getBaseStations().isEmpty());
    }

    @Test
    @DisplayName("Test setAuthorizations")
    void testSetAuthorizations() {
        systemState.setAuthorizations(authorizations);
        assertEquals(1, systemState.getAuthorizations().size());
        assertTrue(systemState.getAuthorizations().containsKey("V1"));
    }

    @Test
    @DisplayName("Test setAuthorizations with null")
    void testSetAuthorizationsWithNull() {
        systemState.setAuthorizations(authorizations);
        systemState.setAuthorizations(null);
        assertNotNull(systemState.getAuthorizations());
        assertTrue(systemState.getAuthorizations().isEmpty());
    }

    @Test
    @DisplayName("Test getVehicles returns copy")
    void testGetVehiclesReturnsCopy() {
        systemState.setVehicles(vehicles);
        List<Vehicle> vehicles1 = systemState.getVehicles();
        List<Vehicle> vehicles2 = systemState.getVehicles();
        
        assertNotSame(vehicles1, vehicles2);
        assertEquals(vehicles1.size(), vehicles2.size());
    }

    @Test
    @DisplayName("Test getBaseStations returns copy")
    void testGetBaseStationsReturnsCopy() {
        systemState.setBaseStations(baseStations);
        List<BaseStation> stations1 = systemState.getBaseStations();
        List<BaseStation> stations2 = systemState.getBaseStations();
        
        assertNotSame(stations1, stations2);
        assertEquals(stations1.size(), stations2.size());
    }

    @Test
    @DisplayName("Test getAuthorizations returns copy")
    void testGetAuthorizationsReturnsCopy() {
        systemState.setAuthorizations(authorizations);
        Map<String, FlightAuthorization> auth1 = systemState.getAuthorizations();
        Map<String, FlightAuthorization> auth2 = systemState.getAuthorizations();
        
        assertNotSame(auth1, auth2);
        assertEquals(auth1.size(), auth2.size());
    }

    @Test
    @DisplayName("Test setCenterId")
    void testSetCenterId() {
        systemState.setCenterId("CENTER-123");
        assertEquals("CENTER-123", systemState.getCenterId());
    }

    @Test
    @DisplayName("Test setOperational")
    void testSetOperational() {
        systemState.setOperational(true);
        assertTrue(systemState.isOperational());
        
        systemState.setOperational(false);
        assertFalse(systemState.isOperational());
    }

    @Test
    @DisplayName("Test setTimestamp")
    void testSetTimestamp() {
        long timestamp = System.currentTimeMillis();
        systemState.setTimestamp(timestamp);
        assertEquals(timestamp, systemState.getTimestamp());
    }
}

