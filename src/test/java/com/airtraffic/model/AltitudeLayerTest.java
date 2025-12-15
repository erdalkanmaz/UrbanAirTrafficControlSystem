package com.airtraffic.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AltitudeLayer Tests")
class AltitudeLayerTest {

    @Test
    @DisplayName("Test layer boundaries and speed limits")
    void testLayerDefinitions() {
        AltitudeLayer low = AltitudeLayer.LAYER_1_LOW;
        AltitudeLayer medium = AltitudeLayer.LAYER_2_MEDIUM;
        AltitudeLayer high = AltitudeLayer.LAYER_3_HIGH;

        // Low layer: 0 - 60m
        assertEquals(0.0, low.getMinAltitude(), 0.001);
        assertEquals(60.0, low.getMaxAltitude(), 0.001);
        assertTrue(low.getSpeedLimit() > 0, "Low layer should have positive speed limit");

        // Medium layer: 60 - 120m
        assertEquals(60.0, medium.getMinAltitude(), 0.001);
        assertEquals(120.0, medium.getMaxAltitude(), 0.001);
        assertTrue(medium.getSpeedLimit() > low.getSpeedLimit(),
                "Medium layer should have higher speed limit than low layer");

        // High layer: 120 - 180m
        assertEquals(120.0, high.getMinAltitude(), 0.001);
        assertEquals(180.0, high.getMaxAltitude(), 0.001);
        assertTrue(high.getSpeedLimit() >= medium.getSpeedLimit(),
                "High layer should have speed limit >= medium layer");
    }

    @Test
    @DisplayName("Test fromAltitude returns correct layer for typical values")
    void testFromAltitudeTypicalValues() {
        assertEquals(AltitudeLayer.LAYER_1_LOW, AltitudeLayer.fromAltitude(0.0));
        assertEquals(AltitudeLayer.LAYER_1_LOW, AltitudeLayer.fromAltitude(30.0));
        assertEquals(AltitudeLayer.LAYER_1_LOW, AltitudeLayer.fromAltitude(59.99));

        assertEquals(AltitudeLayer.LAYER_2_MEDIUM, AltitudeLayer.fromAltitude(60.0));
        assertEquals(AltitudeLayer.LAYER_2_MEDIUM, AltitudeLayer.fromAltitude(90.0));
        assertEquals(AltitudeLayer.LAYER_2_MEDIUM, AltitudeLayer.fromAltitude(119.99));

        assertEquals(AltitudeLayer.LAYER_3_HIGH, AltitudeLayer.fromAltitude(120.0));
        assertEquals(AltitudeLayer.LAYER_3_HIGH, AltitudeLayer.fromAltitude(150.0));
        assertEquals(AltitudeLayer.LAYER_3_HIGH, AltitudeLayer.fromAltitude(179.99));
    }

    @Test
    @DisplayName("Test fromAltitude returns null for out-of-range values")
    void testFromAltitudeOutOfRange() {
        assertNull(AltitudeLayer.fromAltitude(-1.0), "Negative altitude should return null");
        assertNull(AltitudeLayer.fromAltitude(180.0), "Altitude at upper bound should return null");
        assertNull(AltitudeLayer.fromAltitude(1000.0), "Very high altitude should return null");
    }

    @Test
    @DisplayName("Test fromAltitude at exact boundaries")
    void testFromAltitudeBoundaryValues() {
        // Lower bounds included
        assertEquals(AltitudeLayer.LAYER_1_LOW, AltitudeLayer.fromAltitude(0.0));
        assertEquals(AltitudeLayer.LAYER_2_MEDIUM, AltitudeLayer.fromAltitude(60.0));
        assertEquals(AltitudeLayer.LAYER_3_HIGH, AltitudeLayer.fromAltitude(120.0));

        // Upper bounds excluded
        assertNull(AltitudeLayer.fromAltitude(180.0));
    }
}





