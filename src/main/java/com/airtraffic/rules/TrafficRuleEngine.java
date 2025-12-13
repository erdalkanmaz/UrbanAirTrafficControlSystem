package com.airtraffic.rules;

import com.airtraffic.model.Vehicle;
import com.airtraffic.model.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Trafik kuralı motoru - tüm trafik kurallarını yönetir ve uygular
 */
public class TrafficRuleEngine {
    private List<TrafficRule> rules;
    private boolean enabled;

    public TrafficRuleEngine() {
        this.rules = new ArrayList<>();
        this.enabled = true;
        initializeDefaultRules();
    }

    /**
     * Varsayılan trafik kurallarını başlatır
     */
    private void initializeDefaultRules() {
        // Hız limiti kuralları
        SpeedLimitRule mainStreetSpeedLimit = new SpeedLimitRule("Ana Cadde Hız Limiti", 60.0 / 3.6); // 60 km/h = 16.67 m/s
        mainStreetSpeedLimit.setPriority(10);
        rules.add(mainStreetSpeedLimit);

        SpeedLimitRule sideStreetSpeedLimit = new SpeedLimitRule("Sokak Hız Limiti", 40.0 / 3.6); // 40 km/h = 11.11 m/s
        sideStreetSpeedLimit.setPriority(10);
        rules.add(sideStreetSpeedLimit);

        // Giriş/çıkış kuralları
        EntryExitRule entryExitRule = new EntryExitRule("Giriş/Çıkış Kuralları");
        entryExitRule.setPriority(15); // Yüksek öncelik
        rules.add(entryExitRule);
    }

    /**
     * Kural ekler
     */
    public void addRule(TrafficRule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Kural null olamaz");
        }
        this.rules.add(rule);
        // Önceliğe göre sırala
        rules.sort(Comparator.comparing(TrafficRule::getPriority).reversed());
    }

    /**
     * Kural kaldırır
     */
    public void removeRule(String ruleId) {
        rules.removeIf(rule -> rule.getId().equals(ruleId));
    }

    /**
     * Bir araç için ihlal edilen kuralları kontrol eder
     * @param vehicle Araç
     * @param position Konum
     * @return İhlal edilen kurallar listesi
     */
    public List<TrafficRule> checkViolations(Vehicle vehicle, Position position) {
        if (!enabled) {
            return new ArrayList<>();
        }

        return rules.stream()
                .filter(rule -> rule.isApplicable(vehicle, position))
                .filter(rule -> rule.isViolated(vehicle, position))
                .collect(Collectors.toList());
    }

    /**
     * Bir araç için uyarı gerektiren kuralları kontrol eder
     * @param vehicle Araç
     * @param position Konum
     * @return Uyarı gerektiren kurallar listesi
     */
    public List<TrafficRule> checkWarnings(Vehicle vehicle, Position position) {
        if (!enabled) {
            return new ArrayList<>();
        }

        List<TrafficRule> warnings = new ArrayList<>();

        for (TrafficRule rule : rules) {
            if (rule.isApplicable(vehicle, position)) {
                // Hız limiti için özel uyarı kontrolü
                if (rule instanceof SpeedLimitRule) {
                    SpeedLimitRule speedRule = (SpeedLimitRule) rule;
                    if (speedRule.isWarningNeeded(vehicle)) {
                        warnings.add(rule);
                    }
                }
                // Diğer uyarı kontrolleri buraya eklenebilir
            }
        }

        return warnings;
    }

    /**
     * Belirli bir tip kuralı döndürür
     */
    public List<TrafficRule> getRulesByType(RuleType ruleType) {
        return rules.stream()
                .filter(rule -> rule.getRuleType() == ruleType)
                .collect(Collectors.toList());
    }

    /**
     * Aktif kuralları döndürür
     */
    public List<TrafficRule> getActiveRules() {
        return rules.stream()
                .filter(TrafficRule::isActive)
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public List<TrafficRule> getRules() {
        return new ArrayList<>(rules);
    }

    public void setRules(List<TrafficRule> rules) {
        this.rules = new ArrayList<>(rules);
        this.rules.sort(Comparator.comparing(TrafficRule::getPriority).reversed());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}











