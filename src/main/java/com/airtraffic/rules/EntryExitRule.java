package com.airtraffic.rules;

import com.airtraffic.model.Vehicle;
import com.airtraffic.model.Position;
import com.airtraffic.model.VehicleStatus;

/**
 * Giriş/çıkış kuralları
 * - Girişler: Trafik seviyesinin 10m üzerinden yan yoldan aşağı doğru hızlanarak
 * - Çıkışlar: Aşağıya doğru yan yoldan yavaşlayarak 10m aşağıdan
 */
public class EntryExitRule extends TrafficRule {
    private double entryAltitudeOffset;     // Giriş yükseklik farkı (metre)
    private double exitAltitudeOffset;      // Çıkış yükseklik farkı (metre)
    private double entrySpeedLimit;         // Giriş hız limiti (m/s)
    private double exitSpeedLimit;           // Çıkış hız limiti (m/s)

    public EntryExitRule() {
        super();
        setRuleType(RuleType.ENTRY_EXIT);
        this.entryAltitudeOffset = 10.0;      // Varsayılan: 10m üzerinden
        this.exitAltitudeOffset = 10.0;      // Varsayılan: 10m aşağıdan
        this.entrySpeedLimit = 5.0;          // Varsayılan giriş hızı
        this.exitSpeedLimit = 3.0;           // Varsayılan çıkış hızı
    }

    public EntryExitRule(String name) {
        this();
        setName(name);
    }

    @Override
    public boolean isViolated(Vehicle vehicle, Position position) {
        if (!isApplicable(vehicle, position)) {
            return false;
        }

        VehicleStatus status = vehicle.getStatus();

        // Giriş kuralları
        if (status == VehicleStatus.TAKING_OFF || status == VehicleStatus.PREPARING) {
            // Giriş hızı kontrolü
            if (vehicle.getVelocity() > entrySpeedLimit) {
                return true;
            }
        }

        // Çıkış kuralları
        if (status == VehicleStatus.LANDING) {
            // Çıkış hızı kontrolü
            if (vehicle.getVelocity() > exitSpeedLimit) {
                return true;
            }
        }

        return false;
    }

    /**
     * Giriş için güvenli yüksekliği döndürür
     * @param trafficAltitude Trafik yüksekliği (metre)
     * @return Giriş yüksekliği (metre)
     */
    public double getEntryAltitude(double trafficAltitude) {
        return trafficAltitude + entryAltitudeOffset;
    }

    /**
     * Çıkış için güvenli yüksekliği döndürür
     * @param trafficAltitude Trafik yüksekliği (metre)
     * @return Çıkış yüksekliği (metre)
     */
    public double getExitAltitude(double trafficAltitude) {
        return trafficAltitude - exitAltitudeOffset;
    }

    // Getters and Setters
    public double getEntryAltitudeOffset() {
        return entryAltitudeOffset;
    }

    public void setEntryAltitudeOffset(double entryAltitudeOffset) {
        this.entryAltitudeOffset = entryAltitudeOffset;
    }

    public double getExitAltitudeOffset() {
        return exitAltitudeOffset;
    }

    public void setExitAltitudeOffset(double exitAltitudeOffset) {
        this.exitAltitudeOffset = exitAltitudeOffset;
    }

    public double getEntrySpeedLimit() {
        return entrySpeedLimit;
    }

    public void setEntrySpeedLimit(double entrySpeedLimit) {
        this.entrySpeedLimit = entrySpeedLimit;
    }

    public double getExitSpeedLimit() {
        return exitSpeedLimit;
    }

    public void setExitSpeedLimit(double exitSpeedLimit) {
        this.exitSpeedLimit = exitSpeedLimit;
    }
}











