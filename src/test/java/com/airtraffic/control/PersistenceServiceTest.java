package com.airtraffic.control;

import com.airtraffic.model.*;
import com.airtraffic.map.CityMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PersistenceService class
 */
@DisplayName("PersistenceService Tests")
class PersistenceServiceTest {

    private PersistenceService persistenceService;
    private SystemState systemState;
    private CityMap cityMap;
    private List<Vehicle> vehicles;
    private List<BaseStation> baseStations;
    private Map<String, FlightAuthorization> authorizations;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        persistenceService = new PersistenceService();
        
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
        
        // Create system state
        systemState = new SystemState(cityMap, vehicles, baseStations, 
            authorizations, "CENTER-1", true);
    }

    @Test
    @DisplayName("Test saveState - valid file path")
    void testSaveStateValidFilePath() throws Exception {
        File testFile = tempDir.resolve("test_state.json").toFile();
        String filePath = testFile.getAbsolutePath();
        
        persistenceService.saveState(systemState, filePath);
        
        assertTrue(testFile.exists());
        assertTrue(testFile.length() > 0);
    }

    @Test
    @DisplayName("Test saveState - null system state throws exception")
    void testSaveStateNullSystemState() {
        File testFile = tempDir.resolve("test_state.json").toFile();
        String filePath = testFile.getAbsolutePath();
        
        assertThrows(IllegalArgumentException.class, () -> {
            persistenceService.saveState(null, filePath);
        });
    }

    @Test
    @DisplayName("Test saveState - null file path throws exception")
    void testSaveStateNullFilePath() {
        assertThrows(IllegalArgumentException.class, () -> {
            persistenceService.saveState(systemState, null);
        });
    }

    @Test
    @DisplayName("Test saveState - empty file path throws exception")
    void testSaveStateEmptyFilePath() {
        assertThrows(IllegalArgumentException.class, () -> {
            persistenceService.saveState(systemState, "");
        });
    }

    @Test
    @DisplayName("Test loadState - valid file path")
    void testLoadStateValidFilePath() throws Exception {
        // First save the state
        File testFile = tempDir.resolve("test_state.json").toFile();
        String filePath = testFile.getAbsolutePath();
        persistenceService.saveState(systemState, filePath);
        
        // Then load it
        SystemState loadedState = persistenceService.loadState(filePath);
        
        assertNotNull(loadedState);
        assertNotNull(loadedState.getCityMap());
        assertEquals("Istanbul", loadedState.getCityMap().getCityName());
        assertEquals(2, loadedState.getVehicles().size());
        assertEquals(1, loadedState.getBaseStations().size());
        assertEquals(1, loadedState.getAuthorizations().size());
        assertEquals("CENTER-1", loadedState.getCenterId());
        assertTrue(loadedState.isOperational());
    }

    @Test
    @DisplayName("Test loadState - file not found throws exception")
    void testLoadStateFileNotFound() {
        String nonExistentPath = tempDir.resolve("non_existent.json").toFile().getAbsolutePath();
        
        assertThrows(Exception.class, () -> {
            persistenceService.loadState(nonExistentPath);
        });
    }

    @Test
    @DisplayName("Test loadState - null file path throws exception")
    void testLoadStateNullFilePath() {
        assertThrows(IllegalArgumentException.class, () -> {
            persistenceService.loadState(null);
        });
    }

    @Test
    @DisplayName("Test loadState - empty file path throws exception")
    void testLoadStateEmptyFilePath() {
        assertThrows(IllegalArgumentException.class, () -> {
            persistenceService.loadState("");
        });
    }

    @Test
    @DisplayName("Test loadState - invalid JSON format throws exception")
    void testLoadStateInvalidJson() throws Exception {
        File testFile = tempDir.resolve("invalid.json").toFile();
        String filePath = testFile.getAbsolutePath();
        
        // Write invalid JSON
        java.nio.file.Files.write(testFile.toPath(), "invalid json content".getBytes());
        
        assertThrows(Exception.class, () -> {
            persistenceService.loadState(filePath);
        });
    }

    @Test
    @DisplayName("Test save and load - data integrity")
    void testSaveAndLoadDataIntegrity() throws Exception {
        File testFile = tempDir.resolve("test_state.json").toFile();
        String filePath = testFile.getAbsolutePath();
        
        // Save
        persistenceService.saveState(systemState, filePath);
        
        // Load
        SystemState loadedState = persistenceService.loadState(filePath);
        
        // Verify vehicles
        assertEquals(systemState.getVehicles().size(), loadedState.getVehicles().size());
        assertEquals(systemState.getVehicles().get(0).getId(), loadedState.getVehicles().get(0).getId());
        assertEquals(systemState.getVehicles().get(0).getType(), loadedState.getVehicles().get(0).getType());
        
        // Verify base stations
        assertEquals(systemState.getBaseStations().size(), loadedState.getBaseStations().size());
        assertEquals(systemState.getBaseStations().get(0).getId(), loadedState.getBaseStations().get(0).getId());
        
        // Verify authorizations
        assertEquals(systemState.getAuthorizations().size(), loadedState.getAuthorizations().size());
        assertTrue(loadedState.getAuthorizations().containsKey("V1"));
        
        // Verify city map
        assertEquals(systemState.getCityMap().getCityName(), loadedState.getCityMap().getCityName());
        assertEquals(systemState.getCityMap().getMinLatitude(), loadedState.getCityMap().getMinLatitude());
        assertEquals(systemState.getCityMap().getMaxLatitude(), loadedState.getCityMap().getMaxLatitude());
    }

    @Test
    @DisplayName("Test save and load - empty system state")
    void testSaveAndLoadEmptySystemState() throws Exception {
        SystemState emptyState = new SystemState();
        File testFile = tempDir.resolve("empty_state.json").toFile();
        String filePath = testFile.getAbsolutePath();
        
        persistenceService.saveState(emptyState, filePath);
        SystemState loadedState = persistenceService.loadState(filePath);
        
        assertNotNull(loadedState);
        assertNull(loadedState.getCityMap());
        assertTrue(loadedState.getVehicles().isEmpty());
        assertTrue(loadedState.getBaseStations().isEmpty());
        assertTrue(loadedState.getAuthorizations().isEmpty());
    }
}

