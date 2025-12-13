package com.airtraffic.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 3D konum bilgisi (latitude, longitude, altitude)
 * Havacılık standartlarına uygun konum gösterimi
 */
public class Position {
    private double latitude;      // Enlem (derece)
    private double longitude;     // Boylam (derece)
    private double altitude;      // Yükseklik (metre)
    private LocalDateTime timestamp; // Zaman damgası

    public Position() {
        this.timestamp = LocalDateTime.now();
    }

    public Position(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = LocalDateTime.now();
    }

    public Position(double latitude, double longitude, double altitude, LocalDateTime timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
    }

    /**
     * İki konum arasındaki yatay mesafeyi hesaplar (Haversine formülü)
     * @param other Diğer konum
     * @return Mesafe (metre)
     */
    public double horizontalDistanceTo(Position other) {
        final int EARTH_RADIUS_M = 6371000; // Dünya yarıçapı (metre)

        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_M * c;
    }

    /**
     * İki konum arasındaki dikey mesafeyi hesaplar
     * @param other Diğer konum
     * @return Dikey mesafe (metre)
     */
    public double verticalDistanceTo(Position other) {
        return Math.abs(this.altitude - other.altitude);
    }

    /**
     * İki konum arasındaki 3D mesafeyi hesaplar
     * @param other Diğer konum
     * @return 3D mesafe (metre)
     */
    public double distance3DTo(Position other) {
        double horizontal = horizontalDistanceTo(other);
        double vertical = verticalDistanceTo(other);
        return Math.sqrt(horizontal * horizontal + vertical * vertical);
    }

    // Getters and Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.latitude, latitude) == 0 &&
               Double.compare(position.longitude, longitude) == 0 &&
               Double.compare(position.altitude, altitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, altitude);
    }

    @Override
    public String toString() {
        return String.format("Position(%.6f, %.6f, %.2fm)", latitude, longitude, altitude);
    }
}











