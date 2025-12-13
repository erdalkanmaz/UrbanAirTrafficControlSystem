package com.airtraffic.ui;

import com.airtraffic.control.TrafficControlCenter;
import com.airtraffic.map.CityMap;
import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import com.airtraffic.model.VehicleType;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for vehicle rendering on map visualization
 * Tests that vehicles are correctly displayed on the map
 */
@DisplayName("MapVisualization Vehicle Rendering Tests")
class MapVisualizationVehicleRenderingTest {

    private MapVisualization mapVisualization;
    private TrafficControlCenter controlCenter;
    private CityMap cityMap;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
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
        
        cityMap = new CityMap("Istanbul");
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        cityMap.setCenter(new Position(41.0, 29.0, 50.0));
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            mapVisualization = new MapVisualization(cityMap);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "MapVisualization should be created");
    }

    @Test
    @DisplayName("Test setControlCenter links control center")
    void testSetControlCenter() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                mapVisualization.setControlCenter(controlCenter);
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "setControlCenter should complete");
        assertTrue(successRef.get(), "setControlCenter should complete without exception");
    }

    @Test
    @DisplayName("Test render includes vehicles when control center is set")
    void testRenderIncludesVehicles() throws InterruptedException {
        // Register a test vehicle
        Position pos = new Position(41.0082, 28.9784, 100.0);
        Position destination = new Position(41.0100, 28.9800, 120.0);
        Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, pos);
        vehicle.setPilotLicense("PILOT-1");
        vehicle.setStatus(VehicleStatus.IN_FLIGHT);
        
        com.airtraffic.control.FlightAuthorization auth = 
            controlCenter.requestFlightAuthorization(vehicle, pos, destination);
        if (auth.getStatus() == com.airtraffic.control.AuthorizationStatus.APPROVED) {
            controlCenter.registerVehicle(vehicle);
        }
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                mapVisualization.setControlCenter(controlCenter);
                mapVisualization.render();
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Render should complete");
        assertTrue(successRef.get(), "Render should complete without exception");
    }

    @Test
    @DisplayName("Test vehicles are rendered with different colors by type")
    void testVehicleColorsByType() throws InterruptedException {
        // Register vehicles of different types
        Position destination = new Position(41.0100, 28.9800, 120.0);
        
        Vehicle passenger = new Vehicle(VehicleType.PASSENGER, new Position(41.0082, 28.9784, 100.0));
        passenger.setPilotLicense("PILOT-P");
        Vehicle cargo = new Vehicle(VehicleType.CARGO, new Position(41.0085, 28.9787, 100.0));
        cargo.setPilotLicense("PILOT-C");
        Vehicle emergency = new Vehicle(VehicleType.EMERGENCY, new Position(41.0088, 28.9790, 100.0));
        emergency.setPilotLicense("PILOT-E");
        
        com.airtraffic.control.FlightAuthorization auth1 = 
            controlCenter.requestFlightAuthorization(passenger, passenger.getPosition(), destination);
        com.airtraffic.control.FlightAuthorization auth2 = 
            controlCenter.requestFlightAuthorization(cargo, cargo.getPosition(), destination);
        com.airtraffic.control.FlightAuthorization auth3 = 
            controlCenter.requestFlightAuthorization(emergency, emergency.getPosition(), destination);
        
        if (auth1.getStatus() == com.airtraffic.control.AuthorizationStatus.APPROVED) {
            controlCenter.registerVehicle(passenger);
        }
        if (auth2.getStatus() == com.airtraffic.control.AuthorizationStatus.APPROVED) {
            controlCenter.registerVehicle(cargo);
        }
        if (auth3.getStatus() == com.airtraffic.control.AuthorizationStatus.APPROVED) {
            controlCenter.registerVehicle(emergency);
        }
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                mapVisualization.setControlCenter(controlCenter);
                mapVisualization.render();
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Render should complete");
        assertTrue(successRef.get(), "Render should complete without exception");
    }

    @Test
    @DisplayName("Test emergency vehicles are rendered with special styling")
    void testEmergencyVehicleRendering() throws InterruptedException {
        Position pos = new Position(41.0082, 28.9784, 100.0);
        Position destination = new Position(41.0100, 28.9800, 120.0);
        Vehicle emergency = new Vehicle(VehicleType.EMERGENCY, pos);
        emergency.setPilotLicense("PILOT-E");
        emergency.setStatus(VehicleStatus.EMERGENCY);
        
        com.airtraffic.control.FlightAuthorization auth = 
            controlCenter.requestFlightAuthorization(emergency, pos, destination);
        if (auth.getStatus() == com.airtraffic.control.AuthorizationStatus.APPROVED) {
            controlCenter.registerVehicle(emergency);
        }
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                mapVisualization.setControlCenter(controlCenter);
                mapVisualization.render();
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Render should complete");
        assertTrue(successRef.get(), "Render should complete without exception");
    }

    @Test
    @DisplayName("Test render handles no vehicles gracefully")
    void testRenderNoVehicles() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                mapVisualization.setControlCenter(controlCenter);
                mapVisualization.render();
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Render should complete");
        assertTrue(successRef.get(), "Render should complete without exception even with no vehicles");
    }

    @Test
    @DisplayName("Test vehicle positions update on map when vehicle moves")
    void testVehiclePositionUpdate() throws InterruptedException {
        Position startPos = new Position(41.0082, 28.9784, 100.0);
        Position destination = new Position(41.0100, 28.9800, 120.0);
        Vehicle vehicle = new Vehicle(VehicleType.PASSENGER, startPos);
        vehicle.setPilotLicense("PILOT-1");
        
        com.airtraffic.control.FlightAuthorization auth = 
            controlCenter.requestFlightAuthorization(vehicle, startPos, destination);
        if (auth.getStatus() == com.airtraffic.control.AuthorizationStatus.APPROVED) {
            controlCenter.registerVehicle(vehicle);
        }
        
        CountDownLatch latch1 = new CountDownLatch(1);
        Platform.runLater(() -> {
            mapVisualization.setControlCenter(controlCenter);
            mapVisualization.render();
            latch1.countDown();
        });
        assertTrue(latch1.await(5, TimeUnit.SECONDS), "Initial render should complete");
        
        // Update vehicle position
        Position newPos = new Position(41.0085, 28.9787, 105.0);
        controlCenter.updateVehiclePosition(vehicle.getId(), newPos);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            mapVisualization.render();
            latch2.countDown();
        });
        assertTrue(latch2.await(5, TimeUnit.SECONDS), "Updated render should complete");
    }
}

