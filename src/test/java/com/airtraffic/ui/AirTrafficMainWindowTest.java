package com.airtraffic.ui;

import javafx.application.Platform;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AirTrafficMainWindow class
 * Tests main window creation, menu structure, and window management
 * 
 * IMPORTANT: This test requires JavaFX modules to be properly configured.
 * 
 * Configuration in IntelliJ IDEA:
 * 1. Run → Edit Configurations...
 * 2. Select "AirTrafficMainWindowTest"
 * 3. Add to VM options:
 *    --module-path "C:\javafx-sdk-17.0.10\lib" --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics --add-opens javafx.base/javafx.util=ALL-UNNAMED --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
 * 
 * Replace "C:\javafx-sdk-17.0.10\lib" with your JavaFX SDK path.
 * Download JavaFX SDK from: https://openjfx.io/
 * 
 * See INTELLIJ_TEST_YAPILANDIRMA.md for detailed instructions.
 */
@DisplayName("AirTrafficMainWindow Tests")
class AirTrafficMainWindowTest {

    private AirTrafficMainWindow mainWindow;
    private Stage testStage;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Initialize JavaFX toolkit for testing
        // This requires proper module path configuration in IntelliJ IDEA
        CountDownLatch latch = new CountDownLatch(1);
        
        // Initialize JavaFX toolkit using Platform.startup()
        // This is the proper way for Java 9+ module system
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {
                latch.countDown();
            });
        } else {
            latch.countDown();
        }
        
        // Wait for initialization to complete
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("JavaFX initialization timeout. " +
                "Make sure VM options include: --module-path <javafx-path> --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics");
        }
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        // Create new window instance for each test
        mainWindow = new AirTrafficMainWindow();
        
        // Stage must be created on JavaFX Application Thread
        CountDownLatch stageLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            testStage = new Stage();
            stageLatch.countDown();
        });
        
        // Wait for Stage creation to complete
        assertTrue(stageLatch.await(5, TimeUnit.SECONDS), "Stage creation should complete");
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        AirTrafficMainWindow window = new AirTrafficMainWindow();
        
        assertNotNull(window, "Window should be created");
    }

    @Test
    @DisplayName("Test window creation and initialization")
    void testWindowCreation() {
        assertNotNull(mainWindow, "Main window should be created");
    }

    @Test
    @DisplayName("Test start method creates scene")
    void testStartCreatesScene() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mainWindow.start(testStage);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX operations should complete");
        
        assertNotNull(testStage.getScene(), "Scene should be created");
        assertNotNull(testStage.getTitle(), "Window should have a title");
    }

    @Test
    @DisplayName("Test window title")
    void testWindowTitle() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mainWindow.start(testStage);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX operations should complete");
        
        String title = testStage.getTitle();
        assertNotNull(title, "Window title should not be null");
        assertFalse(title.isEmpty(), "Window title should not be empty");
        assertEquals("Air Traffic Control System", title, "Window title should match");
    }

    @Test
    @DisplayName("Test window is shown")
    void testWindowIsShown() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mainWindow.start(testStage);
            testStage.show();
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX operations should complete");
        
        assertTrue(testStage.isShowing(), "Window should be showing");
    }

    @Test
    @DisplayName("Test window size")
    void testWindowSize() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mainWindow.start(testStage);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX operations should complete");
        
        assertTrue(testStage.getWidth() > 0, "Window should have width");
        assertTrue(testStage.getHeight() > 0, "Window should have height");
    }

    @Test
    @DisplayName("Test window is resizable")
    void testWindowIsResizable() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mainWindow.start(testStage);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX operations should complete");
        
        // Window should be resizable by default
        assertTrue(testStage.isResizable(), "Window should be resizable");
    }

    @Test
    @DisplayName("Test menu bar exists")
    void testMenuBarExists() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mainWindow.start(testStage);
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX operations should complete");
        
        // Menu bar should be present in the scene
        assertNotNull(testStage.getScene(), "Scene should exist");
        MenuBar menuBar = mainWindow.getMenuBar();
        assertNotNull(menuBar, "Menu bar should exist");
        assertTrue(menuBar.getMenus().size() > 0, "Menu bar should have menus");
    }

    @Test
    @DisplayName("Test window close")
    void testWindowClose() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mainWindow.start(testStage);
            testStage.show();
            testStage.close();
            latch.countDown();
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX operations should complete");
        
        assertFalse(testStage.isShowing(), "Window should be closed");
    }

    @Test
    @DisplayName("Test multiple window instances")
    void testMultipleWindowInstances() {
        AirTrafficMainWindow window1 = new AirTrafficMainWindow();
        AirTrafficMainWindow window2 = new AirTrafficMainWindow();
        
        assertNotSame(window1, window2, "Should create separate window instances");
    }
}

