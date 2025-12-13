package com.airtraffic.ui;

import com.airtraffic.control.AuthorizationStatus;
import com.airtraffic.control.FlightAuthorization;
import com.airtraffic.control.TrafficControlCenter;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Map;

/**
 * Sistem durumu paneli
 * TrafficControlCenter'dan sistem durumu bilgilerini gösterir
 */
public class SystemStatusPanel {
    
    private VBox view;
    private TrafficControlCenter controlCenter;
    
    // Status labels
    private Label operationalStatusLabel;
    private Label vehicleCountLabel;
    private Label pendingAuthorizationsLabel;
    private Label approvedNotRegisteredLabel;
    private Label inFlightLabel;
    private Label landingLabel;
    private Label emergencyLabel;
    private Label baseStationCountLabel;
    private Label ruleEngineStatusLabel;
    private Label systemHealthLabel;
    
    private double width = 300.0;
    private double height = 200.0;
    
    /**
     * Default constructor
     */
    public SystemStatusPanel() {
        initializeView();
    }
    
    /**
     * Constructor with control center
     * @param controlCenter Traffic control center
     */
    public SystemStatusPanel(TrafficControlCenter controlCenter) {
        this.controlCenter = controlCenter;
        initializeView();
    }
    
    /**
     * Initialize the view component
     */
    private void initializeView() {
        view = new VBox(10);
        view.setPadding(new Insets(10));
        view.setPrefSize(width, height);
        view.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
        
        // Title
        Label titleLabel = new Label("System Status");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        view.getChildren().add(titleLabel);
        
        // Operational status
        operationalStatusLabel = new Label("Operational: Unknown");
        operationalStatusLabel.setStyle("-fx-font-size: 12px;");
        view.getChildren().add(operationalStatusLabel);
        
        // Vehicle count (total active)
        vehicleCountLabel = new Label("Active Vehicles: 0");
        vehicleCountLabel.setStyle("-fx-font-size: 12px;");
        view.getChildren().add(vehicleCountLabel);
        
        // Pending authorizations
        pendingAuthorizationsLabel = new Label("Pending Authorizations: 0");
        pendingAuthorizationsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #ff8800;");
        view.getChildren().add(pendingAuthorizationsLabel);
        
        // Approved but not registered
        approvedNotRegisteredLabel = new Label("Approved (Not Registered): 0");
        approvedNotRegisteredLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #0088ff;");
        view.getChildren().add(approvedNotRegisteredLabel);
        
        // In flight vehicles
        inFlightLabel = new Label("In Flight: 0");
        inFlightLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #00aa00;");
        view.getChildren().add(inFlightLabel);
        
        // Landing vehicles
        landingLabel = new Label("Landing: 0");
        landingLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #ff6600;");
        view.getChildren().add(landingLabel);
        
        // Emergency vehicles
        emergencyLabel = new Label("Emergency: 0");
        emergencyLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #ff0000; -fx-font-weight: bold;");
        view.getChildren().add(emergencyLabel);
        
        // Base station count
        baseStationCountLabel = new Label("Base Stations: 0");
        baseStationCountLabel.setStyle("-fx-font-size: 12px;");
        view.getChildren().add(baseStationCountLabel);
        
        // Rule engine status
        ruleEngineStatusLabel = new Label("Rule Engine: Active");
        ruleEngineStatusLabel.setStyle("-fx-font-size: 12px;");
        view.getChildren().add(ruleEngineStatusLabel);
        
        // System health
        systemHealthLabel = new Label("System Health: Good");
        systemHealthLabel.setStyle("-fx-font-size: 12px;");
        view.getChildren().add(systemHealthLabel);
    }
    
    /**
     * Get the view component
     * @return View VBox
     */
    public VBox getView() {
        return view;
    }
    
    /**
     * Set control center
     * @param controlCenter Traffic control center
     */
    public void setControlCenter(TrafficControlCenter controlCenter) {
        this.controlCenter = controlCenter;
        refresh();
    }
    
    /**
     * Refresh status information from control center
     */
    public void refresh() {
        if (controlCenter == null) {
            updateLabelsUnknown();
            return;
        }
        
        // Update operational status
        boolean isOperational = controlCenter.isOperational();
        operationalStatusLabel.setText("Operational: " + (isOperational ? "Yes" : "No"));
        operationalStatusLabel.setTextFill(isOperational ? Color.GREEN : Color.RED);
        
        // Update vehicle count (total active)
        int vehicleCount = controlCenter.getActiveVehicles().size();
        vehicleCountLabel.setText("Active Vehicles: " + vehicleCount);
        
        // Update authorization statuses
        Map<String, FlightAuthorization> authorizations = controlCenter.getAuthorizations();
        long pendingCount = authorizations.values().stream()
                .filter(auth -> auth.getStatus() == AuthorizationStatus.PENDING)
                .count();
        pendingAuthorizationsLabel.setText("Pending Authorizations: " + pendingCount);
        
        long approvedNotRegisteredCount = authorizations.values().stream()
                .filter(auth -> auth.getStatus() == AuthorizationStatus.APPROVED && auth.isValid())
                .filter(auth -> !controlCenter.getActiveVehicles().stream()
                        .anyMatch(v -> v.getId().equals(auth.getVehicleId())))
                .count();
        approvedNotRegisteredLabel.setText("Approved (Not Registered): " + approvedNotRegisteredCount);
        
        // Update vehicle status counts
        long inFlightCount = controlCenter.getActiveVehicles().stream()
                .filter(v -> v.getStatus() == VehicleStatus.IN_FLIGHT)
                .count();
        inFlightLabel.setText("In Flight: " + inFlightCount);
        
        long landingCount = controlCenter.getActiveVehicles().stream()
                .filter(v -> v.getStatus() == VehicleStatus.LANDING)
                .count();
        landingLabel.setText("Landing: " + landingCount);
        
        long emergencyCount = controlCenter.getActiveVehicles().stream()
                .filter(v -> v.getStatus() == VehicleStatus.EMERGENCY)
                .count();
        emergencyLabel.setText("Emergency: " + emergencyCount);
        
        // Update base station count
        int baseStationCount = controlCenter.getBaseStations().size();
        baseStationCountLabel.setText("Base Stations: " + baseStationCount);
        
        // Update rule engine status
        boolean ruleEngineActive = controlCenter.getRuleEngine() != null;
        ruleEngineStatusLabel.setText("Rule Engine: " + (ruleEngineActive ? "Active" : "Inactive"));
        ruleEngineStatusLabel.setTextFill(ruleEngineActive ? Color.GREEN : Color.RED);
        
        // Update system health
        String healthStatus = calculateSystemHealth(isOperational, vehicleCount, baseStationCount);
        systemHealthLabel.setText("System Health: " + healthStatus);
        systemHealthLabel.setTextFill(getHealthColor(healthStatus));
    }
    
    /**
     * Update labels when control center is unknown
     */
    private void updateLabelsUnknown() {
        operationalStatusLabel.setText("Operational: Unknown");
        operationalStatusLabel.setTextFill(Color.GRAY);
        vehicleCountLabel.setText("Active Vehicles: Unknown");
        pendingAuthorizationsLabel.setText("Pending Authorizations: Unknown");
        approvedNotRegisteredLabel.setText("Approved (Not Registered): Unknown");
        inFlightLabel.setText("In Flight: Unknown");
        landingLabel.setText("Landing: Unknown");
        emergencyLabel.setText("Emergency: Unknown");
        baseStationCountLabel.setText("Base Stations: Unknown");
        ruleEngineStatusLabel.setText("Rule Engine: Unknown");
        ruleEngineStatusLabel.setTextFill(Color.GRAY);
        systemHealthLabel.setText("System Health: Unknown");
        systemHealthLabel.setTextFill(Color.GRAY);
    }
    
    /**
     * Calculate system health status
     * @param isOperational Is system operational
     * @param vehicleCount Active vehicle count
     * @param baseStationCount Base station count
     * @return Health status string
     */
    private String calculateSystemHealth(boolean isOperational, int vehicleCount, int baseStationCount) {
        if (!isOperational) {
            return "Critical";
        }
        
        if (baseStationCount == 0) {
            return "Warning";
        }
        
        // System is operational and has base stations
        return "Good";
    }
    
    /**
     * Get color for health status
     * @param healthStatus Health status string
     * @return Color
     */
    private Color getHealthColor(String healthStatus) {
        switch (healthStatus.toLowerCase()) {
            case "good":
                return Color.GREEN;
            case "warning":
                return Color.ORANGE;
            case "critical":
                return Color.RED;
            default:
                return Color.GRAY;
        }
    }
    
    /**
     * Set view size
     * @param width Width
     * @param height Height
     */
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        view.setPrefSize(width, height);
    }
}

