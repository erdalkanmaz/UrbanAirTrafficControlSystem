package com.airtraffic.control;

import com.airtraffic.map.CityMap;
import com.airtraffic.model.*;
import com.airtraffic.spatial.Quadtree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Çarpışma tespiti ve önleme servisi
 * ICAO Annex 2 standartlarına uygun çarpışma önleme algoritması
 * 
 * Minimum güvenli mesafeler:
 * - Yatay: 50 metre
 * - Dikey: 10 metre
 */
public class CollisionDetectionService {
    
    // Minimum güvenli mesafeler (ICAO Annex 2 standartları)
    private static final double MIN_HORIZONTAL_SEPARATION = 50.0; // metre
    private static final double MIN_VERTICAL_SEPARATION = 10.0;    // metre
    
    // Çarpışma kontrolü için kontrol yarıçapı (metre)
    private static final double COLLISION_CHECK_RADIUS = 500.0; // 500 metre yarıçap
    
    // Risk seviyesi eşikleri
    private static final double LOW_RISK_THRESHOLD = 0.3;      // Risk skoru < 0.3
    private static final double MEDIUM_RISK_THRESHOLD = 0.5;   // Risk skoru < 0.5
    private static final double HIGH_RISK_THRESHOLD = 0.8;    // Risk skoru < 0.8
    // CRITICAL: Risk skoru >= 0.8
    
    // Tahmin zamanı (saniye) - gelecek konum projeksiyonu için
    private static final double PREDICTION_TIME_HORIZON = 30.0; // 30 saniye
    
    /**
     * Belirli bir araç için çarpışma risklerini kontrol eder
     * @param vehicle Kontrol edilecek araç
     * @param allVehicles Tüm aktif araçlar listesi
     * @param vehicleIndex Quadtree spatial index (opsiyonel, performans için)
     * @return Çarpışma riskleri listesi
     */
    public List<CollisionRisk> checkCollisionRisks(Vehicle vehicle, List<Vehicle> allVehicles, Quadtree vehicleIndex) {
        return checkCollisionRisks(vehicle, allVehicles, vehicleIndex, null);
    }

    /**
     * Belirli bir araç için çarpışma risklerini kontrol eder (yükseklik katmanı desteği ile)
     * @param vehicle Kontrol edilecek araç
     * @param allVehicles Tüm aktif araçlar listesi
     * @param vehicleIndex Quadtree spatial index (opsiyonel, performans için)
     * @param cityMap Şehir haritası (yükseklik katmanı kontrolü için, opsiyonel)
     * @return Çarpışma riskleri listesi
     */
    public List<CollisionRisk> checkCollisionRisks(Vehicle vehicle, List<Vehicle> allVehicles, Quadtree vehicleIndex, CityMap cityMap) {
        if (vehicle == null || vehicle.getPosition() == null) {
            throw new IllegalArgumentException("Vehicle and position cannot be null");
        }
        
        List<CollisionRisk> risks = new ArrayList<>();
        
        // Yakın araçları bul (Quadtree kullanarak performanslı)
        List<Vehicle> nearbyVehicles = findNearbyVehicles(vehicle, allVehicles, vehicleIndex);
        
        // Her yakın araç için çarpışma riski hesapla
        for (Vehicle otherVehicle : nearbyVehicles) {
            if (otherVehicle.getId().equals(vehicle.getId())) {
                continue; // Kendisi ile karşılaştırma
            }
            
            CollisionRisk risk = calculateCollisionRisk(vehicle, otherVehicle, cityMap);
            if (risk != null && risk.getRiskScore() > 0.0) {
                risks.add(risk);
            }
        }
        
        return risks;
    }
    
    /**
     * İki araç arasındaki çarpışma riskini hesaplar
     * @param vehicle1 İlk araç
     * @param vehicle2 İkinci araç
     * @return Çarpışma riski (risk yoksa null)
     */
    public CollisionRisk calculateCollisionRisk(Vehicle vehicle1, Vehicle vehicle2) {
        return calculateCollisionRisk(vehicle1, vehicle2, null);
    }

    /**
     * İki araç arasındaki çarpışma riskini hesaplar (yükseklik katmanı desteği ile)
     * @param vehicle1 İlk araç
     * @param vehicle2 İkinci araç
     * @param cityMap Şehir haritası (yükseklik katmanı kontrolü için, opsiyonel)
     * @return Çarpışma riski (risk yoksa null)
     */
    public CollisionRisk calculateCollisionRisk(Vehicle vehicle1, Vehicle vehicle2, CityMap cityMap) {
        if (vehicle1 == null || vehicle2 == null) {
            throw new IllegalArgumentException("Vehicles cannot be null");
        }
        
        if (vehicle1.getPosition() == null || vehicle2.getPosition() == null) {
            return null; // Konum bilgisi yoksa risk hesaplanamaz
        }
        
        // Yükseklik katmanı kontrolü (CityMap varsa)
        if (cityMap != null) {
            AltitudeLayer layer1 = vehicle1.getCurrentLayer(cityMap);
            AltitudeLayer layer2 = vehicle2.getCurrentLayer(cityMap);
            
            // Eğer araçlar farklı katmanlardaysa, riski azalt veya yok say
            if (layer1 != null && layer2 != null && !layer1.equals(layer2)) {
                // Farklı katmanlardaki araçlar arasında minimum mesafe katmanlar arası mesafeden kaynaklanır
                // LOW: 0-60m, MEDIUM: 60-120m, HIGH: 120-180m
                // Katmanlar arası minimum mesafe en az 60m (LOW-MEDIUM) veya 60m (MEDIUM-HIGH)
                Position pos1 = vehicle1.getPosition();
                Position pos2 = vehicle2.getPosition();
                double verticalDistance = Math.abs(pos1.getAltitude() - pos2.getAltitude());
                
                // Katmanlar arası minimum mesafe kontrolü
                if (verticalDistance >= 60.0) {
                    // Yeterli dikey mesafe varsa, risk yok sayılabilir veya çok düşük olmalı
                    double horizontalDistance = pos1.horizontalDistanceTo(pos2);
                    
                    // Çok büyük dikey mesafe varsa (örn. LOW-HIGH arası 120m), riski tamamen yok say
                    if (verticalDistance >= 100.0) {
                        return null; // Çok büyük dikey mesafe, risk yok
                    }
                    
                    // Orta dikey mesafe varsa (60-100m), yatay mesafe de yeterliyse risk yok
                    if (horizontalDistance > MIN_HORIZONTAL_SEPARATION * 2) {
                        return null; // Yeterli mesafe var, risk yok
                    }
                    // Yatay mesafe yakınsa, risk skorunu azaltarak devam et (calculateRiskScore'da işlenecek)
                }
            }
        }
        
        Position pos1 = vehicle1.getPosition();
        Position pos2 = vehicle2.getPosition();
        
        // Mesafe hesaplamaları
        double horizontalDistance = pos1.horizontalDistanceTo(pos2);
        double verticalDistance = pos1.verticalDistanceTo(pos2);
        double totalDistance = pos1.distance3DTo(pos2);
        
        // Minimum güvenli mesafe kontrolü
        boolean violatesHorizontalSeparation = horizontalDistance < MIN_HORIZONTAL_SEPARATION;
        boolean violatesVerticalSeparation = verticalDistance < MIN_VERTICAL_SEPARATION;
        
        // Eğer minimum mesafeler korunuyorsa, risk düşük
        if (!violatesHorizontalSeparation && !violatesVerticalSeparation) {
            // Ancak yakınlaşıyor mu kontrol et
            double futureRisk = predictFutureCollisionRisk(vehicle1, vehicle2);
            if (futureRisk < LOW_RISK_THRESHOLD) {
                return null; // Risk yok
            }
        }
        
        // Risk skoru hesapla (CityMap varsa katman faktörünü dikkate al)
        double riskScore = calculateRiskScore(vehicle1, vehicle2, horizontalDistance, verticalDistance, totalDistance, cityMap);
        
        // Risk seviyesi belirle
        RiskLevel riskLevel = determineRiskLevel(riskScore, violatesHorizontalSeparation, violatesVerticalSeparation);
        
        // Tahmini çarpışma süresi
        double timeToCollision = estimateTimeToCollision(vehicle1, vehicle2);
        
        // CollisionRisk oluştur
        CollisionRisk risk = new CollisionRisk(
            vehicle1.getId(),
            vehicle2.getId(),
            riskLevel,
            riskScore
        );
        
        risk.setCurrentDistance(totalDistance);
        risk.setHorizontalDistance(horizontalDistance);
        risk.setVerticalDistance(pos1.getAltitude() - pos2.getAltitude()); // Yükseklik farkı
        risk.setEstimatedTimeToCollision(timeToCollision);
        risk.calculateRecommendedAction();
        
        return risk;
    }
    
    /**
     * Yakın araçları bulur (Quadtree kullanarak performanslı)
     */
    private List<Vehicle> findNearbyVehicles(Vehicle vehicle, List<Vehicle> allVehicles, Quadtree vehicleIndex) {
        if (vehicleIndex != null && vehicle.getPosition() != null) {
            // Quadtree kullanarak yakın araçları bul (O(log n))
            return vehicleIndex.query(vehicle.getPosition(), COLLISION_CHECK_RADIUS);
        } else {
            // Fallback: Linear search (O(n))
            return allVehicles.stream()
                    .filter(v -> v.getPosition() != null)
                    .filter(v -> v.getPosition().horizontalDistanceTo(vehicle.getPosition()) <= COLLISION_CHECK_RADIUS)
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Risk skoru hesaplar (0.0 - 1.0)
     */
    private double calculateRiskScore(Vehicle v1, Vehicle v2, double horizontalDist, double verticalDist, double totalDist) {
        return calculateRiskScore(v1, v2, horizontalDist, verticalDist, totalDist, null);
    }

    /**
     * Risk skoru hesaplar (0.0 - 1.0) - yükseklik katmanı desteği ile
     */
    private double calculateRiskScore(Vehicle v1, Vehicle v2, double horizontalDist, double verticalDist, double totalDist, CityMap cityMap) {
        double score = 0.0;
        
        // Yükseklik katmanı faktörü (CityMap varsa)
        double layerFactor = 1.0; // Varsayılan: katman faktörü yok
        if (cityMap != null) {
            AltitudeLayer layer1 = v1.getCurrentLayer(cityMap);
            AltitudeLayer layer2 = v2.getCurrentLayer(cityMap);
            
            if (layer1 != null && layer2 != null && !layer1.equals(layer2)) {
                // Farklı katmanlardaki araçlar için risk skorunu azalt
                // Katmanlar arası minimum mesafe (60m) göz önüne alınarak
                double verticalDistForLayers = Math.abs(v1.getPosition().getAltitude() - v2.getPosition().getAltitude());
                if (verticalDistForLayers >= 100.0) {
                    // Çok büyük dikey mesafe (örn. LOW-HIGH arası 120m), risk skorunu %90 azalt
                    layerFactor = 0.1;
                } else if (verticalDistForLayers >= 60.0) {
                    // Yeterli dikey mesafe varsa, risk skorunu %70 azalt
                    layerFactor = 0.3;
                } else {
                    // Dikey mesafe yetersizse, risk skorunu %50 azalt
                    layerFactor = 0.5;
                }
            }
        }
        
        // Mesafe faktörü (daha yakın = daha yüksek risk)
        double distanceFactor = 1.0 - Math.min(totalDist / COLLISION_CHECK_RADIUS, 1.0);
        score += distanceFactor * 0.4 * layerFactor; // %40 ağırlık, katman faktörü ile çarp
        
        // Minimum mesafe ihlali
        if (horizontalDist < MIN_HORIZONTAL_SEPARATION) {
            double violationFactor = 1.0 - (horizontalDist / MIN_HORIZONTAL_SEPARATION);
            score += violationFactor * 0.3 * layerFactor; // %30 ağırlık, katman faktörü ile çarp
        }
        
        if (verticalDist < MIN_VERTICAL_SEPARATION) {
            double violationFactor = 1.0 - (verticalDist / MIN_VERTICAL_SEPARATION);
            score += violationFactor * 0.2 * layerFactor; // %20 ağırlık, katman faktörü ile çarp
        }
        
        // Hız faktörü (daha hızlı = daha yüksek risk)
        double relativeSpeed = Math.abs(v1.getVelocity() - v2.getVelocity());
        double maxSpeed = Math.max(v1.getMaxSpeed() > 0 ? v1.getMaxSpeed() : 50.0, 
                                   v2.getMaxSpeed() > 0 ? v2.getMaxSpeed() : 50.0);
        double speedFactor = Math.min(relativeSpeed / maxSpeed, 1.0);
        score += speedFactor * 0.1 * layerFactor; // %10 ağırlık, katman faktörü ile çarp
        
        // Gelecek çarpışma riski
        double futureRisk = predictFutureCollisionRisk(v1, v2);
        score += futureRisk * 0.3 * layerFactor; // %30 ağırlık, katman faktörü ile çarp
        
        return Math.min(score, 1.0); // Maksimum 1.0
    }
    
    /**
     * Gelecek çarpışma riskini tahmin eder
     */
    private double predictFutureCollisionRisk(Vehicle v1, Vehicle v2) {
        Position futurePos1 = predictFuturePosition(v1, PREDICTION_TIME_HORIZON);
        Position futurePos2 = predictFuturePosition(v2, PREDICTION_TIME_HORIZON);
        
        if (futurePos1 == null || futurePos2 == null) {
            return 0.0;
        }
        
        double futureDistance = futurePos1.distance3DTo(futurePos2);
        double currentDistance = v1.getPosition().distance3DTo(v2.getPosition());
        
        // Yakınlaşıyor mu?
        if (futureDistance < currentDistance) {
            // Ne kadar yakınlaşacak?
            double approachFactor = 1.0 - (futureDistance / currentDistance);
            return Math.min(approachFactor, 1.0);
        }
        
        return 0.0; // Uzaklaşıyor, risk yok
    }
    
    /**
     * Gelecek konumu tahmin eder (basit lineer projeksiyon)
     */
    public Position predictFuturePosition(Vehicle vehicle, double timeSeconds) {
        if (vehicle == null || vehicle.getPosition() == null) {
            return null;
        }
        
        Position currentPos = vehicle.getPosition();
        double velocity = vehicle.getVelocity();
        double heading = vehicle.getHeading(); // derece
        
        if (velocity <= 0 || timeSeconds <= 0) {
            return new Position(currentPos.getLatitude(), currentPos.getLongitude(), currentPos.getAltitude());
        }
        
        // Basit lineer projeksiyon (daha gelişmiş algoritma için rüzgar, ivme vb. eklenebilir)
        // Mesafe = hız * zaman
        double distance = velocity * timeSeconds; // metre
        
        // Yatay mesafe (basitleştirilmiş - küçük mesafeler için yeterli)
        // Gerçek uygulamada great circle calculation kullanılmalı
        double latOffset = distance * Math.cos(Math.toRadians(heading)) / 111320.0; // ~111km per degree
        double lonOffset = distance * Math.sin(Math.toRadians(heading)) / (111320.0 * Math.cos(Math.toRadians(currentPos.getLatitude())));
        
        double futureLat = currentPos.getLatitude() + latOffset;
        double futureLon = currentPos.getLongitude() + lonOffset;
        double futureAlt = currentPos.getAltitude(); // Yükseklik değişimi için daha gelişmiş model gerekir
        
        return new Position(futureLat, futureLon, futureAlt);
    }
    
    /**
     * Risk seviyesini belirler
     */
    private RiskLevel determineRiskLevel(double riskScore, boolean violatesHorizontal, boolean violatesVertical) {
        if (riskScore >= HIGH_RISK_THRESHOLD || (violatesHorizontal && violatesVertical)) {
            return RiskLevel.CRITICAL;
        } else if (riskScore >= MEDIUM_RISK_THRESHOLD || violatesHorizontal || violatesVertical) {
            return RiskLevel.HIGH;
        } else if (riskScore >= LOW_RISK_THRESHOLD) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.LOW;
        }
    }
    
    /**
     * Tahmini çarpışma süresini hesaplar (saniye)
     */
    private double estimateTimeToCollision(Vehicle v1, Vehicle v2) {
        Position pos1 = v1.getPosition();
        Position pos2 = v2.getPosition();
        
        double currentDistance = pos1.distance3DTo(pos2);
        
        // Yaklaşma hızı (basitleştirilmiş)
        double relativeSpeed = Math.abs(v1.getVelocity() - v2.getVelocity());
        
        if (relativeSpeed <= 0) {
            return Double.MAX_VALUE; // Çarpışma olmayacak
        }
        
        // Basit tahmin: mesafe / yaklaşma hızı
        double timeToCollision = currentDistance / relativeSpeed;
        
        // Minimum mesafeye ulaşma süresi
        double timeToMinSeparation = (currentDistance - MIN_HORIZONTAL_SEPARATION) / relativeSpeed;
        
        return Math.max(timeToMinSeparation, 0.0);
    }
    
    /**
     * Minimum güvenli mesafe kontrolü
     * @param vehicle1 İlk araç
     * @param vehicle2 İkinci araç
     * @return Minimum mesafe korunuyorsa true
     */
    public boolean checkMinimumSeparation(Vehicle vehicle1, Vehicle vehicle2) {
        if (vehicle1 == null || vehicle2 == null || 
            vehicle1.getPosition() == null || vehicle2.getPosition() == null) {
            return false;
        }
        
        Position pos1 = vehicle1.getPosition();
        Position pos2 = vehicle2.getPosition();
        
        double horizontalDist = pos1.horizontalDistanceTo(pos2);
        double verticalDist = pos1.verticalDistanceTo(pos2);
        
        return horizontalDist >= MIN_HORIZONTAL_SEPARATION && 
               verticalDist >= MIN_VERTICAL_SEPARATION;
    }
    
    /**
     * Minimum güvenli mesafe değerlerini döndürür
     */
    public static double getMinHorizontalSeparation() {
        return MIN_HORIZONTAL_SEPARATION;
    }
    
    public static double getMinVerticalSeparation() {
        return MIN_VERTICAL_SEPARATION;
    }
}





