package com.airtraffic.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.airtraffic.model.Position;
import com.airtraffic.model.Route;
import com.airtraffic.model.RouteDirection;

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
    
    // Segment yönetimi
    private Map<String, List<RouteSegment>> routeSegments; // Route ID -> Segments listesi
    private List<RouteSegment> allSegments; // Tüm segmentler

    public RouteNetwork() {
        this.mainStreets = new ArrayList<>();
        this.sideStreets = new ArrayList<>();
        this.mainStreetSpacing = 50.0;      // Varsayılan: 50m
        this.sideStreetSpacing = 25.0;      // Varsayılan: 25m
        this.mainStreetAltitude = 100.0;    // Varsayılan: 100m
        this.sideStreetAltitude = 75.0;     // Varsayılan: 75m
        this.mainStreetConnectionOffset = 25.0; // Varsayılan: 25m
        this.routeSegments = new HashMap<>();
        this.allSegments = new ArrayList<>();
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
    
    /**
     * Rotayı segmentlere böler ve kaydeder
     * @param route Segmentlere bölünecek rota
     * @param segmentLength Her segmentin uzunluğu (metre)
     * @param direction Segment yönü (FORWARD veya REVERSE)
     * @param altitude Segment yüksekliği (metre)
     * @param speedLimit Segment hız limiti (m/s)
     * @return Oluşturulan segmentler listesi
     */
    public List<RouteSegment> createSegmentsForRoute(Route route, double segmentLength, 
                                                      RouteDirection direction, 
                                                      double altitude, double speedLimit) {
        if (route == null) {
            throw new IllegalArgumentException("Route cannot be null");
        }
        
        List<RouteSegment> segments = route.createSegments(segmentLength, direction, altitude, speedLimit);
        
        // Segmentleri kaydet
        routeSegments.put(route.getId(), segments);
        allSegments.addAll(segments);
        
        return new ArrayList<>(segments);
    }
    
    /**
     * Belirli bir konuma en yakın segmenti bulur
     * @param position Konum
     * @param threshold Mesafe eşiği (metre)
     * @return En yakın segment veya null
     */
    public RouteSegment findNearestSegment(Position position, double threshold) {
        if (position == null) {
            return null;
        }
        
        RouteSegment nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (RouteSegment segment : allSegments) {
            if (!segment.isActive()) {
                continue;
            }
            
            if (segment.isOnSegment(position, threshold)) {
                // Segment üzerinde, mesafeyi hesapla
                double distanceToStart = position.horizontalDistanceTo(segment.getStartPoint());
                double distanceToEnd = position.horizontalDistanceTo(segment.getEndPoint());
                double minSegmentDistance = Math.min(distanceToStart, distanceToEnd);
                
                if (minSegmentDistance < minDistance) {
                    minDistance = minSegmentDistance;
                    nearest = segment;
                }
            }
        }
        
        return nearest;
    }
    
    /**
     * Belirli bir rotaya ait segmentleri döndürür
     * @param routeId Rota ID
     * @return Segmentler listesi
     */
    public List<RouteSegment> getSegmentsForRoute(String routeId) {
        List<RouteSegment> segments = routeSegments.get(routeId);
        return segments != null ? new ArrayList<>(segments) : new ArrayList<>();
    }
    
    /**
     * Tüm aktif segmentleri döndürür
     * @return Aktif segmentler listesi
     */
    public List<RouteSegment> getAllActiveSegments() {
        return allSegments.stream()
                .filter(RouteSegment::isActive)
                .collect(Collectors.toList());
    }
    
    /**
     * Belirli bir yöne ait segmentleri döndürür
     * @param direction Yön (FORWARD veya REVERSE)
     * @return Segmentler listesi
     */
    public List<RouteSegment> getSegmentsByDirection(RouteDirection direction) {
        return allSegments.stream()
                .filter(s -> s.isActive() && s.getDirection() == direction)
                .collect(Collectors.toList());
    }
}











