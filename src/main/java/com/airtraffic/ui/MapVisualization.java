package com.airtraffic.ui;

import com.airtraffic.control.TrafficControlCenter;
import com.airtraffic.map.CityMap;
import com.airtraffic.map.Obstacle;
import com.airtraffic.map.RestrictedZone;
import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import com.airtraffic.model.VehicleType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Harita görselleştirme bileşeni
 * CityMap verilerini JavaFX Canvas üzerinde görselleştirir
 */
public class MapVisualization {
    
    private CityMap cityMap;
    private TrafficControlCenter controlCenter;
    private Pane view;
    private Canvas canvas;
    private GraphicsContext gc;
    
    // View state
    private double zoomLevel = 1.0;
    private double viewCenterX = 0.0;
    private double viewCenterY = 0.0;
    private double width = 800.0;
    private double height = 600.0;
    
    // Colors
    private static final Color CITY_BOUNDARY_COLOR = Color.BLUE;
    private static final Color OBSTACLE_COLOR = Color.RED;
    private static final Color RESTRICTED_ZONE_COLOR = Color.ORANGE;
    private static final Color BACKGROUND_COLOR = Color.LIGHTGRAY;
    
    // Vehicle colors by type
    private static final Color PASSENGER_VEHICLE_COLOR = Color.GREEN;
    private static final Color CARGO_VEHICLE_COLOR = Color.BLUE;
    private static final Color EMERGENCY_VEHICLE_COLOR = Color.RED;
    private static final Color DEFAULT_VEHICLE_COLOR = Color.GRAY;
    
    // Vehicle rendering
    private static final double VEHICLE_RADIUS = 5.0; // Base radius in pixels
    private static final double EMERGENCY_VEHICLE_RADIUS = 7.0; // Larger for emergency
    
    /**
     * Default constructor
     */
    public MapVisualization() {
        initializeView();
    }
    
    /**
     * Constructor with city map
     * @param cityMap City map to visualize
     */
    public MapVisualization(CityMap cityMap) {
        this.cityMap = cityMap;
        initializeView();
    }
    
    /**
     * Initialize the view component
     */
    private void initializeView() {
        view = new Pane();
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        
        view.getChildren().add(canvas);
        view.setPrefSize(width, height);
        
        // Bind canvas size to view size
        canvas.widthProperty().bind(view.widthProperty());
        canvas.heightProperty().bind(view.heightProperty());
        
        // Redraw when canvas size changes
        canvas.widthProperty().addListener((obs, oldVal, newVal) -> render());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> render());
        
        // Mouse wheel zoom support
        view.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            if (deltaY > 0) {
                // Scroll up - zoom in
                zoomIn();
            } else if (deltaY < 0) {
                // Scroll down - zoom out
                zoomOut();
            }
        });
    }
    
    /**
     * Set city map to visualize
     * @param cityMap City map
     */
    public void setCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
        if (cityMap != null && cityMap.getCenter() != null) {
            // Initialize view center to city center
            Position center = cityMap.getCenter();
            viewCenterX = center.getLongitude();
            viewCenterY = center.getLatitude();
        }
        render();
    }
    
    /**
     * Set traffic control center for vehicle rendering
     * @param controlCenter Traffic control center
     */
    public void setControlCenter(TrafficControlCenter controlCenter) {
        this.controlCenter = controlCenter;
        render();
    }
    
    /**
     * Get current city map
     * @return City map
     */
    public CityMap getCityMap() {
        return cityMap;
    }
    
    /**
     * Get the view component (Pane)
     * @return View pane
     */
    public Pane getView() {
        return view;
    }
    
    /**
     * Get the canvas component
     * @return Canvas
     */
    public Canvas getCanvas() {
        return canvas;
    }
    
    /**
     * Render the map visualization
     */
    public void render() {
        if (canvas == null || gc == null) {
            return;
        }
        
        // Clear canvas
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        if (cityMap == null) {
            return;
        }
        
        // Calculate coordinate transformation
        // Use actual canvas size, or fallback to preferred size if canvas not yet sized
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        
        // If canvas not yet sized (width/height is 0), use view preferred size
        if (canvasWidth <= 0 || canvasHeight <= 0) {
            canvasWidth = view.getPrefWidth() > 0 ? view.getPrefWidth() : width;
            canvasHeight = view.getPrefHeight() > 0 ? view.getPrefHeight() : height;
        }
        
        // Draw city boundaries
        drawCityBoundaries(canvasWidth, canvasHeight);
        
        // Draw restricted zones
        drawRestrictedZones(canvasWidth, canvasHeight);
        
        // Draw obstacles
        drawObstacles(canvasWidth, canvasHeight);
        
        // Draw vehicles
        drawVehicles(canvasWidth, canvasHeight);
    }
    
    /**
     * Draw city boundaries
     */
    private void drawCityBoundaries(double canvasWidth, double canvasHeight) {
        if (cityMap.getMinLatitude() == 0.0 && cityMap.getMaxLatitude() == 0.0) {
            return; // No boundaries defined
        }
        
        gc.setStroke(CITY_BOUNDARY_COLOR);
        gc.setLineWidth(3.0); // Make it more visible
        
        // Simple coordinate transformation for city boundaries - fill entire canvas
        // City boundaries should always be visible, so use simple mapping
        double minX = 10.0; // Small margin
        double maxX = canvasWidth - 10.0;
        double minY = 10.0;
        double maxY = canvasHeight - 10.0;
        
        // Draw rectangle boundary
        gc.strokeRect(minX, minY, maxX - minX, maxY - minY);
    }
    
    /**
     * Draw obstacles
     */
    private void drawObstacles(double canvasWidth, double canvasHeight) {
        if (cityMap.getObstacles() == null || cityMap.getObstacles().isEmpty()) {
            return;
        }
        
        gc.setFill(OBSTACLE_COLOR);
        
        for (Obstacle obstacle : cityMap.getObstacles()) {
            Position pos = obstacle.getPosition();
            if (pos == null) {
                continue;
            }
            
            double x = lonToX(pos.getLongitude(), canvasWidth);
            double y = latToY(pos.getLatitude(), canvasHeight);
            
            // Draw circular obstacle if radius is set
            if (obstacle.getRadius() > 0) {
                double radius = obstacle.getRadius() * zoomLevel * 0.1; // Scale with zoom
                gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            } else if (obstacle.getWidth() > 0 && obstacle.getLength() > 0) {
                // Draw rectangular obstacle
                double width = obstacle.getWidth() * zoomLevel * 0.1;
                double length = obstacle.getLength() * zoomLevel * 0.1;
                gc.fillRect(x - width / 2, y - length / 2, width, length);
            } else {
                // Draw small point for obstacles without size
                gc.fillOval(x - 3, y - 3, 6, 6);
            }
        }
    }
    
    /**
     * Draw restricted zones
     */
    private void drawRestrictedZones(double canvasWidth, double canvasHeight) {
        if (cityMap.getRestrictedZones() == null || cityMap.getRestrictedZones().isEmpty()) {
            return;
        }
        
        gc.setFill(RESTRICTED_ZONE_COLOR);
        gc.setGlobalAlpha(0.3); // Semi-transparent
        
        for (RestrictedZone zone : cityMap.getRestrictedZones()) {
            if (zone.getBoundaries() == null || zone.getBoundaries().size() < 3) {
                continue;
            }
            
            // Draw polygon
            double[] xPoints = new double[zone.getBoundaries().size()];
            double[] yPoints = new double[zone.getBoundaries().size()];
            
            for (int i = 0; i < zone.getBoundaries().size(); i++) {
                Position pos = zone.getBoundaries().get(i);
                xPoints[i] = lonToX(pos.getLongitude(), canvasWidth);
                yPoints[i] = latToY(pos.getLatitude(), canvasHeight);
            }
            
            gc.fillPolygon(xPoints, yPoints, xPoints.length);
            
            // Draw outline
            gc.setStroke(RESTRICTED_ZONE_COLOR);
            gc.setGlobalAlpha(1.0);
            gc.setLineWidth(1.5);
            gc.strokePolygon(xPoints, yPoints, xPoints.length);
        }
        
        gc.setGlobalAlpha(1.0); // Reset alpha
    }
    
    /**
     * Convert longitude to X coordinate
     */
    private double lonToX(double longitude, double canvasWidth) {
        if (cityMap == null) {
            return 0.0;
        }
        
        double minLon = cityMap.getMinLongitude();
        double maxLon = cityMap.getMaxLongitude();
        
        if (maxLon == minLon) {
            return canvasWidth / 2.0;
        }
        
        double normalized = (longitude - minLon) / (maxLon - minLon);
        return normalized * canvasWidth * zoomLevel + (canvasWidth * (1 - zoomLevel)) / 2.0;
    }
    
    /**
     * Convert latitude to Y coordinate
     */
    private double latToY(double latitude, double canvasHeight) {
        if (cityMap == null) {
            return 0.0;
        }
        
        double minLat = cityMap.getMinLatitude();
        double maxLat = cityMap.getMaxLatitude();
        
        if (maxLat == minLat) {
            return canvasHeight / 2.0;
        }
        
        double normalized = (latitude - minLat) / (maxLat - minLat);
        // Y is inverted (0 is at top)
        return (1.0 - normalized) * canvasHeight * zoomLevel + (canvasHeight * (1 - zoomLevel)) / 2.0;
    }
    
    /**
     * Zoom in
     */
    public void zoomIn() {
        zoomLevel = Math.min(zoomLevel * 1.2, 5.0); // Max zoom 5x
        render();
    }
    
    /**
     * Zoom out
     */
    public void zoomOut() {
        zoomLevel = Math.max(zoomLevel / 1.2, 0.5); // Min zoom 0.5x
        render();
    }
    
    /**
     * Reset view to default
     */
    public void resetView() {
        zoomLevel = 1.0;
        if (cityMap != null && cityMap.getCenter() != null) {
            Position center = cityMap.getCenter();
            viewCenterX = center.getLongitude();
            viewCenterY = center.getLatitude();
        } else {
            viewCenterX = 0.0;
            viewCenterY = 0.0;
        }
        render();
    }
    
    /**
     * Pan the view
     * @param deltaX X offset in pixels
     * @param deltaY Y offset in pixels
     */
    public void pan(double deltaX, double deltaY) {
        // Convert pixel offset to lat/lon offset
        if (cityMap != null) {
            double lonRange = cityMap.getMaxLongitude() - cityMap.getMinLongitude();
            double latRange = cityMap.getMaxLatitude() - cityMap.getMinLatitude();
            
            double lonOffset = (deltaX / canvas.getWidth()) * lonRange / zoomLevel;
            double latOffset = -(deltaY / canvas.getHeight()) * latRange / zoomLevel; // Inverted
            
            viewCenterX += lonOffset;
            viewCenterY += latOffset;
        } else {
            viewCenterX += deltaX * 0.001;
            viewCenterY += deltaY * 0.001;
        }
        
        render();
    }
    
    /**
     * Set visualization size
     * @param width Width
     * @param height Height
     */
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        view.setPrefSize(width, height);
        // Unbind canvas before setting size
        canvas.widthProperty().unbind();
        canvas.heightProperty().unbind();
        canvas.setWidth(width);
        canvas.setHeight(height);
        // Rebind to view size
        canvas.widthProperty().bind(view.widthProperty());
        canvas.heightProperty().bind(view.heightProperty());
    }
    
    /**
     * Get current zoom level
     * @return Zoom level
     */
    public double getZoomLevel() {
        return zoomLevel;
    }
    
    /**
     * Get view center X (longitude)
     * @return View center X
     */
    public double getViewCenterX() {
        return viewCenterX;
    }
    
    /**
     * Get view center Y (latitude)
     * @return View center Y
     */
    public double getViewCenterY() {
        return viewCenterY;
    }
    
    /**
     * Draw vehicles on the map
     */
    private void drawVehicles(double canvasWidth, double canvasHeight) {
        if (controlCenter == null) {
            return;
        }
        
        java.util.List<Vehicle> vehicles = controlCenter.getActiveVehicles();
        if (vehicles == null || vehicles.isEmpty()) {
            return;
        }
        
        for (Vehicle vehicle : vehicles) {
            if (vehicle == null || vehicle.getPosition() == null) {
                continue;
            }
            
            Position pos = vehicle.getPosition();
            double x = lonToX(pos.getLongitude(), canvasWidth);
            double y = latToY(pos.getLatitude(), canvasHeight);
            
            // Skip if vehicle is outside visible area (with some margin)
            if (x < -50 || x > canvasWidth + 50 || y < -50 || y > canvasHeight + 50) {
                continue;
            }
            
            // Determine vehicle color based on type
            Color vehicleColor = getVehicleColor(vehicle);
            double radius = getVehicleRadius(vehicle);
            
            // Draw vehicle circle
            gc.setFill(vehicleColor);
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            
            // Draw outline
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.0);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
            
            // Draw direction indicator for vehicles in flight
            if (vehicle.getStatus() == VehicleStatus.IN_FLIGHT && vehicle.getHeading() != 0.0) {
                drawDirectionIndicator(x, y, vehicle.getHeading(), radius);
            }
        }
    }
    
    /**
     * Get vehicle color based on type
     */
    private Color getVehicleColor(Vehicle vehicle) {
        if (vehicle == null) {
            return DEFAULT_VEHICLE_COLOR;
        }
        
        VehicleType type = vehicle.getType();
        if (type == null) {
            return DEFAULT_VEHICLE_COLOR;
        }
        
        // Emergency vehicles always red, regardless of type
        if (vehicle.getStatus() == VehicleStatus.EMERGENCY) {
            return EMERGENCY_VEHICLE_COLOR;
        }
        
        switch (type) {
            case PASSENGER:
                return PASSENGER_VEHICLE_COLOR;
            case CARGO:
                return CARGO_VEHICLE_COLOR;
            case EMERGENCY:
                return EMERGENCY_VEHICLE_COLOR;
            default:
                return DEFAULT_VEHICLE_COLOR;
        }
    }
    
    /**
     * Get vehicle radius based on type and status
     */
    private double getVehicleRadius(Vehicle vehicle) {
        if (vehicle == null) {
            return VEHICLE_RADIUS;
        }
        
        // Emergency vehicles are larger
        if (vehicle.getStatus() == VehicleStatus.EMERGENCY || 
            vehicle.getType() == VehicleType.EMERGENCY) {
            return EMERGENCY_VEHICLE_RADIUS * zoomLevel;
        }
        
        return VEHICLE_RADIUS * zoomLevel;
    }
    
    /**
     * Draw direction indicator (arrow) for vehicle heading
     */
    private void drawDirectionIndicator(double x, double y, double heading, double radius) {
        if (heading == 0.0) {
            return; // No heading information
        }
        
        // Convert heading (degrees) to radians
        double headingRad = Math.toRadians(heading);
        
        // Calculate arrow tip position
        double arrowLength = radius * 1.5;
        double tipX = x + Math.sin(headingRad) * arrowLength;
        double tipY = y - Math.cos(headingRad) * arrowLength; // Negative because Y is inverted
        
        // Draw arrow line
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.strokeLine(x, y, tipX, tipY);
        
        // Draw arrow head (small triangle)
        double arrowHeadSize = 3.0;
        double angle1 = headingRad + Math.PI - Math.PI / 6; // 30 degrees
        double angle2 = headingRad + Math.PI + Math.PI / 6;
        
        double headX1 = tipX + Math.sin(angle1) * arrowHeadSize;
        double headY1 = tipY - Math.cos(angle1) * arrowHeadSize;
        double headX2 = tipX + Math.sin(angle2) * arrowHeadSize;
        double headY2 = tipY - Math.cos(angle2) * arrowHeadSize;
        
        gc.setFill(Color.WHITE);
        gc.fillPolygon(
            new double[]{tipX, headX1, headX2},
            new double[]{tipY, headY1, headY2},
            3
        );
    }
}

