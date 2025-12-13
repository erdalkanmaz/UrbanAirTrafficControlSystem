package com.airtraffic.rules;

import com.airtraffic.model.Vehicle;
import com.airtraffic.model.Position;

/**
 * Hız limiti kuralı
 */
public class SpeedLimitRule extends TrafficRule {
    private double maxSpeed;        // Maksimum hız (m/s)
    private double minSpeed;        // Minimum hız (m/s) - opsiyonel
    private double tolerance;        // Tolerans (m/s) - uyarı için

    public SpeedLimitRule() {
        super();
        setRuleType(RuleType.SPEED_LIMIT);
        this.tolerance = 5.0; // Varsayılan 5 m/s tolerans
    }

    public SpeedLimitRule(String name, double maxSpeed) {
        super(name, RuleType.SPEED_LIMIT);
        this.maxSpeed = maxSpeed;
        this.tolerance = 5.0;
    }

    @Override
    public boolean isViolated(Vehicle vehicle, Position position) {
        if (!isApplicable(vehicle, position)) {
            return false;
        }

        double currentSpeed = vehicle.getVelocity();

        // Maksimum hız kontrolü
        if (maxSpeed > 0 && currentSpeed > maxSpeed) {
            return true;
        }

        // Minimum hız kontrolü
        if (minSpeed > 0 && currentSpeed < minSpeed && vehicle.getStatus().name().contains("FLIGHT")) {
            return true;
        }

        return false;
    }

    /**
     * Hız limitine yaklaşıldığında uyarı verir
     * @param vehicle Araç
     * @return Uyarı gerekiyorsa true
     */
    public boolean isWarningNeeded(Vehicle vehicle) {
        if (maxSpeed <= 0) {
            return false;
        }

        double currentSpeed = vehicle.getVelocity();
        return currentSpeed > (maxSpeed - tolerance) && currentSpeed <= maxSpeed;
    }

    // Getters and Setters
    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        if (maxSpeed < 0) {
            throw new IllegalArgumentException("Maksimum hız negatif olamaz");
        }
        this.maxSpeed = maxSpeed;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }
}











