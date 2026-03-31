package com.smartcourier.tracking.controller;

import com.smartcourier.tracking.config.JwtAuthenticationFilterTestConfig;
import com.smartcourier.tracking.config.SecurityConfig;
import com.smartcourier.tracking.dto.TrackingEventResponse;
import com.smartcourier.tracking.dto.TrackingTimelineResponse;
import com.smartcourier.tracking.security.JwtUserPrincipal;
import com.smartcourier.tracking.service.TrackingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrackingController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilterTestConfig.class})
class TrackingControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackingService trackingService;

    @Test
    void trackShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/tracking/TRK-123456"))
                .andExpect(status().isForbidden());
    }

    @Test
    void trackShouldAllowAuthenticatedUser() throws Exception {
        when(trackingService.getTimeline("TRK-123456")).thenReturn(
                new TrackingTimelineResponse(
                        "TRK-123456",
                        List.of(new TrackingEventResponse(
                                "TRK-123456",
                                "BOOKED",
                                "Bengaluru",
                                "Delivery created",
                                Instant.now()
                        ))
                )
        );

        mockMvc.perform(get("/api/tracking/TRK-123456")
                        .with(authentication(customerAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRK-123456"))
                .andExpect(jsonPath("$.events[0].status").value("BOOKED"));
    }

    @Test
    void recentShouldAllowAuthenticatedUser() throws Exception {
        when(trackingService.getRecentEvents()).thenReturn(List.of(
                new TrackingEventResponse("TRK-123456", "IN_TRANSIT", "Hyderabad", "Scanned at hub", Instant.now())
        ));

        mockMvc.perform(get("/api/tracking/internal/recent")
                        .with(authentication(customerAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trackingNumber").value("TRK-123456"));
    }

    private UsernamePasswordAuthenticationToken customerAuthentication() {
        JwtUserPrincipal principal = new JwtUserPrincipal(10L, "customer@example.com", List.of("ROLE_CUSTOMER"));
        return new UsernamePasswordAuthenticationToken(principal, null, List.of(() -> "ROLE_CUSTOMER"));
    }
}
