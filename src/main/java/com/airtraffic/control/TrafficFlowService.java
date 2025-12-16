package com.airtraffic.control;

import com.airtraffic.map.CityMap;
import com.airtraffic.map.RouteNetwork;
import com.airtraffic.map.RouteSegment;
import com.airtraffic.model.Position;
import com.airtraffic.model.RouteDirection;
import com.airtraffic.model.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trafik akışı yönetimi servisi
 * Segment bazlı trafik organizasyonu ve hız/yükseklik yönetimi
 */
public class TrafficFlowService {
    
    private RouteNetwork routeNetwork;
    private CityMap cityMap;
    
    // Segment bazlı araç takibi
    private Map<String, List<String>> segmentVehicles; // Segment ID -> Vehicle ID listesi
    
    public TrafficFlowService() {
        this.segmentVehicles = new HashMap<>();
    }
    
    /**
     * RouteNetwork ve CityMap'i ayarlar
     * @param routeNetwork Yol ağı
     * @param cityMap Şehir haritası
     */
    public void initialize(RouteNetwork routeNetwork, CityMap cityMap) {
        this.routeNetwork = routeNetwork;
        this.cityMap = cityMap;
    }
    
    /**
     * Aracın mevcut segmentini belirler ve günceller
     * @param vehicle Araç
     * @param threshold Mesafe eşiği (metre)
     * @return Bulunan segment veya null
     */
    public RouteSegment updateVehicleSegment(Vehicle vehicle, double threshold) {
        if (vehicle == null || vehicle.getPosition() == null || routeNetwork == null) {
            return null;
        }
        
        // Mevcut segmenti kontrol et
        RouteSegment currentSegment = vehicle.getCurrentSegment();
        if (currentSegment != null && currentSegment.isActive() && 
            currentSegment.isOnSegment(vehicle.getPosition(), threshold)) {
            // Hala aynı segmentte
            return currentSegment;
        }
        
        // Yeni segment bul
        RouteSegment newSegment = routeNetwork.findNearestSegment(vehicle.getPosition(), threshold);
        
        if (newSegment != null) {
            // Eski segmentten çıkar
            if (currentSegment != null) {
                removeVehicleFromSegment(currentSegment.getSegmentId(), vehicle.getId());
            }
            
            // Yeni segmente ekle
            vehicle.setCurrentSegment(newSegment);
            addVehicleToSegment(newSegment.getSegmentId(), vehicle.getId());
        } else {
            // Segment bulunamadı
            if (currentSegment != null) {
                removeVehicleFromSegment(currentSegment.getSegmentId(), vehicle.getId());
            }
            vehicle.setCurrentSegment(null);
        }
        
        return newSegment;
    }
    
    /**
     * Aracın segment kurallarına uygun olup olmadığını kontrol eder
     * @param vehicle Araç
     * @return Uyumluysa true
     */
    public boolean checkSegmentCompliance(Vehicle vehicle) {
        if (vehicle == null || vehicle.getCurrentSegment() == null) {
            return true; // Segment yoksa kontrol yapılmaz
        }
        
        RouteSegment segment = vehicle.getCurrentSegment();
        
        // Yükseklik kontrolü
        if (vehicle.getPosition() != null) {
            double altitudeDiff = Math.abs(vehicle.getPosition().getAltitude() - segment.getAltitude());
            if (altitudeDiff > 5.0) { // 5m tolerans
                return false; // Yükseklik uyumsuz
            }
        }
        
        // Hız kontrolü
        if (vehicle.getVelocity() > segment.getSpeedLimit() * 1.1) { // %10 tolerans
            return false; // Hız limiti aşılmış
        }
        
        return true;
    }
    
    /**
     * Segment için araç sayısını döndürür
     * @param segmentId Segment ID
     * @return Araç sayısı
     */
    public int getVehicleCountForSegment(String segmentId) {
        List<String> vehicles = segmentVehicles.get(segmentId);
        return vehicles != null ? vehicles.size() : 0;
    }
    
    /**
     * Segment kapasitesini kontrol eder
     * @param segment Segment
     * @return Kapasite dolmuşsa true
     */
    public boolean isSegmentAtCapacity(RouteSegment segment) {
        if (segment == null) {
            return false;
        }
        
        int currentCount = getVehicleCountForSegment(segment.getSegmentId());
        return currentCount >= segment.getMaxVehicles();
    }
    
    /**
     * Segmentteki araçları döndürür
     * @param segmentId Segment ID
     * @return Araç ID listesi
     */
    public List<String> getVehiclesInSegment(String segmentId) {
        List<String> vehicles = segmentVehicles.get(segmentId);
        return vehicles != null ? new ArrayList<>(vehicles) : new ArrayList<>();
    }
    
    /**
     * Aracı segmente ekler
     */
    private void addVehicleToSegment(String segmentId, String vehicleId) {
        segmentVehicles.computeIfAbsent(segmentId, k -> new ArrayList<>()).add(vehicleId);
    }
    
    /**
     * Aracı segmentten çıkarır
     */
    private void removeVehicleFromSegment(String segmentId, String vehicleId) {
        List<String> vehicles = segmentVehicles.get(segmentId);
        if (vehicles != null) {
            vehicles.remove(vehicleId);
            if (vehicles.isEmpty()) {
                segmentVehicles.remove(segmentId);
            }
        }
    }
    
    /**
     * Tüm segment araç dağılımını temizler
     */
    public void clearSegmentVehicles() {
        segmentVehicles.clear();
    }
}

