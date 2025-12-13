package com.airtraffic.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * VTOL (Vertical Take-Off and Landing) araç modeli
 * Dikey kalkış yapabilen dron tipi araçlar için
 */
public class Vehicle {
    private String id;                      // Benzersiz araç ID
    private VehicleType type;              // Araç tipi
    private Position position;             // Mevcut konum (3D)
    private double velocity;                // Hız (m/s)
    private double heading;                 // Yön (derece, 0-360)
    private double altitude;                // Yükseklik (metre)
    private double fuelLevel;               // Yakıt seviyesi (0-100%)
    private VehicleStatus status;           // Araç durumu
    private String pilotLicense;            // Pilot lisans numarası
    private AutomationLevel automationLevel; // Otomasyon seviyesi
    private String registrationNumber;      // Kayıt numarası
    private LocalDateTime lastUpdateTime;   // Son güncelleme zamanı
    private double maxSpeed;                // Maksimum hız (m/s)
    private double maxAltitude;             // Maksimum yükseklik (metre)
    private double weight;                  // Ağırlık (kg)
    private String manufacturer;           // Üretici
    private String model;                   // Model

    public Vehicle() {
        this.id = UUID.randomUUID().toString();
        this.status = VehicleStatus.IDLE;
        this.position = new Position();
        this.lastUpdateTime = LocalDateTime.now();
        this.automationLevel = AutomationLevel.MANUAL;
    }

    public Vehicle(VehicleType type, Position initialPosition) {
        this();
        this.type = type;
        this.position = initialPosition;
        this.altitude = initialPosition.getAltitude();
    }

    /**
     * Araç konumunu günceller
     */
    public void updatePosition(Position newPosition) {
        this.position = newPosition;
        this.altitude = newPosition.getAltitude();
        this.lastUpdateTime = LocalDateTime.now();
    }

    /**
     * Araç hızını günceller
     */
    public void updateVelocity(double newVelocity) {
        if (newVelocity < 0) {
            throw new IllegalArgumentException("Hız negatif olamaz");
        }
        if (maxSpeed > 0 && newVelocity > maxSpeed) {
            throw new IllegalArgumentException("Hız maksimum hızı aşamaz: " + maxSpeed);
        }
        this.velocity = newVelocity;
        this.lastUpdateTime = LocalDateTime.now();
    }

    /**
     * Yakıt seviyesini kontrol eder
     * @return Yakıt yetersizse true
     */
    public boolean isLowFuel() {
        return fuelLevel < 20.0; // %20'nin altında
    }

    /**
     * Acil durum moduna geçer
     */
    public void enterEmergencyMode() {
        this.status = VehicleStatus.EMERGENCY;
        this.lastUpdateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
        if (position != null) {
            this.altitude = position.getAltitude();
        }
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        // Normalize heading to 0-360
        this.heading = ((heading % 360) + 360) % 360;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        if (maxAltitude > 0 && altitude > maxAltitude) {
            throw new IllegalArgumentException("Yükseklik maksimum yüksekliği aşamaz: " + maxAltitude);
        }
        this.altitude = altitude;
        if (position != null) {
            position.setAltitude(altitude);
        }
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(double fuelLevel) {
        if (fuelLevel < 0 || fuelLevel > 100) {
            throw new IllegalArgumentException("Yakıt seviyesi 0-100 arasında olmalıdır");
        }
        this.fuelLevel = fuelLevel;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public String getPilotLicense() {
        return pilotLicense;
    }

    public void setPilotLicense(String pilotLicense) {
        this.pilotLicense = pilotLicense;
    }

    public AutomationLevel getAutomationLevel() {
        return automationLevel;
    }

    public void setAutomationLevel(AutomationLevel automationLevel) {
        this.automationLevel = automationLevel;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return String.format("Vehicle[id=%s, type=%s, status=%s, pos=%s, speed=%.2f m/s]",
                id, type, status, position, velocity);
    }
}











