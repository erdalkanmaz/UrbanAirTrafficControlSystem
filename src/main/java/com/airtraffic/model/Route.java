package com.airtraffic.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Rota tanımı - uçuş yolu
 */
public class Route {
    private String id;                      // Rota ID
    private String name;                    // Rota adı
    private List<Position> waypoints;       // Yol noktaları
    private double speedLimit;               // Hız limiti (m/s)
    private double minAltitude;              // Minimum yükseklik (metre)
    private double maxAltitude;              // Maksimum yükseklik (metre)
    private List<String> restrictions;      // Kısıtlamalar
    private boolean isActive;                // Aktif mi?

    public Route() {
        this.id = UUID.randomUUID().toString();
        this.waypoints = new ArrayList<>();
        this.restrictions = new ArrayList<>();
        this.isActive = true;
    }

    public Route(String name, List<Position> waypoints) {
        this();
        this.name = name;
        this.waypoints = new ArrayList<>(waypoints);
    }

    /**
     * Yol noktası ekler
     */
    public void addWaypoint(Position waypoint) {
        if (waypoint == null) {
            throw new IllegalArgumentException("Waypoint null olamaz");
        }
        this.waypoints.add(waypoint);
    }

    /**
     * Rota uzunluğunu hesaplar
     * @return Toplam mesafe (metre)
     */
    public double calculateTotalDistance() {
        if (waypoints.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            totalDistance += waypoints.get(i).horizontalDistanceTo(waypoints.get(i + 1));
        }
        return totalDistance;
    }

    /**
     * Belirli bir konumun rotaya yakınlığını kontrol eder
     * @param position Kontrol edilecek konum
     * @param threshold Mesafe eşiği (metre)
     * @return Rotaya yakınsa true
     */
    public boolean isNearRoute(Position position, double threshold) {
        for (Position waypoint : waypoints) {
            if (position.horizontalDistanceTo(waypoint) <= threshold) {
                return true;
            }
        }
        return false;
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

    public List<Position> getWaypoints() {
        return new ArrayList<>(waypoints);
    }

    public void setWaypoints(List<Position> waypoints) {
        this.waypoints = new ArrayList<>(waypoints);
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(double speedLimit) {
        if (speedLimit < 0) {
            throw new IllegalArgumentException("Hız limiti negatif olamaz");
        }
        this.speedLimit = speedLimit;
    }

    public double getMinAltitude() {
        return minAltitude;
    }

    public void setMinAltitude(double minAltitude) {
        this.minAltitude = minAltitude;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public List<String> getRestrictions() {
        return new ArrayList<>(restrictions);
    }

    public void setRestrictions(List<String> restrictions) {
        this.restrictions = new ArrayList<>(restrictions);
    }

    public void addRestriction(String restriction) {
        this.restrictions.add(restriction);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return String.format("Route[id=%s, name=%s, waypoints=%d, distance=%.2f m]",
                id, name, waypoints.size(), calculateTotalDistance());
    }
}











