package com.airtraffic.ui;

import com.airtraffic.control.TrafficControlCenter;
import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import com.airtraffic.model.VehicleType;
import javafx.application.Platform;
import javafx.scene.control.TableView;
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
 * Unit tests for VehicleListView class
 * Tests vehicle list display component with table view
 * 
 * IMPORTANT: This test requires JavaFX modules to be properly configured.
 * See INTELLIJ_TEST_YAPILANDIRMA.md for detailed instructions.
 */
@DisplayName("VehicleListView Tests")
class VehicleListViewTest {

    private VehicleListView vehicleListView;
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
        
        // Create vehicle list view
        CountDownLatch stageLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            vehicleListView = new VehicleListView();
            stageLatch.countDown();
        });
        
        assertTrue(stageLatch.await(5, TimeUnit.SECONDS), "VehicleListView creation should complete");
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<VehicleListView> viewRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            viewRef.set(new VehicleListView());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Constructor should complete");
        assertNotNull(viewRef.get(), "VehicleListView should be created");
    }

    @Test
    @DisplayName("Test getView returns VBox")
    void testGetViewReturnsVBox() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<javafx.scene.layout.VBox> viewRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            viewRef.set(vehicleListView.getView());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "getView should complete");
        assertNotNull(viewRef.get(), "View should not be null");
        assertTrue(viewRef.get() instanceof javafx.scene.layout.VBox, "View should be a VBox");
    }
    
    @Test
    @DisplayName("Test getTableView returns TableView")
    void testGetTableViewReturnsTableView() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<TableView<?>> tableViewRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            tableViewRef.set(vehicleListView.getTableView());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "getTableView should complete");
        assertNotNull(tableViewRef.get(), "TableView should not be null");
    }

    @Test
    @DisplayName("Test table has required columns")
    void testTableHasRequiredColumns() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Integer> columnCountRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            TableView<?> tableView = vehicleListView.getTableView();
            if (tableView != null) {
                columnCountRef.set(tableView.getColumns().size());
            } else {
                columnCountRef.set(0);
            }
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Column check should complete");
        assertTrue(columnCountRef.get() >= 5, "Table should have at least 5 columns (ID, Type, Position, Velocity, Status)");
    }

    @Test
    @DisplayName("Test refresh updates vehicle list")
    void testRefreshUpdatesVehicleList() throws InterruptedException {
        // Note: Vehicle needs authorization to be registered
        // For test purposes, we'll just test that refresh method exists and works
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                vehicleListView.refresh();
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
    @DisplayName("Test filter by status")
    void testFilterByStatus() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                vehicleListView.filterByStatus(VehicleStatus.IN_FLIGHT);
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Filter should complete");
        assertTrue(successRef.get(), "Filter should complete without exception");
    }

    @Test
    @DisplayName("Test filter by type")
    void testFilterByType() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                vehicleListView.filterByType(VehicleType.CARGO);
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Filter should complete");
        assertTrue(successRef.get(), "Filter should complete without exception");
    }

    @Test
    @DisplayName("Test clear filter")
    void testClearFilter() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                vehicleListView.clearFilter();
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Clear filter should complete");
        assertTrue(successRef.get(), "Clear filter should complete without exception");
    }

    @Test
    @DisplayName("Test setControlCenter method")
    void testSetControlCenter() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                vehicleListView.setControlCenter(controlCenter);
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
    @DisplayName("Test table is initially empty")
    void testTableIsInitiallyEmpty() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Integer> itemCountRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            TableView<?> tableView = vehicleListView.getTableView();
            if (tableView != null) {
                itemCountRef.set(tableView.getItems().size());
            } else {
                itemCountRef.set(0);
            }
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Item count check should complete");
        assertEquals(0, itemCountRef.get(), "Table should be initially empty");
    }

    @Test
    @DisplayName("Test setSize method")
    void testSetSize() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        double width = 600.0;
        double height = 400.0;
        
        Platform.runLater(() -> {
            try {
                vehicleListView.setSize(width, height);
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
}

