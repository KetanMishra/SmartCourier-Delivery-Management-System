package com.smartcourier.delivery.controller;

import com.smartcourier.delivery.config.JwtAuthenticationFilterTestConfig;
import com.smartcourier.delivery.config.SecurityConfig;
import com.smartcourier.delivery.dto.DeliveryResponse;
import com.smartcourier.delivery.dto.DeliveryServicesResponse;
import com.smartcourier.delivery.dto.DeliverySummaryResponse;
import com.smartcourier.delivery.security.JwtUserPrincipal;
import com.smartcourier.delivery.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilterTestConfig.class})
class DeliveryControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @Test
    void getServicesShouldBeAccessibleWithoutAuthentication() throws Exception {
        when(deliveryService.getSupportedServices()).thenReturn(
                new DeliveryServicesResponse(
                        List.of("DOMESTIC", "EXPRESS"),
                        List.of("BOOKED", "IN_TRANSIT"),
                        List.of("DELAYED")
                )
        );

        mockMvc.perform(get("/api/deliveries/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supportedServices[0]").value("DOMESTIC"));
    }

    @Test
    void getMyDeliveriesShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/deliveries/my"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyDeliveriesShouldAllowAuthenticatedUser() throws Exception {
        when(deliveryService.getMyDeliveries(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/deliveries/my")
                        .with(authentication(customerAuthentication())))
                .andExpect(status().isOk());
    }

    @Test
    void internalSummaryShouldRejectCustomerUser() throws Exception {
        mockMvc.perform(get("/api/deliveries/internal/summary")
                        .with(authentication(customerAuthentication())))
                .andExpect(status().isForbidden());
    }

    @Test
    void internalSummaryShouldAllowAdminUser() throws Exception {
        when(deliveryService.getSummary()).thenReturn(
                new DeliverySummaryResponse(2L, Map.of("BOOKED", 1L, "DELIVERED", 1L))
        );

        mockMvc.perform(get("/api/deliveries/internal/summary")
                        .with(authentication(adminAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeliveries").value(2));
    }

    private UsernamePasswordAuthenticationToken customerAuthentication() {
        JwtUserPrincipal principal = new JwtUserPrincipal(10L, "customer@example.com", List.of("ROLE_CUSTOMER"));
        return new UsernamePasswordAuthenticationToken(principal, null, List.of(() -> "ROLE_CUSTOMER"));
    }

    private UsernamePasswordAuthenticationToken adminAuthentication() {
        JwtUserPrincipal principal = new JwtUserPrincipal(1L, "admin@example.com", List.of("ROLE_ADMIN"));
        return new UsernamePasswordAuthenticationToken(principal, null, List.of(() -> "ROLE_ADMIN"));
    }
}
