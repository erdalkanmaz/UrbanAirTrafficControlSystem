package com.airtraffic.standards;

import java.util.ArrayList;
import java.util.List;

/**
 * ICAO standartları uyumluluk sonucu
 */
public class ComplianceResult {
    private boolean isCompliant;                    // Uyumlu mu?
    private List<String> violations;                // İhlal edilen kurallar
    private List<String> recommendations;           // Öneriler
    private String standardName;                    // Standart adı (örn: "ICAO Annex 2")

    public ComplianceResult() {
        this.isCompliant = true;
        this.violations = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }

    public ComplianceResult(boolean isCompliant, String standardName) {
        this();
        this.isCompliant = isCompliant;
        this.standardName = standardName;
    }

    /**
     * İhlal ekler
     */
    public void addViolation(String violation) {
        if (violation != null && !violation.isEmpty()) {
            this.violations.add(violation);
            this.isCompliant = false; // İhlal varsa uyumlu değil
        }
    }

    /**
     * Öneri ekler
     */
    public void addRecommendation(String recommendation) {
        if (recommendation != null && !recommendation.isEmpty()) {
            this.recommendations.add(recommendation);
        }
    }

    // Getters and Setters

    public boolean isCompliant() {
        return isCompliant;
    }

    public void setCompliant(boolean compliant) {
        isCompliant = compliant;
    }

    public List<String> getViolations() {
        return new ArrayList<>(violations);
    }

    public void setViolations(List<String> violations) {
        this.violations = violations != null ? new ArrayList<>(violations) : new ArrayList<>();
    }

    public List<String> getRecommendations() {
        return new ArrayList<>(recommendations);
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations != null ? new ArrayList<>(recommendations) : new ArrayList<>();
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    @Override
    public String toString() {
        return "ComplianceResult{" +
                "isCompliant=" + isCompliant +
                ", standardName='" + standardName + '\'' +
                ", violations=" + violations.size() +
                ", recommendations=" + recommendations.size() +
                '}';
    }
}








