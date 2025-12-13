package com.airtraffic.control;

import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Baz istasyonu - araçlarla iletişim ve takip için
 */
public class BaseStation {
    private String id;
    private String name;
    private Position position;              // Baz istasyonu konumu
    private double coverageRadius;          // Kapsama yarıçapı (metre)
    private boolean isActive;               // Aktif mi?
    private List<String> connectedVehicles; // Bağlı araçlar

    public BaseStation() {
        this.id = UUID.randomUUID().toString();
        this.connectedVehicles = new ArrayList<>();
        this.isActive = true;
        this.coverageRadius = 5000.0; // Varsayılan: 5km
    }

    public BaseStation(String name, Position position, double coverageRadius) {
        this();
        this.name = name;
        this.position = position;
        this.coverageRadius = coverageRadius;
    }

    /**
     * Bir aracın baz istasyonunun kapsama alanında olup olmadığını kontrol eder
     * @param vehicle Araç
     * @return Kapsama alanındaysa true
     */
    public boolean isInCoverage(Vehicle vehicle) {
        if (vehicle == null || vehicle.getPosition() == null) {
            return false;
        }
        double distance = position.horizontalDistanceTo(vehicle.getPosition());
        return distance <= coverageRadius;
    }

    /**
     * Araç bağlantısını ekler
     */
    public void connectVehicle(String vehicleId) {
        if (!connectedVehicles.contains(vehicleId)) {
            connectedVehicles.add(vehicleId);
        }
    }

    /**
     * Araç bağlantısını kaldırır
     */
    public void disconnectVehicle(String vehicleId) {
        connectedVehicles.remove(vehicleId);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public double getCoverageRadius() {
        return coverageRadius;
    }

    public void setCoverageRadius(double coverageRadius) {
        this.coverageRadius = coverageRadius;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<String> getConnectedVehicles() {
        return new ArrayList<>(connectedVehicles);
    }

    public int getConnectedVehicleCount() {
        return connectedVehicles.size();
    }
}











