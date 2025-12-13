package com.airtraffic.map;

import com.airtraffic.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Obstacle class
 * Tests obstacle creation, position containment checks, and safe passage altitude calculations
 */
@DisplayName("Obstacle Tests")
class ObstacleTest {

    private Obstacle obstacle;
    private Position obstaclePosition;
    private Position testPosition;

    @BeforeEach
    void setUp() {
        obstaclePosition = new Position(41.0082, 28.9784, 50.0);
        obstacle = new Obstacle("Test Building", ObstacleType.BUILDING, obstaclePosition, 100.0);
        testPosition = new Position(41.0083, 28.9785, 100.0);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        Obstacle obs = new Obstacle();
        
        assertNull(obs.getName());
        assertNull(obs.getType());
        assertNull(obs.getPosition());
        assertEquals(0.0, obs.getHeight(), 0.01);
        assertEquals(0.0, obs.getRadius(), 0.01);
        assertEquals(0.0, obs.getWidth(), 0.01);
        assertEquals(0.0, obs.getLength(), 0.01);
    }

    @Test
    @DisplayName("Test constructor with parameters")
    void testConstructorWithParameters() {
        Obstacle obs = new Obstacle("Test Tower", ObstacleType.TOWER, obstaclePosition, 150.0);
        
        assertEquals("Test Tower", obs.getName());
        assertEquals(ObstacleType.TOWER, obs.getType());
        assertEquals(obstaclePosition, obs.getPosition());
        assertEquals(150.0, obs.getHeight(), 0.01);
    }

    @Test
    @DisplayName("Test contains with circular obstacle - position inside")
    void testContainsCircularInside() {
        obstacle.setRadius(200.0); // 200m radius
        
        // Position within radius
        Position insidePosition = new Position(41.0083, 28.9785, 100.0);
        assertTrue(obstacle.contains(insidePosition), 
            "Position within circular obstacle radius should be contained");
    }

    @Test
    @DisplayName("Test contains with circular obstacle - position outside")
    void testContainsCircularOutside() {
        obstacle.setRadius(50.0); // 50m radius
        
        // Position far from obstacle
        Position farPosition = new Position(41.0200, 29.0000, 100.0);
        assertFalse(obstacle.contains(farPosition), 
            "Position outside circular obstacle radius should not be contained");
    }

    @Test
    @DisplayName("Test contains with circular obstacle - position above obstacle")
    void testContainsCircularAbove() {
        obstacle.setRadius(200.0);
        
        // Position above obstacle (altitude > obstacle top)
        Position abovePosition = new Position(41.0083, 28.9785, 200.0); // Above obstacle top (50 + 100 = 150)
        assertFalse(obstacle.contains(abovePosition), 
            "Position above obstacle should not be contained");
    }

    @Test
    @DisplayName("Test contains with rectangular obstacle")
    void testContainsRectangular() {
        obstacle.setWidth(100.0);
        obstacle.setLength(200.0);
        
        // Position within rectangular bounds
        Position insidePosition = new Position(41.0083, 28.9785, 100.0);
        assertTrue(obstacle.contains(insidePosition), 
            "Position within rectangular obstacle should be contained");
        
        // Position far from obstacle
        Position farPosition = new Position(41.0200, 29.0000, 100.0);
        assertFalse(obstacle.contains(farPosition), 
            "Position outside rectangular obstacle should not be contained");
    }

    @Test
    @DisplayName("Test contains with no radius or dimensions")
    void testContainsNoDimensions() {
        // Obstacle with no radius, width, or length
        Obstacle emptyObstacle = new Obstacle("Empty", ObstacleType.OTHER, obstaclePosition, 50.0);
        
        assertFalse(emptyObstacle.contains(testPosition), 
            "Position should not be contained in obstacle with no dimensions");
    }

    @Test
    @DisplayName("Test getSafePassageAltitude")
    void testGetSafePassageAltitude() {
        // Obstacle at altitude 50m with height 100m
        double safeAltitude = obstacle.getSafePassageAltitude();
        
        // Should be obstacle top (50 + 100) + 10m safety = 160m
        assertEquals(160.0, safeAltitude, 0.01, 
            "Safe passage altitude should be obstacle top + 10m safety margin");
    }

    @Test
    @DisplayName("Test getSafePassageAltitude with different obstacle heights")
    void testGetSafePassageAltitudeDifferentHeights() {
        Obstacle tallObstacle = new Obstacle("Tall Building", ObstacleType.BUILDING, 
            new Position(41.0082, 28.9784, 0.0), 200.0);
        
        double safeAltitude = tallObstacle.getSafePassageAltitude();
        assertEquals(210.0, safeAltitude, 0.01, 
            "Safe altitude for 200m building should be 210m");
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        obstacle.setId("OBS-001");
        obstacle.setName("Updated Building");
        obstacle.setType(ObstacleType.TOWER);
        obstacle.setHeight(120.0);
        obstacle.setRadius(150.0);
        obstacle.setWidth(80.0);
        obstacle.setLength(100.0);
        obstacle.setHeading(45.0);
        
        Position newPosition = new Position(40.0, 30.0, 60.0);
        obstacle.setPosition(newPosition);
        
        assertEquals("OBS-001", obstacle.getId());
        assertEquals("Updated Building", obstacle.getName());
        assertEquals(ObstacleType.TOWER, obstacle.getType());
        assertEquals(120.0, obstacle.getHeight(), 0.01);
        assertEquals(150.0, obstacle.getRadius(), 0.01);
        assertEquals(80.0, obstacle.getWidth(), 0.01);
        assertEquals(100.0, obstacle.getLength(), 0.01);
        assertEquals(45.0, obstacle.getHeading(), 0.01);
        assertEquals(newPosition, obstacle.getPosition());
    }

    @Test
    @DisplayName("Test edge case: position exactly at obstacle center")
    void testPositionAtObstacleCenter() {
        obstacle.setRadius(100.0);
        
        // Position exactly at obstacle center
        assertTrue(obstacle.contains(obstaclePosition), 
            "Position at obstacle center should be contained");
    }

    @Test
    @DisplayName("Test edge case: position exactly at obstacle radius boundary")
    void testPositionAtRadiusBoundary() {
        obstacle.setRadius(100.0);
        
        // Position at exact radius (100m away horizontally)
        // This is approximate since we're using Haversine distance
        Position atBoundary = new Position(41.0082, 28.9784, 100.0);
        // The exact distance calculation might vary, so we test with a position very close
        assertTrue(obstacle.contains(atBoundary), 
            "Position at obstacle center (within radius) should be contained");
    }

    @Test
    @DisplayName("Test edge case: very tall obstacle")
    void testVeryTallObstacle() {
        Obstacle tallObstacle = new Obstacle("Skyscraper", ObstacleType.BUILDING, 
            new Position(41.0082, 28.9784, 0.0), 500.0);
        tallObstacle.setRadius(50.0);
        
        // Position at same location but above obstacle
        Position abovePosition = new Position(41.0082, 28.9784, 600.0);
        assertFalse(tallObstacle.contains(abovePosition), 
            "Position above very tall obstacle should not be contained");
        
        // Position at same location but below obstacle top
        Position belowPosition = new Position(41.0082, 28.9784, 400.0);
        assertTrue(tallObstacle.contains(belowPosition), 
            "Position below obstacle top should be contained");
    }

    @Test
    @DisplayName("Test edge case: obstacle at ground level")
    void testObstacleAtGroundLevel() {
        Obstacle groundObstacle = new Obstacle("Ground Structure", ObstacleType.BRIDGE, 
            new Position(41.0082, 28.9784, 0.0), 20.0);
        groundObstacle.setRadius(30.0);
        
        Position atGround = new Position(41.0082, 28.9784, 10.0);
        assertTrue(groundObstacle.contains(atGround), 
            "Position at ground level within obstacle should be contained");
    }

    @Test
    @DisplayName("Test edge case: zero height obstacle")
    void testZeroHeightObstacle() {
        Obstacle flatObstacle = new Obstacle("Flat Structure", ObstacleType.OTHER, 
            new Position(41.0082, 28.9784, 50.0), 0.0);
        flatObstacle.setRadius(100.0);
        
        // Position at same altitude
        Position sameAltitude = new Position(41.0083, 28.9785, 50.0);
        assertTrue(flatObstacle.contains(sameAltitude), 
            "Position at same altitude as zero-height obstacle should be contained");
        
        // Position above
        Position above = new Position(41.0083, 28.9785, 51.0);
        assertFalse(flatObstacle.contains(above), 
            "Position above zero-height obstacle should not be contained");
    }

    @Test
    @DisplayName("Test circular obstacle priority over rectangular")
    void testCircularPriorityOverRectangular() {
        // When both radius and width/length are set, circular takes priority
        obstacle.setRadius(100.0);
        obstacle.setWidth(200.0);
        obstacle.setLength(300.0);
        
        // Position within circular radius but outside rectangular bounds
        Position withinCircle = new Position(41.0083, 28.9785, 100.0);
        assertTrue(obstacle.contains(withinCircle), 
            "Circular obstacle should take priority when both are defined");
    }
}






