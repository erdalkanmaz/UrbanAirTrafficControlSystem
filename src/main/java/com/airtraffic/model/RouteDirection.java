package com.airtraffic.model;

/**
 * Yol yönü enum
 * Yol segmentlerinin gidiş/geliş yönlerini belirtir
 */
public enum RouteDirection {
    /**
     * Gidiş yönü (forward direction)
     * Waypoint'lerin sırasına göre ileri yön
     */
    FORWARD,
    
    /**
     * Geliş yönü (reverse direction)
     * Waypoint'lerin ters sırasına göre geri yön
     */
    REVERSE
}

