package com.airtraffic.model;

import com.airtraffic.control.BaseStation;
import com.airtraffic.control.FlightAuthorization;
import com.airtraffic.map.CityMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sistem durumu - kaydedilecek ve yüklenecek tüm verileri içerir
 */
public class SystemState {
    private CityMap cityMap;
    private List<Vehicle> vehicles;
    private List<BaseStation> baseStations;
    private Map<String, FlightAuthorization> authorizations;
    private String centerId;
    private boolean isOperational;
    private long timestamp; // Kayıt zamanı (milliseconds since epoch)

    public SystemState() {
        this.vehicles = new ArrayList<>();
        this.baseStations = new ArrayList<>();
        this.authorizations = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    public SystemState(CityMap cityMap, List<Vehicle> vehicles, List<BaseStation> baseStations,
                       Map<String, FlightAuthorization> authorizations, String centerId, boolean isOperational) {
        this();
        this.cityMap = cityMap;
        this.vehicles = vehicles != null ? new ArrayList<>(vehicles) : new ArrayList<>();
        this.baseStations = baseStations != null ? new ArrayList<>(baseStations) : new ArrayList<>();
        this.authorizations = authorizations != null ? new HashMap<>(authorizations) : new HashMap<>();
        this.centerId = centerId;
        this.isOperational = isOperational;
    }

    // Getters and Setters
    public CityMap getCityMap() {
        return cityMap;
    }

    public void setCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
    }

    public List<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles != null ? new ArrayList<>(vehicles) : new ArrayList<>();
    }

    public List<BaseStation> getBaseStations() {
        return new ArrayList<>(baseStations);
    }

    public void setBaseStations(List<BaseStation> baseStations) {
        this.baseStations = baseStations != null ? new ArrayList<>(baseStations) : new ArrayList<>();
    }

    public Map<String, FlightAuthorization> getAuthorizations() {
        return new HashMap<>(authorizations);
    }

    public void setAuthorizations(Map<String, FlightAuthorization> authorizations) {
        this.authorizations = authorizations != null ? new HashMap<>(authorizations) : new HashMap<>();
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public boolean isOperational() {
        return isOperational;
    }

    public void setOperational(boolean operational) {
        isOperational = operational;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

