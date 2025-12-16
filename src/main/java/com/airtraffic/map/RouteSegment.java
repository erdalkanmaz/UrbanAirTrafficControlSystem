package com.airtraffic.map;

import com.airtraffic.model.Position;
import com.airtraffic.model.Route;
import com.airtraffic.model.RouteDirection;

import java.util.UUID;

/**
 * Yol segmenti - bir rotanın belirli bir bölümü
 * Her segment için yön, yükseklik, hız limiti tanımlanır
 */
public class RouteSegment {
    private String segmentId;
    private Route parentRoute;           // Hangi rotaya ait
    private Position startPoint;         // Segment başlangıç noktası
    private Position endPoint;           // Segment bitiş noktası
    private RouteDirection direction;    // FORWARD (gidiş) veya REVERSE (geliş)
    private double altitude;             // Bu segment için sabit yükseklik (metre)
    private double speedLimit;             // Bu segment için hız limiti (m/s)
    private int maxVehicles;              // Bu segment için maksimum araç sayısı
    private boolean isActive;             // Segment aktif mi?
    
    public RouteSegment() {
        this.segmentId = UUID.randomUUID().toString();
        this.isActive = true;
        this.maxVehicles = 50; // Varsayılan kapasite
    }
    
    public RouteSegment(Route parentRoute, Position startPoint, Position endPoint, 
                       RouteDirection direction, double altitude, double speedLimit) {
        this();
        this.parentRoute = parentRoute;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.direction = direction;
        this.altitude = altitude;
        this.speedLimit = speedLimit;
    }
    
    /**
     * Segment uzunluğunu hesaplar
     * @return Segment uzunluğu (metre)
     */
    public double calculateLength() {
        if (startPoint == null || endPoint == null) {
            return 0.0;
        }
        return startPoint.horizontalDistanceTo(endPoint);
    }
    
    /**
     * Belirli bir konumun bu segment üzerinde olup olmadığını kontrol eder
     * @param position Kontrol edilecek konum
     * @param threshold Mesafe eşiği (metre)
     * @return Segment üzerindeyse true
     */
    public boolean isOnSegment(Position position, double threshold) {
        if (position == null || startPoint == null || endPoint == null) {
            return false;
        }
        
        // Segment üzerindeki en yakın noktaya mesafeyi hesapla
        double distanceToStart = position.horizontalDistanceTo(startPoint);
        double distanceToEnd = position.horizontalDistanceTo(endPoint);
        double segmentLength = calculateLength();
        
        // Eğer konum segment başlangıç/bitiş noktalarına yakınsa
        if (distanceToStart <= threshold || distanceToEnd <= threshold) {
            return true;
        }
        
        // Segment üzerindeki en yakın noktaya mesafe hesapla (basitleştirilmiş)
        // Gerçek implementasyonda doğru nokta-segment mesafesi hesaplanmalı
        double minDistance = Math.min(distanceToStart, distanceToEnd);
        return minDistance <= threshold;
    }
    
    // Getters and Setters
    public String getSegmentId() {
        return segmentId;
    }
    
    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }
    
    public Route getParentRoute() {
        return parentRoute;
    }
    
    public void setParentRoute(Route parentRoute) {
        this.parentRoute = parentRoute;
    }
    
    public Position getStartPoint() {
        return startPoint;
    }
    
    public void setStartPoint(Position startPoint) {
        this.startPoint = startPoint;
    }
    
    public Position getEndPoint() {
        return endPoint;
    }
    
    public void setEndPoint(Position endPoint) {
        this.endPoint = endPoint;
    }
    
    public RouteDirection getDirection() {
        return direction;
    }
    
    public void setDirection(RouteDirection direction) {
        this.direction = direction;
    }
    
    public double getAltitude() {
        return altitude;
    }
    
    public void setAltitude(double altitude) {
        if (altitude < 0) {
            throw new IllegalArgumentException("Altitude cannot be negative");
        }
        this.altitude = altitude;
    }
    
    public double getSpeedLimit() {
        return speedLimit;
    }
    
    public void setSpeedLimit(double speedLimit) {
        if (speedLimit < 0) {
            throw new IllegalArgumentException("Speed limit cannot be negative");
        }
        this.speedLimit = speedLimit;
    }
    
    public int getMaxVehicles() {
        return maxVehicles;
    }
    
    public void setMaxVehicles(int maxVehicles) {
        if (maxVehicles < 0) {
            throw new IllegalArgumentException("Max vehicles cannot be negative");
        }
        this.maxVehicles = maxVehicles;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return String.format("RouteSegment[id=%s, route=%s, direction=%s, altitude=%.2fm, speed=%.2fm/s]",
                segmentId, parentRoute != null ? parentRoute.getName() : "null", direction, altitude, speedLimit);
    }
}

