package com.airtraffic.model;

/**
 * Yükseklik katmanları.
 * Hava sahasını dikeyde katmanlara bölerek trafik organizasyonu ve güvenliği artırmak için kullanılır.
 */
public enum AltitudeLayer {
    /**
     * Düşük irtifa katmanı.
     * Önerilen kullanım: teslimat dronları, alçak irtifa trafiği.
     * Yükseklik aralığı: 0 - 60 metre.
     */
    LAYER_1_LOW(0.0, 60.0, 15.0),

    /**
     * Orta irtifa katmanı.
     * Önerilen kullanım: şehir içi yolcu dronları ve normal trafik.
     * Yükseklik aralığı: 60 - 120 metre.
     */
    LAYER_2_MEDIUM(60.0, 120.0, 25.0),

    /**
     * Yüksek irtifa katmanı.
     * Önerilen kullanım: acil durum ve öncelikli trafik.
     * Yükseklik aralığı: 120 - 180 metre.
     */
    LAYER_3_HIGH(120.0, 180.0, 35.0);

    private final double minAltitude;   // Metre cinsinden minimum yükseklik (dahil)
    private final double maxAltitude;   // Metre cinsinden maksimum yükseklik (hariç)
    private final double speedLimit;    // Bu katman için önerilen hız limiti (m/s)

    AltitudeLayer(double minAltitude, double maxAltitude, double speedLimit) {
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
        this.speedLimit = speedLimit;
    }

    public double getMinAltitude() {
        return minAltitude;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

    /**
     * Verilen yükseklik için uygun katmanı döner.
     * Alt sınır dahil, üst sınır hariçtir (örn. 60m LAYER_2_MEDIUM'a gider).
     *
     * @param altitude metre cinsinden yükseklik
     * @return İlgili AltitudeLayer veya aralık dışındaysa null
     */
    public static AltitudeLayer fromAltitude(double altitude) {
        for (AltitudeLayer layer : values()) {
            if (altitude >= layer.minAltitude && altitude < layer.maxAltitude) {
                return layer;
            }
        }
        return null;
    }
}





