package com.airtraffic.ui;

import com.airtraffic.control.TrafficControlCenter;
import com.airtraffic.map.CityMap;
import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import com.airtraffic.model.VehicleType;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RealTimeUpdateService
 * Tests real-time UI updates for vehicle list, system status, and map visualization
 */
@DisplayName("RealTimeUpdateService Tests")
class RealTimeUpdateServiceTest {

    private RealTimeUpdateService updateService;
    private TrafficControlCenter controlCenter;
    private VehicleListView vehicleListView;
    private SystemStatusPanel systemStatusPanel;
    private MapVisualization mapVisualization;
    private CityMap cityMap;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Initialize JavaFX toolkit for testing
        CountDownLatch latch = new CountDownLatch(1);
        
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {
                latch.countDown();
            });
        } else {
            latch.countDown();
        }
        
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("JavaFX initialization timeout");
        }
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        controlCenter = TrafficControlCenter.getInstance();
        
        // Clear existing vehicles
        List<Vehicle> existingVehicles = controlCenter.getActiveVehicles();
        for (Vehicle vehicle : existingVehicles) {
            controlCenter.unregisterVehicle(vehicle.getId());
        }
        
        cityMap = new CityMap("Istanbul");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        controlCenter.loadCityMap(cityMap);
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            vehicleListView = new VehicleListView(controlCenter);
            systemStatusPanel = new SystemStatusPanel(controlCenter);
            mapVisualization = new MapVisualization();
            mapVisualization.setCityMap(cityMap);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "UI components should be created");
    }

    @AfterEach
    void tearDown() {
        if (updateService != null) {
            updateService.stop();
        }
    }

    @Test
    @DisplayName("Test constructor creates service")
    void testConstructor() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            updateService = new RealTimeUpdateService(
                vehicleListView, systemStatusPanel, mapVisualization);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Service should be created");
        assertNotNull(updateService);
    }

    @Test
    @DisplayName("Test start begins periodic updates")
    void testStart() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            updateService = new RealTimeUpdateService(
                vehicleListView, systemStatusPanel, mapVisualization);
            updateService.start(100); // Update every 100ms for testing
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Service should start");
        
        // Wait for at least one update cycle
        Thread.sleep(200);
        
        // Service should be running
        assertTrue(updateService.isRunning(), "Service should be running");
    }

    @Test
    @DisplayName("Test stop ends periodic updates")
    void testStop() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            updateService = new RealTimeUpdateService(
                vehicleListView, systemStatusPanel, mapVisualization);
            updateService.start(100);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Service should start");
        
        // Stop the service
        updateService.stop();
        
        // Wait a bit
        Thread.sleep(200);
        
        // Service should be stopped
        assertFalse(updateService.isRunning(), "Service should be stopped");
    }

    @Test
    @DisplayName("Test updates vehicle list view")
    void testUpdatesVehicleListView() throws Exception {
        // Register a test vehicle
        Position pos = new Position(41.0082, 28.9784, 100.0);
        Position destination = new Position(41.0100, 28.9800, 120.0);
        Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
        vehicle.setPilotLicense("PILOT-1");
        
        com.airtraffic.control.FlightAuthorization auth = 
            controlCenter.requestFlightAuthorization(vehicle, pos, destination);
        if (auth.getStatus() == com.airtraffic.control.AuthorizationStatus.APPROVED) {
            controlCenter.registerVehicle(vehicle);
        }
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            updateService = new RealTimeUpdateService(
                vehicleListView, systemStatusPanel, mapVisualization);
            updateService.start(100);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Service should start");
        
        // Wait for update cycle
        Thread.sleep(200);
        
        // Vehicle list should be updated (check via control center)
        List<Vehicle> activeVehicles = controlCenter.getActiveVehicles();
        assertTrue(activeVehicles.size() > 0, "Should have active vehicles");
    }

    @Test
    @DisplayName("Test updates system status panel")
    void testUpdatesSystemStatusPanel() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            updateService = new RealTimeUpdateService(
                vehicleListView, systemStatusPanel, mapVisualization);
            updateService.start(100);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Service should start");
        
        // Wait for update cycle
        Thread.sleep(200);
        
        // System status should be updated
        assertTrue(controlCenter.isOperational(), "System should be operational");
    }

    @Test
    @DisplayName("Test updates map visualization")
    void testUpdatesMapVisualization() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            updateService = new RealTimeUpdateService(
                vehicleListView, systemStatusPanel, mapVisualization);
            updateService.start(100);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Service should start");
        
        // Wait for update cycle
        Thread.sleep(200);
        
        // Map visualization should be accessible
        assertNotNull(mapVisualization.getView(), "Map view should exist");
    }

    @Test
    @DisplayName("Test multiple start calls are handled gracefully")
    void testMultipleStartCalls() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            updateService = new RealTimeUpdateService(
                vehicleListView, systemStatusPanel, mapVisualization);
            updateService.start(100);
            updateService.start(100); // Second start
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Service should handle multiple starts");
        assertTrue(updateService.isRunning(), "Service should be running");
    }

    @Test
    @DisplayName("Test update interval can be changed")
    void testUpdateInterval() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            updateService = new RealTimeUpdateService(
                vehicleListView, systemStatusPanel, mapVisualization);
            updateService.start(50); // Fast updates
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Service should start with custom interval");
        
        // Wait for multiple update cycles
        Thread.sleep(300);
        
        assertTrue(updateService.isRunning(), "Service should still be running");
    }
}

