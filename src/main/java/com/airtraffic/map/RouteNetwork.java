package com.airtraffic.map;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.airtraffic.model.Position;
import com.airtraffic.model.Route;

/**
 * Trafik yolu ağı - şehir içi hava trafik yolları
 */
public class RouteNetwork {
    private String cityName;
    private List<Route> mainStreets;       // Ana caddeler
    private List<Route> sideStreets;       // Sokaklar
    private double mainStreetSpacing;      // Ana caddeler arası mesafe (metre)
    private double sideStreetSpacing;      // Sokaklar arası mesafe (metre)
    private double mainStreetAltitude;     // Ana cadde yüksekliği (metre)
    private double sideStreetAltitude;    // Sokak yüksekliği (metre)
    private double mainStreetConnectionOffset; // Sokakların ana caddeden bağlantı mesafesi (metre)

    public RouteNetwork() {
        this.mainStreets = new ArrayList<>();
        this.sideStreets = new ArrayList<>();
        this.mainStreetSpacing = 50.0;      // Varsayılan: 50m
        this.sideStreetSpacing = 25.0;      // Varsayılan: 25m
        this.mainStreetAltitude = 100.0;    // Varsayılan: 100m
        this.sideStreetAltitude = 75.0;     // Varsayılan: 75m
        this.mainStreetConnectionOffset = 25.0; // Varsayılan: 25m
    }

    public RouteNetwork(String cityName) {
        this();
        this.cityName = cityName;
    }

    /**
     * Ana cadde ekler
     */
    public void addMainStreet(Route route) {
        this.mainStreets.add(route);
    }

    /**
     * Sokak ekler
     */
    public void addSideStreet(Route route) {
        this.sideStreets.add(route);
    }

    /**
     * Belirli bir konuma en yakın rotayı bulur
     * @param position Konum
     * @return En yakın rota
     */
    public Route findNearestRoute(Position position) {
        Route nearest = null;
        double minDistance = Double.MAX_VALUE;

        // Önce ana caddeleri kontrol et
        for (Route route : mainStreets) {
            for (Position waypoint : route.getWaypoints()) {
                double distance = position.horizontalDistanceTo(waypoint);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = route;
                }
            }
        }

        // Sonra sokakları kontrol et
        for (Route route : sideStreets) {
            for (Position waypoint : route.getWaypoints()) {
                double distance = position.horizontalDistanceTo(waypoint);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = route;
                }
            }
        }

        return nearest;
    }

    /**
     * Belirli bir yükseklikteki tüm rotaları döndürür
     * @param altitude Yükseklik (metre)
     * @param tolerance Tolerans (metre)
     * @return Yüksekliğe uygun rotalar
     */
    public List<Route> getRoutesAtAltitude(double altitude, double tolerance) {
        List<Route> routes = new ArrayList<>();

        for (Route route : mainStreets) {
            if (Math.abs(route.getMinAltitude() - altitude) <= tolerance ||
                Math.abs(route.getMaxAltitude() - altitude) <= tolerance) {
                routes.add(route);
            }
        }

        for (Route route : sideStreets) {
            if (Math.abs(route.getMinAltitude() - altitude) <= tolerance ||
                Math.abs(route.getMaxAltitude() - altitude) <= tolerance) {
                routes.add(route);
            }
        }

        return routes;
    }

    /**
     * Aktif rotaları döndürür
     */
    public List<Route> getActiveRoutes() {
        List<Route> activeRoutes = new ArrayList<>();
        activeRoutes.addAll(mainStreets.stream()
                .filter(Route::isActive)
                .collect(Collectors.toList()));
        activeRoutes.addAll(sideStreets.stream()
                .filter(Route::isActive)
                .collect(Collectors.toList()));
        return activeRoutes;
    }

    // Getters and Setters
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<Route> getMainStreets() {
        return new ArrayList<>(mainStreets);
    }

    public void setMainStreets(List<Route> mainStreets) {
        this.mainStreets = new ArrayList<>(mainStreets);
    }

    public List<Route> getSideStreets() {
        return new ArrayList<>(sideStreets);
    }

    public void setSideStreets(List<Route> sideStreets) {
        this.sideStreets = new ArrayList<>(sideStreets);
    }

    public double getMainStreetSpacing() {
        return mainStreetSpacing;
    }

    public void setMainStreetSpacing(double mainStreetSpacing) {
        this.mainStreetSpacing = mainStreetSpacing;
    }

    public double getSideStreetSpacing() {
        return sideStreetSpacing;
    }

    public void setSideStreetSpacing(double sideStreetSpacing) {
        this.sideStreetSpacing = sideStreetSpacing;
    }

    public double getMainStreetAltitude() {
        return mainStreetAltitude;
    }

    public void setMainStreetAltitude(double mainStreetAltitude) {
        this.mainStreetAltitude = mainStreetAltitude;
    }

    public double getSideStreetAltitude() {
        return sideStreetAltitude;
    }

    public void setSideStreetAltitude(double sideStreetAltitude) {
        this.sideStreetAltitude = sideStreetAltitude;
    }

    public double getMainStreetConnectionOffset() {
        return mainStreetConnectionOffset;
    }

    public void setMainStreetConnectionOffset(double mainStreetConnectionOffset) {
        this.mainStreetConnectionOffset = mainStreetConnectionOffset;
    }
}











