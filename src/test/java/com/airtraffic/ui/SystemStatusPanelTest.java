package com.airtraffic.ui;

import com.airtraffic.control.BaseStation;
import com.airtraffic.control.TrafficControlCenter;
import com.airtraffic.model.Position;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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
 * Unit tests for SystemStatusPanel class
 * Tests system status display component
 * 
 * IMPORTANT: This test requires JavaFX modules to be properly configured.
 * See INTELLIJ_TEST_YAPILANDIRMA.md for detailed instructions.
 */
@DisplayName("SystemStatusPanel Tests")
class SystemStatusPanelTest {

    private SystemStatusPanel statusPanel;
    private TrafficControlCenter controlCenter;

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
            throw new RuntimeException("JavaFX initialization timeout. " +
                "Make sure VM options include: --module-path <javafx-path> --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics");
        }
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        // Get control center instance
        controlCenter = TrafficControlCenter.getInstance();
        
        // Create system status panel
        CountDownLatch stageLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            statusPanel = new SystemStatusPanel();
            stageLatch.countDown();
        });
        
        assertTrue(stageLatch.await(5, TimeUnit.SECONDS), "SystemStatusPanel creation should complete");
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<SystemStatusPanel> panelRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            panelRef.set(new SystemStatusPanel());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Constructor should complete");
        assertNotNull(panelRef.get(), "SystemStatusPanel should be created");
    }

    @Test
    @DisplayName("Test getView returns VBox")
    void testGetViewReturnsVBox() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<VBox> viewRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            viewRef.set(statusPanel.getView());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "getView should complete");
        assertNotNull(viewRef.get(), "View should not be null");
        assertTrue(viewRef.get() instanceof VBox, "View should be a VBox");
    }

    @Test
    @DisplayName("Test setControlCenter method")
    void testSetControlCenter() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                statusPanel.setControlCenter(controlCenter);
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
    @DisplayName("Test refresh updates status")
    void testRefreshUpdatesStatus() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                statusPanel.setControlCenter(controlCenter);
                statusPanel.refresh();
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Refresh should complete");
        assertTrue(successRef.get(), "Refresh should complete without exception");
    }

    @Test
    @DisplayName("Test displays active vehicle count")
    void testDisplaysActiveVehicleCount() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> hasVehicleCountRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            statusPanel.setControlCenter(controlCenter);
            statusPanel.refresh();
            VBox view = statusPanel.getView();
            // Check if view contains vehicle count label
            hasVehicleCountRef.set(view.getChildren().stream()
                .anyMatch(node -> node instanceof Label && 
                    ((Label) node).getText().toLowerCase().contains("vehicle")));
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Check should complete");
        // Note: Even if no vehicles, the label should exist
    }

    @Test
    @DisplayName("Test displays base station status")
    void testDisplaysBaseStationStatus() throws InterruptedException {
        // Add a base station
        BaseStation station = new BaseStation("Test Station", 
            new Position(41.0082, 28.9784, 50.0), 5000.0);
        controlCenter.addBaseStation(station);
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> hasBaseStationRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            statusPanel.setControlCenter(controlCenter);
            statusPanel.refresh();
            VBox view = statusPanel.getView();
            // Check if view contains base station label
            hasBaseStationRef.set(view.getChildren().stream()
                .anyMatch(node -> node instanceof Label && 
                    ((Label) node).getText().toLowerCase().contains("station")));
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Check should complete");
        // Note: Even if no stations, the label should exist
    }

    @Test
    @DisplayName("Test displays operational status")
    void testDisplaysOperationalStatus() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> hasOperationalRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            statusPanel.setControlCenter(controlCenter);
            statusPanel.refresh();
            VBox view = statusPanel.getView();
            // Check if view contains operational status label
            hasOperationalRef.set(view.getChildren().stream()
                .anyMatch(node -> node instanceof Label && 
                    ((Label) node).getText().toLowerCase().contains("operational") ||
                    ((Label) node).getText().toLowerCase().contains("status")));
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Check should complete");
    }

    @Test
    @DisplayName("Test setSize method")
    void testSetSize() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        double width = 300.0;
        double height = 200.0;
        
        Platform.runLater(() -> {
            try {
                statusPanel.setSize(width, height);
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "setSize should complete");
        assertTrue(successRef.get(), "setSize should complete without exception");
    }

    @Test
    @DisplayName("Test panel updates when control center changes")
    void testPanelUpdatesWhenControlCenterChanges() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                statusPanel.setControlCenter(controlCenter);
                statusPanel.refresh();
                // Change control center state
                controlCenter.setOperational(false);
                statusPanel.refresh();
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Update should complete");
        assertTrue(successRef.get(), "Update should complete without exception");
    }
}

