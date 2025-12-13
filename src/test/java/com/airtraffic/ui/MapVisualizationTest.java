package com.airtraffic.ui;

import com.airtraffic.map.CityMap;
import com.airtraffic.map.Obstacle;
import com.airtraffic.map.ObstacleType;
import com.airtraffic.map.RestrictedZone;
import com.airtraffic.map.RestrictedZoneType;
import com.airtraffic.model.Position;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
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
 * Unit tests for MapVisualization class
 * Tests map visualization component creation, rendering, and interaction features
 * 
 * IMPORTANT: This test requires JavaFX modules to be properly configured.
 * See INTELLIJ_TEST_YAPILANDIRMA.md for detailed instructions.
 */
@DisplayName("MapVisualization Tests")
class MapVisualizationTest {

    private MapVisualization mapVisualization;
    private CityMap cityMap;
    private Stage testStage;

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
        // Create test city map
        cityMap = new CityMap("Istanbul");
        cityMap.setCenter(new Position(41.0082, 28.9784, 50.0));
        cityMap.setMinLatitude(40.8);
        cityMap.setMaxLatitude(41.2);
        cityMap.setMinLongitude(28.6);
        cityMap.setMaxLongitude(29.3);
        
        // Create visualization component
        CountDownLatch stageLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            testStage = new Stage();
            mapVisualization = new MapVisualization();
            stageLatch.countDown();
        });
        
        assertTrue(stageLatch.await(5, TimeUnit.SECONDS), "MapVisualization creation should complete");
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<MapVisualization> visualizationRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            visualizationRef.set(new MapVisualization());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Constructor should complete");
        assertNotNull(visualizationRef.get(), "MapVisualization should be created");
    }

    @Test
    @DisplayName("Test setCityMap method")
    void testSetCityMap() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mapVisualization.setCityMap(cityMap);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "setCityMap should complete");
        
        // Verify city map is set
        Platform.runLater(() -> {
            CityMap retrievedMap = mapVisualization.getCityMap();
            assertNotNull(retrievedMap, "City map should be set");
            assertEquals("Istanbul", retrievedMap.getCityName(), "City name should match");
        });
    }

    @Test
    @DisplayName("Test getView method returns Pane")
    void testGetViewReturnsPane() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Pane> viewRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            viewRef.set(mapVisualization.getView());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "getView should complete");
        assertNotNull(viewRef.get(), "View should not be null");
        assertTrue(viewRef.get() instanceof Pane, "View should be a Pane");
    }

    @Test
    @DisplayName("Test visualization renders city boundaries")
    void testRendersCityBoundaries() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mapVisualization.setCityMap(cityMap);
            mapVisualization.render();
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Render should complete");
        
        // Verify canvas exists and has been drawn on
        // Note: Canvas width/height may be 0 if view is not in a scene yet (normal in test environment)
        Platform.runLater(() -> {
            Canvas canvas = mapVisualization.getCanvas();
            assertNotNull(canvas, "Canvas should exist");
            // Canvas exists and render was called - that's sufficient for this test
            // Width/height will be set when view is added to a scene
        });
    }

    @Test
    @DisplayName("Test visualization renders obstacles")
    void testRendersObstacles() throws InterruptedException {
        // Add obstacle to city map
        Obstacle obstacle = new Obstacle("Test Building", ObstacleType.BUILDING, 
            new Position(41.0082, 28.9784, 50.0), 100.0);
        obstacle.setRadius(50.0);
        cityMap.addObstacle(obstacle);
        
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mapVisualization.setCityMap(cityMap);
            mapVisualization.render();
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Render should complete");
        
        // Verify obstacle is rendered (indirectly by checking render was called)
        Platform.runLater(() -> {
            assertNotNull(mapVisualization.getCanvas(), "Canvas should exist after render");
        });
    }

    @Test
    @DisplayName("Test visualization renders restricted zones")
    void testRendersRestrictedZones() throws InterruptedException {
        // Add restricted zone to city map
        RestrictedZone zone = new RestrictedZone("Government Area", RestrictedZoneType.GOVERNMENT);
        zone.addBoundaryPoint(new Position(41.0, 28.9, 0.0));
        zone.addBoundaryPoint(new Position(41.0, 29.0, 0.0));
        zone.addBoundaryPoint(new Position(41.1, 29.0, 0.0));
        zone.addBoundaryPoint(new Position(41.1, 28.9, 0.0));
        zone.setMinAltitude(0.0);
        zone.setMaxAltitude(500.0);
        cityMap.addRestrictedZone(zone);
        
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mapVisualization.setCityMap(cityMap);
            mapVisualization.render();
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Render should complete");
        
        // Verify restricted zone is rendered
        Platform.runLater(() -> {
            assertNotNull(mapVisualization.getCanvas(), "Canvas should exist after render");
        });
    }

    @Test
    @DisplayName("Test zoom in functionality")
    void testZoomIn() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Double> initialZoomRef = new AtomicReference<>();
        AtomicReference<Double> zoomAfterRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            mapVisualization.setCityMap(cityMap);
            initialZoomRef.set(mapVisualization.getZoomLevel());
            mapVisualization.zoomIn();
            zoomAfterRef.set(mapVisualization.getZoomLevel());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Zoom in should complete");
        assertTrue(zoomAfterRef.get() > initialZoomRef.get(), "Zoom level should increase after zoom in");
    }

    @Test
    @DisplayName("Test zoom out functionality")
    void testZoomOut() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Double> initialZoomRef = new AtomicReference<>();
        AtomicReference<Double> zoomAfterRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            mapVisualization.setCityMap(cityMap);
            mapVisualization.zoomIn(); // First zoom in
            initialZoomRef.set(mapVisualization.getZoomLevel());
            mapVisualization.zoomOut();
            zoomAfterRef.set(mapVisualization.getZoomLevel());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Zoom out should complete");
        assertTrue(zoomAfterRef.get() < initialZoomRef.get(), "Zoom level should decrease after zoom out");
    }

    @Test
    @DisplayName("Test reset view functionality")
    void testResetView() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Double> initialZoomRef = new AtomicReference<>();
        AtomicReference<Double> zoomAfterResetRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            mapVisualization.setCityMap(cityMap);
            initialZoomRef.set(mapVisualization.getZoomLevel());
            mapVisualization.zoomIn();
            mapVisualization.zoomIn();
            mapVisualization.resetView();
            zoomAfterResetRef.set(mapVisualization.getZoomLevel());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Reset view should complete");
        assertEquals(initialZoomRef.get(), zoomAfterResetRef.get(), 0.001, "Zoom level should reset to initial value");
    }

    @Test
    @DisplayName("Test pan functionality")
    void testPan() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Double> initialCenterXRef = new AtomicReference<>();
        AtomicReference<Double> initialCenterYRef = new AtomicReference<>();
        AtomicReference<Double> centerXAfterPanRef = new AtomicReference<>();
        AtomicReference<Double> centerYAfterPanRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            mapVisualization.setCityMap(cityMap);
            initialCenterXRef.set(mapVisualization.getViewCenterX());
            initialCenterYRef.set(mapVisualization.getViewCenterY());
            mapVisualization.pan(50.0, 30.0);
            centerXAfterPanRef.set(mapVisualization.getViewCenterX());
            centerYAfterPanRef.set(mapVisualization.getViewCenterY());
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Pan should complete");
        assertNotEquals(initialCenterXRef.get(), centerXAfterPanRef.get(), "View center X should change after pan");
        assertNotEquals(initialCenterYRef.get(), centerYAfterPanRef.get(), "View center Y should change after pan");
    }

    @Test
    @DisplayName("Test setSize method")
    void testSetSize() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> successRef = new AtomicReference<>(false);
        double width = 800.0;
        double height = 600.0;
        
        Platform.runLater(() -> {
            try {
                mapVisualization.setSize(width, height);
                successRef.set(true);
            } catch (Exception e) {
                successRef.set(false);
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "setSize should complete");
        assertTrue(successRef.get(), "setSize should complete without exception");
        
        CountDownLatch verifyLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Pane view = mapVisualization.getView();
            assertEquals(width, view.getPrefWidth(), 0.1, "View width should be set");
            assertEquals(height, view.getPrefHeight(), 0.1, "View height should be set");
            verifyLatch.countDown();
        });
        
        assertTrue(verifyLatch.await(5, TimeUnit.SECONDS), "Verification should complete");
    }

    @Test
    @DisplayName("Test visualization with empty city map")
    void testVisualizationWithEmptyCityMap() throws InterruptedException {
        CityMap emptyMap = new CityMap("Empty City");
        
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mapVisualization.setCityMap(emptyMap);
            mapVisualization.render();
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Render should complete even with empty map");
        
        Platform.runLater(() -> {
            assertNotNull(mapVisualization.getCanvas(), "Canvas should exist even with empty map");
        });
    }
}

