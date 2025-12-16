package com.airtraffic.control;

import com.airtraffic.map.CityMap;
import com.airtraffic.model.CollisionRisk;
import com.airtraffic.model.Position;
import com.airtraffic.model.SystemState;
import com.airtraffic.model.Vehicle;
import com.airtraffic.rules.TrafficRuleEngine;
import com.airtraffic.rules.TrafficRule;
import com.airtraffic.spatial.Quadtree;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Trafik Kontrol Merkezi - tüm trafik yönetimini koordine eder
 */
public class TrafficControlCenter {
    private static TrafficControlCenter instance;

    private String centerId;
    private CityMap cityMap;                           // Şehir haritası
    private TrafficRuleEngine ruleEngine;              // Trafik kuralı motoru
    private List<BaseStation> baseStations;             // Baz istasyonları
    private Map<String, Vehicle> activeVehicles;       // Aktif araçlar
    private Map<String, FlightAuthorization> authorizations; // Uçuş izinleri
    private Quadtree vehicleIndex;                      // Spatial index for vehicles (Quadtree)
    private CollisionDetectionService collisionService; // Çarpışma tespiti servisi
    private boolean isOperational;                      // Operasyonel mi?

    private TrafficControlCenter() {
        this.centerId = UUID.randomUUID().toString();
        this.baseStations = new ArrayList<>();
        this.activeVehicles = new ConcurrentHashMap<>();
        this.authorizations = new ConcurrentHashMap<>();
        this.ruleEngine = new TrafficRuleEngine();
        this.collisionService = new CollisionDetectionService();
        this.isOperational = true;
    }

    /**
     * Singleton instance döndürür
     */
    public static synchronized TrafficControlCenter getInstance() {
        if (instance == null) {
            instance = new TrafficControlCenter();
        }
        return instance;
    }

    /**
     * Şehir haritasını yükler
     */
    public void loadCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
        
        // Initialize Quadtree with city map bounds
        if (cityMap != null) {
            double minLat = cityMap.getMinLatitude();
            double maxLat = cityMap.getMaxLatitude();
            double minLon = cityMap.getMinLongitude();
            double maxLon = cityMap.getMaxLongitude();
            
            // If bounds are not set, use default Istanbul bounds
            if (minLat == 0.0 && maxLat == 0.0 && minLon == 0.0 && maxLon == 0.0) {
                minLat = 40.8;
                maxLat = 41.2;
                minLon = 28.5;
                maxLon = 29.5;
            }
            
            this.vehicleIndex = new Quadtree(minLat, maxLat, minLon, maxLon);
        }
    }

    /**
     * Baz istasyonu ekler
     */
    public void addBaseStation(BaseStation station) {
        if (station == null) {
            throw new IllegalArgumentException("Baz istasyonu null olamaz");
        }
        this.baseStations.add(station);
    }

    /**
     * Uçuş izni talebinde bulunur
     */
    public FlightAuthorization requestFlightAuthorization(Vehicle vehicle, Position departure, Position destination) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Araç null olamaz");
        }

        // Adres kontrolü (planınızda belirtildiği gibi)
        if (departure == null || destination == null) {
            throw new IllegalArgumentException("Kalkış ve varış noktaları belirtilmelidir");
        }

        FlightAuthorization authorization = new FlightAuthorization(vehicle.getId(), departure, destination);
        authorization.setPilotLicense(vehicle.getPilotLicense());

        // İzin kontrolü
        if (canAuthorizeFlight(vehicle, departure, destination)) {
            authorization.approve(LocalDateTime.now().plusHours(2)); // 2 saat geçerli
            authorizations.put(vehicle.getId(), authorization);
        } else {
            authorization.reject("Trafik yoğunluğu veya güvenlik nedeniyle izin verilemedi");
        }

        return authorization;
    }

    /**
     * Uçuş izninin verilip verilemeyeceğini kontrol eder
     */
    private boolean canAuthorizeFlight(Vehicle vehicle, Position departure, Position destination) {
        // Güvenlik kontrolü
        if (cityMap != null) {
            if (!cityMap.isPositionSafe(departure) || !cityMap.isPositionSafe(destination)) {
                return false;
            }
        }

        // Trafik yoğunluğu kontrolü (basitleştirilmiş)
        // Gerçek uygulamada daha gelişmiş algoritma gerekir
        long activeCount = activeVehicles.size();
        // Örnek: Maksimum 100 araç aynı anda trafikte olabilir
        if (activeCount >= 100) {
            return false;
        }

        return true;
    }

    /**
     * Aracı trafiğe kaydeder
     */
    public void registerVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Araç null olamaz");
        }

        // Uçuş izni kontrolü
        FlightAuthorization auth = authorizations.get(vehicle.getId());
        if (auth == null || !auth.isValid()) {
            throw new IllegalStateException("Araç için geçerli uçuş izni bulunamadı");
        }

        activeVehicles.put(vehicle.getId(), vehicle);
        
        // Add to spatial index
        if (vehicleIndex != null) {
            vehicleIndex.insert(vehicle);
        }
        
        updateBaseStationConnections(vehicle);
    }

    /**
     * Aracı trafikten çıkarır
     */
    public void unregisterVehicle(String vehicleId) {
        Vehicle vehicle = activeVehicles.remove(vehicleId);
        authorizations.remove(vehicleId);
        
        // Remove from spatial index
        if (vehicle != null && vehicleIndex != null) {
            vehicleIndex.remove(vehicle);
        }
        
        updateBaseStationConnections(null);
    }

    /**
     * Araç konumunu günceller
     */
    public void updateVehiclePosition(String vehicleId, Position newPosition) {
        Vehicle vehicle = activeVehicles.get(vehicleId);
        if (vehicle == null) {
            return;
        }

        vehicle.updatePosition(newPosition);

        // Update spatial index
        if (vehicleIndex != null) {
            vehicleIndex.update(vehicle);
        }

        // Trafik kuralı kontrolü
        List<TrafficRule> violations = ruleEngine.checkViolations(vehicle, newPosition);

        if (!violations.isEmpty()) {
            // İhlal durumunda uyarı gönder
            sendWarning(vehicleId, violations);
        }

        // Çarpışma riski kontrolü
        List<CollisionRisk> collisionRisks = collisionService.checkCollisionRisks(
            vehicle, 
            getActiveVehicles(), 
            vehicleIndex
        );

        if (!collisionRisks.isEmpty()) {
            // Çarpışma riski tespit edildi
            handleCollisionRisks(vehicleId, collisionRisks);
        }

        // Baz istasyonu bağlantılarını güncelle
        updateBaseStationConnections(vehicle);
    }

    /**
     * Baz istasyonu bağlantılarını günceller
     */
    private void updateBaseStationConnections(Vehicle vehicle) {
        if (vehicle == null) {
            // Tüm araçları kontrol et
            for (Vehicle v : activeVehicles.values()) {
                updateVehicleBaseStationConnection(v);
            }
        } else {
            updateVehicleBaseStationConnection(vehicle);
        }
    }

    private void updateVehicleBaseStationConnection(Vehicle vehicle) {
        for (BaseStation station : baseStations) {
            if (station.isInCoverage(vehicle)) {
                station.connectVehicle(vehicle.getId());
            } else {
                station.disconnectVehicle(vehicle.getId());
            }
        }
    }

    /**
     * Uyarı gönderir
     */
    private void sendWarning(String vehicleId, List<TrafficRule> violations) {
        // Gerçek uygulamada iletişim protokolü üzerinden gönderilir
        System.out.println("UYARI: Araç " + vehicleId + " için " + violations.size() + " kural ihlali tespit edildi");
    }

    /**
     * Çarpışma risklerini işler
     */
    private void handleCollisionRisks(String vehicleId, List<CollisionRisk> risks) {
        for (CollisionRisk risk : risks) {
            if (risk.isCritical()) {
                // Kritik risk - acil uyarı
                System.out.println("KRİTİK ÇARPIŞMA RİSKİ: Araç " + vehicleId + 
                    " ve " + risk.getVehicleId2() + " arasında kritik çarpışma riski tespit edildi! " +
                    "Tahmini çarpışma süresi: " + String.format("%.2f", risk.getEstimatedTimeToCollision()) + " saniye. " +
                    "Önerilen aksiyon: " + risk.getRecommendedAction());
            } else if (risk.requiresImmediateAction()) {
                // Yüksek risk - uyarı
                System.out.println("YÜKSEK ÇARPIŞMA RİSKİ: Araç " + vehicleId + 
                    " ve " + risk.getVehicleId2() + " arasında yüksek çarpışma riski. " +
                    "Mevcut mesafe: " + String.format("%.2f", risk.getCurrentDistance()) + " metre. " +
                    "Önerilen aksiyon: " + risk.getRecommendedAction());
            } else {
                // Orta/düşük risk - bilgilendirme
                System.out.println("ÇARPIŞMA RİSKİ: Araç " + vehicleId + 
                    " ve " + risk.getVehicleId2() + " arasında çarpışma riski tespit edildi. " +
                    "Risk seviyesi: " + risk.getRiskLevel() + ", Mesafe: " + 
                    String.format("%.2f", risk.getCurrentDistance()) + " metre");
            }
        }
    }

    /**
     * Belirli bir araç için çarpışma risklerini döndürür
     * @param vehicleId Araç ID
     * @return Çarpışma riskleri listesi
     */
    public List<CollisionRisk> getCollisionRisks(String vehicleId) {
        Vehicle vehicle = activeVehicles.get(vehicleId);
        if (vehicle == null) {
            return new ArrayList<>();
        }
        return collisionService.checkCollisionRisks(vehicle, getActiveVehicles(), vehicleIndex);
    }

    /**
     * Tüm aktif araçlar için kritik çarpışma risklerini döndürür
     * @return Kritik çarpışma riskleri listesi
     */
    public List<CollisionRisk> getCriticalCollisionRisks() {
        List<CollisionRisk> allRisks = new ArrayList<>();
        for (Vehicle vehicle : activeVehicles.values()) {
            List<CollisionRisk> risks = collisionService.checkCollisionRisks(
                vehicle, 
                getActiveVehicles(), 
                vehicleIndex
            );
            // Sadece kritik riskleri ekle
            risks.stream()
                .filter(CollisionRisk::isCritical)
                .forEach(allRisks::add);
        }
        return allRisks;
    }

    /**
     * Tüm aktif araçları döndürür
     */
    public List<Vehicle> getActiveVehicles() {
        return new ArrayList<>(activeVehicles.values());
    }

    /**
     * Belirli bir bölgedeki araçları döndürür
     * Uses Quadtree spatial indexing for O(log n) performance
     */
    public List<Vehicle> getVehiclesInArea(Position center, double radius) {
        // Use Quadtree if available for better performance
        if (vehicleIndex != null) {
            return vehicleIndex.query(center, radius);
        }
        
        // Fallback to linear search if Quadtree not initialized
        return activeVehicles.values().stream()
                .filter(v -> v.getPosition() != null)
                .filter(v -> v.getPosition().horizontalDistanceTo(center) <= radius)
                .collect(Collectors.toList());
    }

    /**
     * Sistem durumunu kontrol eder
     */
    public boolean isOperational() {
        return isOperational && cityMap != null;
    }

    // Getters and Setters
    public String getCenterId() {
        return centerId;
    }

    public CityMap getCityMap() {
        return cityMap;
    }

    public TrafficRuleEngine getRuleEngine() {
        return ruleEngine;
    }

    public List<BaseStation> getBaseStations() {
        return new ArrayList<>(baseStations);
    }

    /**
     * Tüm uçuş izinlerini döndürür
     */
    public Map<String, FlightAuthorization> getAuthorizations() {
        return new HashMap<>(authorizations);
    }

    public void setOperational(boolean operational) {
        isOperational = operational;
    }

    /**
     * Çarpışma tespiti servisini döndürür
     */
    public CollisionDetectionService getCollisionService() {
        return collisionService;
    }

    /**
     * Sistem durumunu JSON dosyasına kaydeder
     * @param filePath Dosya yolu
     * @throws IOException Dosya yazma hatası
     */
    public void saveState(String filePath) throws IOException {
        PersistenceService persistenceService = new PersistenceService();
        
        // Create SystemState from current TrafficControlCenter state
        SystemState systemState = new SystemState(
            this.cityMap,
            new ArrayList<>(this.activeVehicles.values()),
            this.baseStations,
            this.authorizations,
            this.centerId,
            this.isOperational
        );
        
        persistenceService.saveState(systemState, filePath);
    }

    /**
     * Sistem durumunu JSON dosyasından yükler
     * @param filePath Dosya yolu
     * @throws IOException Dosya okuma hatası
     */
    public void loadState(String filePath) throws IOException {
        PersistenceService persistenceService = new PersistenceService();
        SystemState systemState = persistenceService.loadState(filePath);
        
        // Restore TrafficControlCenter state from SystemState
        this.cityMap = systemState.getCityMap();
        this.centerId = systemState.getCenterId();
        this.isOperational = systemState.isOperational();
        
        // Clear and restore vehicles
        this.activeVehicles.clear();
        for (Vehicle vehicle : systemState.getVehicles()) {
            this.activeVehicles.put(vehicle.getId(), vehicle);
        }
        
        // Clear and restore base stations
        this.baseStations.clear();
        this.baseStations.addAll(systemState.getBaseStations());
        
        // Clear and restore authorizations
        this.authorizations.clear();
        this.authorizations.putAll(systemState.getAuthorizations());
        
        // Reinitialize Quadtree if cityMap is loaded
        if (this.cityMap != null) {
            double minLat = cityMap.getMinLatitude();
            double maxLat = cityMap.getMaxLatitude();
            double minLon = cityMap.getMinLongitude();
            double maxLon = cityMap.getMaxLongitude();
            
            // If bounds are not set, use default Istanbul bounds
            if (minLat == 0.0 && maxLat == 0.0 && minLon == 0.0 && maxLon == 0.0) {
                minLat = 40.8;
                maxLat = 41.2;
                minLon = 28.5;
                maxLon = 29.5;
            }
            
            this.vehicleIndex = new Quadtree(minLat, maxLat, minLon, maxLon);
            
            // Rebuild Quadtree with loaded vehicles
            for (Vehicle vehicle : this.activeVehicles.values()) {
                if (vehicle.getPosition() != null) {
                    this.vehicleIndex.insert(vehicle);
                }
            }
        }
    }
}











