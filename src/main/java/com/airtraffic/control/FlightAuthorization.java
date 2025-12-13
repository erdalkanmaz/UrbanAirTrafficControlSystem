package com.airtraffic.control;

import com.airtraffic.model.Position;
import com.airtraffic.model.Route;
import com.airtraffic.model.Vehicle;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Uçuş izni - trafiğe giriş için gerekli onay
 */
public class FlightAuthorization {
    private String id;
    private String vehicleId;
    private String pilotLicense;
    private Position departurePoint;      // Kalkış noktası
    private Position destinationPoint;     // Varış noktası
    private Route plannedRoute;            // Planlanan rota
    private LocalDateTime requestedTime;   // İzin talep zamanı
    private LocalDateTime authorizedTime;  // İzin verilme zamanı
    private LocalDateTime validUntil;       // İzin geçerlilik süresi
    private AuthorizationStatus status;    // İzin durumu
    private String reason;                  // Red nedeni (eğer reddedildiyse)

    public FlightAuthorization() {
        this.id = UUID.randomUUID().toString();
        this.status = AuthorizationStatus.PENDING;
        this.requestedTime = LocalDateTime.now();
    }

    public FlightAuthorization(String vehicleId, Position departure, Position destination) {
        this();
        this.vehicleId = vehicleId;
        this.departurePoint = departure;
        this.destinationPoint = destination;
    }

    /**
     * İzni onaylar
     */
    public void approve(LocalDateTime validUntil) {
        this.status = AuthorizationStatus.APPROVED;
        this.authorizedTime = LocalDateTime.now();
        this.validUntil = validUntil;
    }

    /**
     * İzni reddeder
     */
    public void reject(String reason) {
        this.status = AuthorizationStatus.REJECTED;
        this.reason = reason;
    }

    /**
     * İznin geçerli olup olmadığını kontrol eder
     */
    public boolean isValid() {
        if (status != AuthorizationStatus.APPROVED) {
            return false;
        }
        if (validUntil != null && LocalDateTime.now().isAfter(validUntil)) {
            return false;
        }
        return true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPilotLicense() {
        return pilotLicense;
    }

    public void setPilotLicense(String pilotLicense) {
        this.pilotLicense = pilotLicense;
    }

    public Position getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(Position departurePoint) {
        this.departurePoint = departurePoint;
    }

    public Position getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(Position destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public Route getPlannedRoute() {
        return plannedRoute;
    }

    public void setPlannedRoute(Route plannedRoute) {
        this.plannedRoute = plannedRoute;
    }

    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(LocalDateTime requestedTime) {
        this.requestedTime = requestedTime;
    }

    public LocalDateTime getAuthorizedTime() {
        return authorizedTime;
    }

    public void setAuthorizedTime(LocalDateTime authorizedTime) {
        this.authorizedTime = authorizedTime;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public AuthorizationStatus getStatus() {
        return status;
    }

    public void setStatus(AuthorizationStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}











