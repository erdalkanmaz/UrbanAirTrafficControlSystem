package com.airtraffic.standards;

import com.airtraffic.control.CollisionDetectionService;
import com.airtraffic.map.CityMap;
import com.airtraffic.model.AltitudeLayer;
import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;
import com.airtraffic.model.VehicleStatus;

import java.util.List;

/**
 * ICAO (International Civil Aviation Organization) standartları uyumluluk kontrolü
 * 
 * ICAO Annex 2: Rules of the Air
 * - Minimum separation standards
 * - Flight rules compliance (VFR/IFR)
 * - Communication requirements
 */
public class ICAOStandardsCompliance {
    
    private static final String STANDARD_NAME = "ICAO Annex 2";
    
    // Minimum separation standards (ICAO Annex 2)
    private static final double MIN_HORIZONTAL_SEPARATION = 50.0; // metre
    private static final double MIN_VERTICAL_SEPARATION = 10.0;   // metre
    
    // Communication requirements
    private static final double COMMUNICATION_RANGE = 5000.0; // 5 km (metre)
    
    private CollisionDetectionService collisionService;
    
    public ICAOStandardsCompliance() {
        this.collisionService = new CollisionDetectionService();
    }
    
    /**
     * İki araç arasındaki minimum separation standartlarını kontrol eder
     * @param vehicle1 İlk araç
     * @param vehicle2 İkinci araç
     * @return Uyumluluk sonucu
     */
    public ComplianceResult checkSeparationStandards(Vehicle vehicle1, Vehicle vehicle2) {
        return checkSeparationStandards(vehicle1, vehicle2, null);
    }

    /**
     * İki araç arasındaki minimum separation standartlarını kontrol eder (yükseklik katmanı desteği ile)
     * @param vehicle1 İlk araç
     * @param vehicle2 İkinci araç
     * @param cityMap Şehir haritası (yükseklik katmanı kontrolü için, opsiyonel)
     * @return Uyumluluk sonucu
     */
    public ComplianceResult checkSeparationStandards(Vehicle vehicle1, Vehicle vehicle2, CityMap cityMap) {
        ComplianceResult result = new ComplianceResult(true, STANDARD_NAME);
        
        if (vehicle1 == null || vehicle2 == null) {
            result.addViolation("One or both vehicles are null");
            return result;
        }
        
        if (vehicle1.getPosition() == null || vehicle2.getPosition() == null) {
            result.addViolation("One or both vehicles have null positions");
            return result;
        }
        
        Position pos1 = vehicle1.getPosition();
        Position pos2 = vehicle2.getPosition();
        
        double horizontalDistance = pos1.horizontalDistanceTo(pos2);
        double verticalDistance = pos1.verticalDistanceTo(pos2);
        
        // Yükseklik katmanı kontrolü (CityMap varsa)
        if (cityMap != null) {
            AltitudeLayer layer1 = vehicle1.getCurrentLayer(cityMap);
            AltitudeLayer layer2 = vehicle2.getCurrentLayer(cityMap);
            
            // Eğer araçlar farklı katmanlardaysa, katmanlar arası minimum mesafe (60m) yeterli
            if (layer1 != null && layer2 != null && !layer1.equals(layer2)) {
                // Farklı katmanlardaki araçlar için minimum dikey mesafe 60m (katmanlar arası mesafe)
                if (verticalDistance >= 60.0) {
                    // Yeterli dikey mesafe var, separation standartlarına uygun
                    return result; // Compliant
                } else {
                    // Dikey mesafe yetersiz, ancak katmanlar farklı olduğu için yatay mesafe kontrolü yapılmalı
                    // Yatay mesafe yeterliyse, dikey mesafe ihlali tolere edilebilir
                    if (horizontalDistance >= MIN_HORIZONTAL_SEPARATION * 2) {
                        // Yeterli yatay mesafe var, katmanlar farklı olduğu için uyumlu sayılabilir
                        return result; // Compliant
                    }
                }
            }
        }
        
        // Normal separation kontrolü (aynı katmanda veya CityMap yoksa)
        // Horizontal separation check
        if (horizontalDistance < MIN_HORIZONTAL_SEPARATION) {
            result.addViolation(String.format(
                "Horizontal separation violation: %.2f m < %.2f m (minimum required)",
                horizontalDistance, MIN_HORIZONTAL_SEPARATION
            ));
            result.addRecommendation("Increase horizontal separation distance");
        }
        
        // Vertical separation check
        if (verticalDistance < MIN_VERTICAL_SEPARATION) {
            result.addViolation(String.format(
                "Vertical separation violation: %.2f m < %.2f m (minimum required)",
                verticalDistance, MIN_VERTICAL_SEPARATION
            ));
            result.addRecommendation("Adjust altitude to maintain minimum vertical separation");
        }
        
        return result;
    }
    
    /**
     * Uçuş kuralları uyumluluğunu kontrol eder (VFR/IFR)
     * @param vehicle Kontrol edilecek araç
     * @return Uyumluluk sonucu
     */
    public ComplianceResult checkFlightRulesCompliance(Vehicle vehicle) {
        return checkFlightRulesCompliance(vehicle, null);
    }

    /**
     * Uçuş kuralları uyumluluğunu kontrol eder (VFR/IFR) - yükseklik katmanı desteği ile
     * @param vehicle Kontrol edilecek araç
     * @param cityMap Şehir haritası (yükseklik katmanı ve yasak bölge kontrolü için, opsiyonel)
     * @return Uyumluluk sonucu
     */
    public ComplianceResult checkFlightRulesCompliance(Vehicle vehicle, CityMap cityMap) {
        ComplianceResult result = new ComplianceResult(true, STANDARD_NAME);
        
        if (vehicle == null) {
            result.addViolation("Vehicle is null");
            return result;
        }
        
        // VFR (Visual Flight Rules) basic checks
        // - Minimum visibility requirements (simplified)
        // - Cloud clearance requirements (simplified)
        // - Position reporting requirements
        
        if (vehicle.getPosition() == null) {
            result.addViolation("Vehicle position is null - cannot verify flight rules compliance");
            return result;
        }
        
        // Yükseklik katmanı ve yasak bölge kontrolü (CityMap varsa)
        if (cityMap != null) {
            // Konumun güvenli olup olmadığını kontrol et (engeller, yasak bölgeler)
            if (!cityMap.isPositionSafe(vehicle.getPosition())) {
                result.addViolation("Vehicle is in an unsafe position (obstacle, restricted zone, or out of bounds) - violates flight rules");
                result.addRecommendation("Adjust flight path to avoid obstacles and restricted zones");
            }
            
            // Yükseklik katmanı kontrolü
            AltitudeLayer currentLayer = vehicle.getCurrentLayer(cityMap);
            if (currentLayer == null) {
                result.addViolation("Vehicle is not in a valid altitude layer - violates flight rules");
                result.addRecommendation("Adjust altitude to enter a valid flight layer");
            }
        }
        
        // Status check - vehicle should be in flight or preparing
        if (vehicle.getStatus() == VehicleStatus.IN_FLIGHT || 
            vehicle.getStatus() == VehicleStatus.TAKING_OFF) {
            
            // Basic VFR compliance checks
            if (vehicle.getPosition().getAltitude() < 0) {
                result.addViolation("Vehicle altitude is negative - violates flight rules");
            }
            
            // Speed check (should be within reasonable limits)
            if (vehicle.getVelocity() < 0) {
                result.addViolation("Vehicle velocity is negative - violates flight rules");
            }
        }
        
        // Communication requirements (simplified)
        // In real implementation, this would check if vehicle is within communication range
        // of a base station
        
        return result;
    }
    
    /**
     * İletişim gereksinimlerini kontrol eder
     * @param vehicle Kontrol edilecek araç
     * @param baseStationPositions Baz istasyonu konumları listesi
     * @return İletişim kurulabiliyorsa true
     */
    public boolean validateCommunicationRequirements(Vehicle vehicle, List<Position> baseStationPositions) {
        if (vehicle == null || vehicle.getPosition() == null) {
            return false;
        }
        
        if (baseStationPositions == null || baseStationPositions.isEmpty()) {
            return false; // No base stations available
        }
        
        Position vehiclePos = vehicle.getPosition();
        
        // Check if vehicle is within communication range of any base station
        for (Position baseStationPos : baseStationPositions) {
            if (baseStationPos == null) {
                continue;
            }
            
            double distance = vehiclePos.horizontalDistanceTo(baseStationPos);
            if (distance <= COMMUNICATION_RANGE) {
                return true; // Within communication range
            }
        }
        
        return false; // Not within communication range of any base station
    }
    
    /**
     * Tüm aktif araçlar için separation standartlarını kontrol eder
     * @param vehicles Araç listesi
     * @return Uyumluluk sonuçları listesi
     */
    public List<ComplianceResult> checkAllSeparationStandards(List<Vehicle> vehicles) {
        List<ComplianceResult> results = new java.util.ArrayList<>();
        
        if (vehicles == null || vehicles.size() < 2) {
            return results; // Need at least 2 vehicles to check separation
        }
        
        // Check separation between all pairs of vehicles
        for (int i = 0; i < vehicles.size(); i++) {
            for (int j = i + 1; j < vehicles.size(); j++) {
                Vehicle v1 = vehicles.get(i);
                Vehicle v2 = vehicles.get(j);
                
                ComplianceResult result = checkSeparationStandards(v1, v2);
                if (!result.isCompliant()) {
                    results.add(result);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Minimum separation değerlerini döndürür
     */
    public static double getMinHorizontalSeparation() {
        return MIN_HORIZONTAL_SEPARATION;
    }
    
    public static double getMinVerticalSeparation() {
        return MIN_VERTICAL_SEPARATION;
    }
    
    public static double getCommunicationRange() {
        return COMMUNICATION_RANGE;
    }
}






