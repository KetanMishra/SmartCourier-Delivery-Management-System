package com.smartcourier.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourier.admin.config.JwtAuthenticationFilterTestConfig;
import com.smartcourier.admin.config.SecurityConfig;
import com.smartcourier.admin.dto.DashboardResponse;
import com.smartcourier.admin.dto.HubRequest;
import com.smartcourier.admin.dto.HubResponse;
import com.smartcourier.admin.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilterTestConfig.class})
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @Test
    void dashboardShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void dashboardShouldRejectCustomerUser() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void dashboardShouldAllowAdminUser() throws Exception {
        when(adminService.getDashboard()).thenReturn(
                new DashboardResponse(5L, Map.of("BOOKED", 2L), 1L, 3L, List.of())
        );

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeliveries").value(5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createHubShouldAllowAdminUser() throws Exception {
        HubRequest request = new HubRequest("BLR-01", "Bengaluru", "Karnataka", "India", "Rohit Manager");
        when(adminService.createHub(any())).thenReturn(
                new HubResponse(1L, "BLR-01", "Bengaluru", "Karnataka", "India", "Rohit Manager")
        );

        mockMvc.perform(post("/api/admin/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hubCode").value("BLR-01"));
    }
}
