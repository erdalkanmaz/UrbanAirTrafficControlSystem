package com.airtraffic.map;

import java.util.ArrayList;
import java.util.List;

import com.airtraffic.model.Position;

/**
 * Yasak bölge tanımı (hükümet binaları, askeri bölgeler, hastaneler, vb.)
 */
public class RestrictedZone {
    private String id;
    private String name;
    private RestrictedZoneType type;
    private List<Position> boundaries;     // Bölge sınırları (kapalı polygon)
    private double minAltitude;            // Minimum yasak yükseklik
    private double maxAltitude;            // Maksimum yasak yükseklik
    private String restrictionReason;      // Kısıtlama nedeni
    private boolean permanent;             // Kalıcı mı yoksa geçici mi?

    public RestrictedZone() {
        this.boundaries = new ArrayList<>();
        this.permanent = true;
    }

    public RestrictedZone(String name, RestrictedZoneType type) {
        this();
        this.name = name;
        this.type = type;
    }

    /**
     * Belirli bir konumun yasak bölgenin içinde olup olmadığını kontrol eder
     * @param position Kontrol edilecek konum
     * @return Yasak bölgenin içindeyse true
     */
    public boolean contains(Position position) {
        if (boundaries.size() < 3) {
            return false; // Polygon için en az 3 nokta gerekli
        }

        // Yükseklik kontrolü
        if (position.getAltitude() < minAltitude || position.getAltitude() > maxAltitude) {
            return false;
        }

        // Point-in-polygon algoritması (Ray casting)
        boolean inside = false;
        int j = boundaries.size() - 1;

        for (int i = 0; i < boundaries.size(); i++) {
            Position pi = boundaries.get(i);
            Position pj = boundaries.get(j);

            if (((pi.getLatitude() > position.getLatitude()) != (pj.getLatitude() > position.getLatitude())) &&
                (position.getLongitude() < (pj.getLongitude() - pi.getLongitude()) *
                 (position.getLatitude() - pi.getLatitude()) / (pj.getLatitude() - pi.getLatitude()) + pi.getLongitude())) {
                inside = !inside;
            }
            j = i;
        }

        return inside;
    }

    /**
     * Sınır noktası ekler
     */
    public void addBoundaryPoint(Position point) {
        this.boundaries.add(point);
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

    public RestrictedZoneType getType() {
        return type;
    }

    public void setType(RestrictedZoneType type) {
        this.type = type;
    }

    public List<Position> getBoundaries() {
        return new ArrayList<>(boundaries);
    }

    public void setBoundaries(List<Position> boundaries) {
        this.boundaries = new ArrayList<>(boundaries);
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

    public String getRestrictionReason() {
        return restrictionReason;
    }

    public void setRestrictionReason(String restrictionReason) {
        this.restrictionReason = restrictionReason;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }
}











