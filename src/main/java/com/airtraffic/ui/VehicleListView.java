package com.airtraffic.ui;

import com.airtraffic.control.TrafficControlCenter;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;
import com.airtraffic.model.VehicleType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * Araç listesi görüntüleme bileşeni
 * TrafficControlCenter'dan aktif araçları alıp TableView'de gösterir
 */
public class VehicleListView {
    
    private TableView<Vehicle> tableView;
    private VBox view;
    private TrafficControlCenter controlCenter;
    private ObservableList<Vehicle> vehicleList;
    private FilteredList<Vehicle> filteredList;
    
    private VehicleStatus statusFilter;
    private VehicleType typeFilter;
    
    private double width = 600.0;
    private double height = 400.0;
    
    /**
     * Default constructor
     */
    public VehicleListView() {
        initializeView();
    }
    
    /**
     * Constructor with control center
     * @param controlCenter Traffic control center
     */
    public VehicleListView(TrafficControlCenter controlCenter) {
        this.controlCenter = controlCenter;
        initializeView();
    }
    
    /**
     * Initialize the view component
     */
    private void initializeView() {
        // Create table view
        tableView = new TableView<>();
        
        // Create columns
        createColumns();
        
        // Initialize vehicle list
        vehicleList = FXCollections.observableArrayList();
        filteredList = new FilteredList<>(vehicleList);
        tableView.setItems(filteredList);
        
        // Create main view
        view = new VBox(tableView);
        view.setPrefSize(width, height);
        
        // Set table to fill available space
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    /**
     * Create table columns
     */
    private void createColumns() {
        // ID Column
        TableColumn<Vehicle, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(150);
        
        // Type Column
        TableColumn<Vehicle, VehicleType> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setPrefWidth(100);
        
        // Position Column (Latitude)
        TableColumn<Vehicle, String> latColumn = new TableColumn<>("Latitude");
        latColumn.setCellValueFactory(cellData -> {
            Vehicle vehicle = cellData.getValue();
            if (vehicle != null && vehicle.getPosition() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    String.format("%.6f", vehicle.getPosition().getLatitude()));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        latColumn.setPrefWidth(100);
        
        // Position Column (Longitude)
        TableColumn<Vehicle, String> lonColumn = new TableColumn<>("Longitude");
        lonColumn.setCellValueFactory(cellData -> {
            Vehicle vehicle = cellData.getValue();
            if (vehicle != null && vehicle.getPosition() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    String.format("%.6f", vehicle.getPosition().getLongitude()));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        lonColumn.setPrefWidth(100);
        
        // Altitude Column
        TableColumn<Vehicle, Double> altitudeColumn = new TableColumn<>("Altitude (m)");
        altitudeColumn.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        altitudeColumn.setPrefWidth(100);
        
        // Velocity Column
        TableColumn<Vehicle, Double> velocityColumn = new TableColumn<>("Velocity (m/s)");
        velocityColumn.setCellValueFactory(new PropertyValueFactory<>("velocity"));
        velocityColumn.setPrefWidth(100);
        
        // Status Column
        TableColumn<Vehicle, VehicleStatus> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(100);
        
        // Fuel Level Column
        TableColumn<Vehicle, Double> fuelColumn = new TableColumn<>("Fuel (%)");
        fuelColumn.setCellValueFactory(new PropertyValueFactory<>("fuelLevel"));
        fuelColumn.setPrefWidth(80);
        
        // Add columns to table
        tableView.getColumns().addAll(
            idColumn, typeColumn, latColumn, lonColumn, altitudeColumn, 
            velocityColumn, statusColumn, fuelColumn
        );
    }
    
    /**
     * Get the view component
     * @return View VBox
     */
    public VBox getView() {
        return view;
    }
    
    /**
     * Get the table view
     * @return TableView
     */
    public TableView<Vehicle> getTableView() {
        return tableView;
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
     * Refresh vehicle list from control center
     */
    public void refresh() {
        if (controlCenter == null) {
            vehicleList.clear();
            return;
        }
        
        // Get active vehicles from control center
        java.util.List<Vehicle> activeVehicles = controlCenter.getActiveVehicles();
        
        // Update observable list
        vehicleList.clear();
        vehicleList.addAll(activeVehicles);
        
        // Apply filters
        updateFilter();
    }
    
    /**
     * Filter by status
     * @param status Vehicle status to filter
     */
    public void filterByStatus(VehicleStatus status) {
        this.statusFilter = status;
        updateFilter();
    }
    
    /**
     * Filter by type
     * @param type Vehicle type to filter
     */
    public void filterByType(VehicleType type) {
        this.typeFilter = type;
        updateFilter();
    }
    
    /**
     * Clear all filters
     */
    public void clearFilter() {
        this.statusFilter = null;
        this.typeFilter = null;
        updateFilter();
    }
    
    /**
     * Update filter predicate
     */
    private void updateFilter() {
        filteredList.setPredicate(vehicle -> {
            // Status filter
            if (statusFilter != null && vehicle.getStatus() != statusFilter) {
                return false;
            }
            
            // Type filter
            if (typeFilter != null && vehicle.getType() != typeFilter) {
                return false;
            }
            
            return true;
        });
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

