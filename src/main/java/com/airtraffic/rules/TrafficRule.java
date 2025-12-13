package com.airtraffic.rules;

import java.util.List;
import java.util.UUID;

import com.airtraffic.model.Position;
import com.airtraffic.model.Vehicle;

/**
 * Trafik kuralı tanımı
 */
public class TrafficRule {
    private String id;
    private String name;
    private RuleType ruleType;
    private int priority;                  // Öncelik (yüksek sayı = yüksek öncelik)
    private List<String> applicableZones; // Uygulanabilir bölgeler
    private boolean isActive;              // Aktif mi?

    public TrafficRule() {
        this.id = UUID.randomUUID().toString();
        this.isActive = true;
        this.priority = 0;
    }

    public TrafficRule(String name, RuleType ruleType) {
        this();
        this.name = name;
        this.ruleType = ruleType;
    }

    /**
     * Kuralın bir araç için geçerli olup olmadığını kontrol eder
     * @param vehicle Araç
     * @param position Konum
     * @return Geçerliyse true
     */
    public boolean isApplicable(Vehicle vehicle, Position position) {
        if (!isActive) {
            return false;
        }
        // Alt sınıflarda override edilecek
        return true;
    }

    /**
     * Kural ihlalini kontrol eder
     * @param vehicle Araç
     * @param position Konum
     * @return İhlal varsa true
     */
    public boolean isViolated(Vehicle vehicle, Position position) {
        // Alt sınıflarda override edilecek
        return false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<String> getApplicableZones() {
        return applicableZones;
    }

    public void setApplicableZones(List<String> applicableZones) {
        this.applicableZones = applicableZones;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}











