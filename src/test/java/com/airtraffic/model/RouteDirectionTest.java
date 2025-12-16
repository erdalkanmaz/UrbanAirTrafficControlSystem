package com.airtraffic.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RouteDirection enum
 */
@DisplayName("RouteDirection Tests")
class RouteDirectionTest {

    @Test
    @DisplayName("Test RouteDirection enum values")
    void testRouteDirectionValues() {
        RouteDirection[] values = RouteDirection.values();
        
        assertEquals(2, values.length);
        assertEquals(RouteDirection.FORWARD, values[0]);
        assertEquals(RouteDirection.REVERSE, values[1]);
    }

    @Test
    @DisplayName("Test RouteDirection valueOf")
    void testRouteDirectionValueOf() {
        assertEquals(RouteDirection.FORWARD, RouteDirection.valueOf("FORWARD"));
        assertEquals(RouteDirection.REVERSE, RouteDirection.valueOf("REVERSE"));
    }

    @Test
    @DisplayName("Test RouteDirection valueOf with invalid value")
    void testRouteDirectionValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            RouteDirection.valueOf("INVALID");
        });
    }
}

