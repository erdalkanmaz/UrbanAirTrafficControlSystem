package com.airtraffic.map;

import java.util.ArrayList;
import java.util.List;

import com.airtraffic.model.Position;

/**
 * Şehir haritası - tüm harita bileşenlerini içerir
 */
public class CityMap {
    private String cityName;
    private String country;
    private Position center;                  // Şehir merkezi
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;
    private RouteNetwork routeNetwork;        // Trafik yolu ağı
    private List<Obstacle> obstacles;          // Engeller
    private List<RestrictedZone> restrictedZones; // Yasak bölgeler

    public CityMap() {
        this.obstacles = new ArrayList<>();
        this.restrictedZones = new ArrayList<>();
        this.routeNetwork = new RouteNetwork();
    }

    public CityMap(String cityName) {
        this();
        this.cityName = cityName;
        this.routeNetwork.setCityName(cityName);
    }

    /**
     * Belirli bir konumun güvenli olup olmadığını kontrol eder
     * @param position Kontrol edilecek konum
     * @return Güvenliyse true
     */
    public boolean isPositionSafe(Position position) {
        // Engelleri kontrol et
        for (Obstacle obstacle : obstacles) {
            if (obstacle.contains(position)) {
                return false;
            }
        }

        // Yasak bölgeleri kontrol et
        for (RestrictedZone zone : restrictedZones) {
            if (zone.contains(position)) {
                return false;
            }
        }

        // Sınırlar içinde mi kontrol et
        if (position.getLatitude() < minLatitude || position.getLatitude() > maxLatitude ||
            position.getLongitude() < minLongitude || position.getLongitude() > maxLongitude) {
            return false;
        }

        return true;
    }

    /**
     * Belirli bir konum için güvenli geçiş yüksekliğini hesaplar
     * @param position Konum
     * @return Güvenli yükseklik (metre)
     */
    public double getSafePassageAltitude(Position position) {
        double maxObstacleHeight = position.getAltitude(); // Start with position altitude

        // En yüksek engeli bul
        for (Obstacle obstacle : obstacles) {
            double distance = position.horizontalDistanceTo(obstacle.getPosition());
            if (distance <= obstacle.getRadius() || distance <= Math.max(obstacle.getWidth(), obstacle.getLength()) / 2.0) {
                double obstacleTop = obstacle.getPosition().getAltitude() + obstacle.getHeight();
                if (obstacleTop > maxObstacleHeight) {
                    maxObstacleHeight = obstacleTop;
                }
            }
        }

        return maxObstacleHeight + 10.0; // 10m güvenlik payı
    }

    /**
     * Engel ekler
     */
    public void addObstacle(Obstacle obstacle) {
        this.obstacles.add(obstacle);
    }

    /**
     * Yasak bölge ekler
     */
    public void addRestrictedZone(RestrictedZone zone) {
        this.restrictedZones.add(zone);
    }

    // Getters and Setters
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
        if (routeNetwork != null) {
            routeNetwork.setCityName(cityName);
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Position getCenter() {
        return center;
    }

    public void setCenter(Position center) {
        this.center = center;
    }

    public double getMinLatitude() {
        return minLatitude;
    }

    public void setMinLatitude(double minLatitude) {
        this.minLatitude = minLatitude;
    }

    public double getMaxLatitude() {
        return maxLatitude;
    }

    public void setMaxLatitude(double maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    public double getMinLongitude() {
        return minLongitude;
    }

    public void setMinLongitude(double minLongitude) {
        this.minLongitude = minLongitude;
    }

    public double getMaxLongitude() {
        return maxLongitude;
    }

    public void setMaxLongitude(double maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public RouteNetwork getRouteNetwork() {
        return routeNetwork;
    }

    public void setRouteNetwork(RouteNetwork routeNetwork) {
        this.routeNetwork = routeNetwork;
    }

    public List<Obstacle> getObstacles() {
        return new ArrayList<>(obstacles);
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = new ArrayList<>(obstacles);
    }

    public List<RestrictedZone> getRestrictedZones() {
        return new ArrayList<>(restrictedZones);
    }

    public void setRestrictedZones(List<RestrictedZone> restrictedZones) {
        this.restrictedZones = new ArrayList<>(restrictedZones);
    }

    @Override
    public String toString() {
        return String.format("CityMap[city=%s, routes=%d, obstacles=%d, restrictedZones=%d]",
                cityName,
                routeNetwork != null ? routeNetwork.getMainStreets().size() + routeNetwork.getSideStreets().size() : 0,
                obstacles.size(),
                restrictedZones.size());
    }
}











