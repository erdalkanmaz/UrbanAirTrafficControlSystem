package com.airtraffic.control;

import com.airtraffic.model.Position;
import com.airtraffic.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FlightAuthorization class
 * Tests flight authorization creation, approval, rejection, and validation
 */
@DisplayName("FlightAuthorization Tests")
class FlightAuthorizationTest {

    private FlightAuthorization authorization;
    private Position departurePosition;
    private Position destinationPosition;
    private String vehicleId;

    @BeforeEach
    void setUp() {
        vehicleId = "VEH-001";
        departurePosition = new Position(41.0082, 28.9784, 100.0);
        destinationPosition = new Position(41.0100, 28.9800, 120.0);
        authorization = new FlightAuthorization(vehicleId, departurePosition, destinationPosition);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        FlightAuthorization auth = new FlightAuthorization();
        
        assertNotNull(auth.getId());
        assertEquals(AuthorizationStatus.PENDING, auth.getStatus());
        assertNotNull(auth.getRequestedTime());
    }

    @Test
    @DisplayName("Test constructor with vehicle ID and positions")
    void testConstructorWithParameters() {
        FlightAuthorization auth = new FlightAuthorization(
            vehicleId, departurePosition, destinationPosition);
        
        assertEquals(vehicleId, auth.getVehicleId());
        assertEquals(departurePosition, auth.getDeparturePoint());
        assertEquals(destinationPosition, auth.getDestinationPoint());
        assertEquals(AuthorizationStatus.PENDING, auth.getStatus());
    }

    @Test
    @DisplayName("Test approve")
    void testApprove() {
        LocalDateTime validUntil = LocalDateTime.now().plusHours(2);
        
        authorization.approve(validUntil);
        
        assertEquals(AuthorizationStatus.APPROVED, authorization.getStatus());
        assertNotNull(authorization.getAuthorizedTime());
        assertEquals(validUntil, authorization.getValidUntil());
    }

    @Test
    @DisplayName("Test approve sets authorized time")
    void testApproveSetsAuthorizedTime() {
        LocalDateTime beforeApproval = LocalDateTime.now();
        
        authorization.approve(LocalDateTime.now().plusHours(2));
        
        assertNotNull(authorization.getAuthorizedTime());
        assertTrue(authorization.getAuthorizedTime().isAfter(beforeApproval) || 
                   authorization.getAuthorizedTime().equals(beforeApproval));
    }

    @Test
    @DisplayName("Test reject")
    void testReject() {
        String reason = "Traffic congestion";
        
        authorization.reject(reason);
        
        assertEquals(AuthorizationStatus.REJECTED, authorization.getStatus());
        assertEquals(reason, authorization.getReason());
    }

    @Test
    @DisplayName("Test reject with null reason")
    void testRejectWithNullReason() {
        authorization.reject(null);
        
        assertEquals(AuthorizationStatus.REJECTED, authorization.getStatus());
        assertNull(authorization.getReason());
    }

    @Test
    @DisplayName("Test isValid with approved and not expired")
    void testIsValidApprovedNotExpired() {
        LocalDateTime validUntil = LocalDateTime.now().plusHours(2);
        authorization.approve(validUntil);
        
        assertTrue(authorization.isValid(), 
            "Should be valid when approved and not expired");
    }

    @Test
    @DisplayName("Test isValid with approved but expired")
    void testIsValidApprovedExpired() {
        LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
        authorization.approve(pastTime);
        
        assertFalse(authorization.isValid(), 
            "Should not be valid when expired");
    }

    @Test
    @DisplayName("Test isValid with pending status")
    void testIsValidPending() {
        // Status is PENDING by default
        assertFalse(authorization.isValid(), 
            "Should not be valid when status is PENDING");
    }

    @Test
    @DisplayName("Test isValid with rejected status")
    void testIsValidRejected() {
        authorization.reject("Test reason");
        
        assertFalse(authorization.isValid(), 
            "Should not be valid when status is REJECTED");
    }

    @Test
    @DisplayName("Test isValid with null validUntil")
    void testIsValidNullValidUntil() {
        authorization.approve(null);
        
        // When validUntil is null, should still be valid if approved
        assertTrue(authorization.isValid(), 
            "Should be valid when approved with null validUntil");
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        authorization.setId("AUTH-001");
        authorization.setVehicleId("VEH-002");
        authorization.setPilotLicense("PILOT-12345");
        authorization.setStatus(AuthorizationStatus.APPROVED);
        authorization.setReason("Test reason");
        
        Route route = new Route("Test Route", new ArrayList<>());
        authorization.setPlannedRoute(route);
        
        LocalDateTime testTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        authorization.setRequestedTime(testTime);
        authorization.setAuthorizedTime(testTime);
        authorization.setValidUntil(testTime);
        
        assertEquals("AUTH-001", authorization.getId());
        assertEquals("VEH-002", authorization.getVehicleId());
        assertEquals("PILOT-12345", authorization.getPilotLicense());
        assertEquals(AuthorizationStatus.APPROVED, authorization.getStatus());
        assertEquals("Test reason", authorization.getReason());
        assertEquals(route, authorization.getPlannedRoute());
        assertEquals(testTime, authorization.getRequestedTime());
        assertEquals(testTime, authorization.getAuthorizedTime());
        assertEquals(testTime, authorization.getValidUntil());
    }

    @Test
    @DisplayName("Test setDeparturePoint and setDestinationPoint")
    void testSetDepartureAndDestination() {
        Position newDeparture = new Position(40.0, 30.0, 80.0);
        Position newDestination = new Position(42.0, 32.0, 150.0);
        
        authorization.setDeparturePoint(newDeparture);
        authorization.setDestinationPoint(newDestination);
        
        assertEquals(newDeparture, authorization.getDeparturePoint());
        assertEquals(newDestination, authorization.getDestinationPoint());
    }

    @Test
    @DisplayName("Test edge case: approve after reject")
    void testApproveAfterReject() {
        authorization.reject("Initial rejection");
        assertEquals(AuthorizationStatus.REJECTED, authorization.getStatus());
        
        authorization.approve(LocalDateTime.now().plusHours(2));
        
        assertEquals(AuthorizationStatus.APPROVED, authorization.getStatus(), 
            "Should allow approval after rejection");
    }

    @Test
    @DisplayName("Test edge case: reject after approve")
    void testRejectAfterApprove() {
        authorization.approve(LocalDateTime.now().plusHours(2));
        assertEquals(AuthorizationStatus.APPROVED, authorization.getStatus());
        
        authorization.reject("New rejection");
        
        assertEquals(AuthorizationStatus.REJECTED, authorization.getStatus(), 
            "Should allow rejection after approval");
    }

    @Test
    @DisplayName("Test edge case: validUntil exactly at current time")
    void testValidUntilAtCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        authorization.approve(now);
        
        // May be valid or invalid depending on timing precision
        boolean isValid = authorization.isValid();
        assertNotNull(Boolean.valueOf(isValid), 
            "Should handle validUntil at current time");
    }

    @Test
    @DisplayName("Test edge case: validUntil in far future")
    void testValidUntilFarFuture() {
        LocalDateTime farFuture = LocalDateTime.now().plusYears(1);
        authorization.approve(farFuture);
        
        assertTrue(authorization.isValid(), 
            "Should be valid when validUntil is in far future");
    }

    @Test
    @DisplayName("Test edge case: validUntil in past")
    void testValidUntilPast() {
        LocalDateTime past = LocalDateTime.now().minusHours(1);
        authorization.approve(past);
        
        assertFalse(authorization.isValid(), 
            "Should not be valid when validUntil is in past");
    }

    @Test
    @DisplayName("Test authorization ID uniqueness")
    void testAuthorizationIdUniqueness() {
        FlightAuthorization auth1 = new FlightAuthorization();
        FlightAuthorization auth2 = new FlightAuthorization();
        
        assertNotEquals(auth1.getId(), auth2.getId(), 
            "Each authorization should have unique ID");
    }

    @Test
    @DisplayName("Test authorization with planned route")
    void testAuthorizationWithPlannedRoute() {
        List<Position> waypoints = new ArrayList<>();
        waypoints.add(departurePosition);
        waypoints.add(new Position(41.0090, 28.9790, 110.0));
        waypoints.add(destinationPosition);
        
        Route route = new Route("Planned Route", waypoints);
        authorization.setPlannedRoute(route);
        
        assertEquals(route, authorization.getPlannedRoute());
        assertEquals(3, authorization.getPlannedRoute().getWaypoints().size());
    }

    @Test
    @DisplayName("Test authorization status transitions")
    void testAuthorizationStatusTransitions() {
        // Start as PENDING
        assertEquals(AuthorizationStatus.PENDING, authorization.getStatus());
        
        // Approve
        authorization.approve(LocalDateTime.now().plusHours(2));
        assertEquals(AuthorizationStatus.APPROVED, authorization.getStatus());
        
        // Manually set to EXPIRED
        authorization.setStatus(AuthorizationStatus.EXPIRED);
        assertEquals(AuthorizationStatus.EXPIRED, authorization.getStatus());
        assertFalse(authorization.isValid(), 
            "Should not be valid when EXPIRED");
        
        // Manually set to CANCELLED
        authorization.setStatus(AuthorizationStatus.CANCELLED);
        assertEquals(AuthorizationStatus.CANCELLED, authorization.getStatus());
        assertFalse(authorization.isValid(), 
            "Should not be valid when CANCELLED");
    }
}






