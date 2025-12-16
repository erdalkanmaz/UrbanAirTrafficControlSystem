package com.airtraffic.model;

/**
 * Çarpışma risk seviyesi enum
 * ICAO Annex 2 standartlarına uygun risk seviyeleri
 */
public enum RiskLevel {
    /**
     * Düşük risk - Normal operasyon
     * Araçlar arası mesafe yeterli, çarpışma riski yok
     */
    LOW,
    
    /**
     * Orta risk - Dikkat gerekli
     * Araçlar yakınlaşıyor, izleme gerekli
     */
    MEDIUM,
    
    /**
     * Yüksek risk - Uyarı gerekli
     * Araçlar çok yakın, müdahale önerilir
     */
    HIGH,
    
    /**
     * Kritik risk - Acil müdahale gerekli
     * Çarpışma kaçınılmaz görünüyor, acil aksiyon gerekli
     */
    CRITICAL
}








