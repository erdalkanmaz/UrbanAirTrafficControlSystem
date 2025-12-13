package com.airtraffic.map;

import com.airtraffic.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CityMap class
 * Tests city map management, safety checks, and obstacle/restricted zone handling
 */
@DisplayName("CityMap Tests")
class CityMapTest {

    private CityMap cityMap;
    private Position centerPosition;
    private Position safePosition;
    private Position unsafePosition;

    @BeforeEach
    void setUp() {
        cityMap = new CityMap("Istanbul");
        centerPosition = new Position(41.0082, 28.9784, 100.0);
        safePosition = new Position(41.0090, 28.9790, 150.0);
        unsafePosition = new Position(40.0, 30.0, 100.0); // Outside boundaries
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        CityMap map = new CityMap();
        
        assertNull(map.getCityName());
        assertNotNull(map.getRouteNetwork());
        assertNotNull(map.getObstacles());
        assertNotNull(map.getRestrictedZones());
        assertTrue(map.getObstacles().isEmpty());
        assertTrue(map.getRestrictedZones().isEmpty());
    }

    @Test
    @DisplayName("Test constructor with city name")
    void testConstructorWithCityName() {
        CityMap map = new CityMap("Ankara");
        
        assertEquals("Ankara", map.getCityName());
        assertNotNull(map.getRouteNetwork());
        assertEquals("Ankara", map.getRouteNetwork().getCityName());
    }

    @Test
    @DisplayName("Test isPositionSafe with no obstacles or zones")
    void testIsPositionSafeNoObstacles() {
        // Set boundaries
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Position within boundaries should be safe
        assertTrue(cityMap.isPositionSafe(safePosition), 
            "Position within boundaries should be safe");
    }

    @Test
    @DisplayName("Test isPositionSafe with position outside boundaries")
    void testIsPositionSafeOutsideBoundaries() {
        cityMap.setMinLatitude(40.5);
        cityMap.setMaxLatitude(41.5);
        cityMap.setMinLongitude(28.5);
        cityMap.setMaxLongitude(29.5);
        
        // Position outside boundaries should not be safe
        assertFalse(cityMap.isPositionSafe(unsafePosition), 
            "Position outside boundaries should not be safe");
    }

    @Test
    @DisplayName("Test isPositionSafe with obstacle")
    void testIsPositionSafeWithObstacle() {
        // Set boundaries
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Create obstacle at safe position
        Obstacle obstacle = new Obstacle("Test Building", ObstacleType.BUILDING, safePosition, 50.0);
        obstacle.setRadius(100.0); // 100m radius
        cityMap.addObstacle(obstacle);
        
        // Position at obstacle should not be safe
        assertFalse(cityMap.isPositionSafe(safePosition), 
            "Position at obstacle should not be safe");
        
        // Position far from obstacle should be safe
        Position farPosition = new Position(41.0200, 29.0000, 150.0);
        assertTrue(cityMap.isPositionSafe(farPosition), 
            "Position far from obstacle should be safe");
    }

    @Test
    @DisplayName("Test isPositionSafe with restricted zone")
    void testIsPositionSafeWithRestrictedZone() {
        // Set boundaries
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Create restricted zone
        RestrictedZone zone = new RestrictedZone("Test Zone", RestrictedZoneType.GOVERNMENT);
        zone.setMinAltitude(0.0);
        zone.setMaxAltitude(200.0);
        // Create a simple square zone
        zone.addBoundaryPoint(new Position(41.0050, 28.9750, 0.0));
        zone.addBoundaryPoint(new Position(41.0100, 28.9750, 0.0));
        zone.addBoundaryPoint(new Position(41.0100, 28.9800, 0.0));
        zone.addBoundaryPoint(new Position(41.0050, 28.9800, 0.0));
        cityMap.addRestrictedZone(zone);
        
        // Position inside restricted zone should not be safe
        Position insideZone = new Position(41.0075, 28.9775, 100.0);
        assertFalse(cityMap.isPositionSafe(insideZone), 
            "Position inside restricted zone should not be safe");
        
        // Position outside restricted zone should be safe
        Position outsideZone = new Position(41.0200, 29.0000, 100.0);
        assertTrue(cityMap.isPositionSafe(outsideZone), 
            "Position outside restricted zone should be safe");
    }

    @Test
    @DisplayName("Test getSafePassageAltitude with no obstacles")
    void testGetSafePassageAltitudeNoObstacles() {
        Position testPosition = new Position(41.0082, 28.9784, 100.0);
        
        // With no obstacles, should return position altitude + 10m safety margin
        double safeAltitude = cityMap.getSafePassageAltitude(testPosition);
        assertEquals(110.0, safeAltitude, 0.01, 
            "Safe altitude should be position altitude + 10m with no obstacles");
    }

    @Test
    @DisplayName("Test getSafePassageAltitude with obstacle")
    void testGetSafePassageAltitudeWithObstacle() {
        Position testPosition = new Position(41.0082, 28.9784, 100.0);
        
        // Create obstacle near position
        Obstacle obstacle = new Obstacle("Test Building", ObstacleType.BUILDING, 
            new Position(41.0083, 28.9785, 50.0), 100.0); // 100m high building
        obstacle.setRadius(200.0); // 200m radius
        cityMap.addObstacle(obstacle);
        
        double safeAltitude = cityMap.getSafePassageAltitude(testPosition);
        // Should be obstacle top (50 + 100) + 10m safety = 160m
        assertEquals(160.0, safeAltitude, 0.01, 
            "Safe altitude should be obstacle top + 10m safety margin");
    }

    @Test
    @DisplayName("Test getSafePassageAltitude with multiple obstacles")
    void testGetSafePassageAltitudeWithMultipleObstacles() {
        Position testPosition = new Position(41.0082, 28.9784, 100.0);
        
        // Create multiple obstacles
        Obstacle obstacle1 = new Obstacle("Building 1", ObstacleType.BUILDING, 
            new Position(41.0083, 28.9785, 50.0), 80.0);
        obstacle1.setRadius(200.0);
        
        Obstacle obstacle2 = new Obstacle("Building 2", ObstacleType.BUILDING, 
            new Position(41.0084, 28.9786, 60.0), 120.0); // Taller building
        obstacle2.setRadius(200.0);
        
        cityMap.addObstacle(obstacle1);
        cityMap.addObstacle(obstacle2);
        
        double safeAltitude = cityMap.getSafePassageAltitude(testPosition);
        // Should be tallest obstacle top (60 + 120) + 10m = 190m
        assertEquals(190.0, safeAltitude, 0.01, 
            "Safe altitude should be tallest obstacle top + 10m");
    }

    @Test
    @DisplayName("Test addObstacle")
    void testAddObstacle() {
        Obstacle obstacle = new Obstacle("Test Building", ObstacleType.BUILDING, 
            centerPosition, 50.0);
        
        cityMap.addObstacle(obstacle);
        
        assertEquals(1, cityMap.getObstacles().size());
        assertEquals(obstacle, cityMap.getObstacles().get(0));
    }

    @Test
    @DisplayName("Test addRestrictedZone")
    void testAddRestrictedZone() {
        RestrictedZone zone = new RestrictedZone("Test Zone", RestrictedZoneType.MILITARY);
        
        cityMap.addRestrictedZone(zone);
        
        assertEquals(1, cityMap.getRestrictedZones().size());
        assertEquals(zone, cityMap.getRestrictedZones().get(0));
    }

    @Test
    @DisplayName("Test setCityName updates route network")
    void testSetCityNameUpdatesRouteNetwork() {
        cityMap.setCityName("Ankara");
        
        assertEquals("Ankara", cityMap.getCityName());
        assertEquals("Ankara", cityMap.getRouteNetwork().getCityName());
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        cityMap.setCityName("Bursa");
        cityMap.setCountry("Turkey");
        cityMap.setCenter(centerPosition);
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        assertEquals("Bursa", cityMap.getCityName());
        assertEquals("Turkey", cityMap.getCountry());
        assertEquals(centerPosition, cityMap.getCenter());
        assertEquals(40.0, cityMap.getMinLatitude(), 0.01);
        assertEquals(42.0, cityMap.getMaxLatitude(), 0.01);
        assertEquals(28.0, cityMap.getMinLongitude(), 0.01);
        assertEquals(30.0, cityMap.getMaxLongitude(), 0.01);
    }

    @Test
    @DisplayName("Test obstacles immutability")
    void testObstaclesImmutability() {
        Obstacle obstacle = new Obstacle("Test Building", ObstacleType.BUILDING, 
            centerPosition, 50.0);
        cityMap.addObstacle(obstacle);
        
        List<Obstacle> obstacles = cityMap.getObstacles();
        obstacles.clear(); // Try to modify
        
        // Original list should not be modified
        assertEquals(1, cityMap.getObstacles().size());
    }

    @Test
    @DisplayName("Test restricted zones immutability")
    void testRestrictedZonesImmutability() {
        RestrictedZone zone = new RestrictedZone("Test Zone", RestrictedZoneType.HOSPITAL);
        cityMap.addRestrictedZone(zone);
        
        List<RestrictedZone> zones = cityMap.getRestrictedZones();
        zones.clear(); // Try to modify
        
        // Original list should not be modified
        assertEquals(1, cityMap.getRestrictedZones().size());
    }

    @Test
    @DisplayName("Test setObstacles")
    void testSetObstacles() {
        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new Obstacle("Building 1", ObstacleType.BUILDING, centerPosition, 50.0));
        obstacles.add(new Obstacle("Building 2", ObstacleType.BUILDING, safePosition, 60.0));
        
        cityMap.setObstacles(obstacles);
        
        assertEquals(2, cityMap.getObstacles().size());
    }

    @Test
    @DisplayName("Test setRestrictedZones")
    void testSetRestrictedZones() {
        List<RestrictedZone> zones = new ArrayList<>();
        zones.add(new RestrictedZone("Zone 1", RestrictedZoneType.GOVERNMENT));
        zones.add(new RestrictedZone("Zone 2", RestrictedZoneType.MILITARY));
        
        cityMap.setRestrictedZones(zones);
        
        assertEquals(2, cityMap.getRestrictedZones().size());
    }

    @Test
    @DisplayName("Test setRouteNetwork")
    void testSetRouteNetwork() {
        RouteNetwork network = new RouteNetwork("Test City");
        cityMap.setRouteNetwork(network);
        
        assertEquals(network, cityMap.getRouteNetwork());
        assertEquals("Test City", cityMap.getRouteNetwork().getCityName());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        cityMap.setCityName("Istanbul");
        Obstacle obstacle = new Obstacle("Test Building", ObstacleType.BUILDING, 
            centerPosition, 50.0);
        cityMap.addObstacle(obstacle);
        
        RestrictedZone zone = new RestrictedZone("Test Zone", RestrictedZoneType.GOVERNMENT);
        cityMap.addRestrictedZone(zone);
        
        String str = cityMap.toString();
        
        assertTrue(str.contains("Istanbul"), "toString should contain city name");
        assertTrue(str.contains("1"), "toString should contain obstacle count");
        assertTrue(str.startsWith("CityMap["), "toString should start with 'CityMap['");
    }

    @Test
    @DisplayName("Test edge case: position at boundary")
    void testPositionAtBoundary() {
        cityMap.setMinLatitude(40.0);
        cityMap.setMaxLatitude(42.0);
        cityMap.setMinLongitude(28.0);
        cityMap.setMaxLongitude(30.0);
        
        // Position exactly at minimum boundary
        Position atMinBoundary = new Position(40.0, 28.0, 100.0);
        assertTrue(cityMap.isPositionSafe(atMinBoundary), 
            "Position at minimum boundary should be safe");
        
        // Position exactly at maximum boundary
        Position atMaxBoundary = new Position(42.0, 30.0, 100.0);
        assertTrue(cityMap.isPositionSafe(atMaxBoundary), 
            "Position at maximum boundary should be safe");
    }

    @Test
    @DisplayName("Test edge case: empty city map")
    void testEmptyCityMap() {
        CityMap emptyMap = new CityMap();
        Position testPosition = new Position(41.0082, 28.9784, 100.0);
        
        // Without boundaries set, position should be considered safe
        // (boundaries check will fail, but no obstacles/zones)
        // Actually, if boundaries are not set (0.0), position might be outside
        // Let's set boundaries first
        emptyMap.setMinLatitude(40.0);
        emptyMap.setMaxLatitude(42.0);
        emptyMap.setMinLongitude(28.0);
        emptyMap.setMaxLongitude(30.0);
        
        assertTrue(emptyMap.isPositionSafe(testPosition), 
            "Position should be safe in empty city map with proper boundaries");
    }
}






