package com.smartcourier.admin.controller;

import com.smartcourier.admin.dto.DashboardResponse;
import com.smartcourier.admin.dto.DeliveryAdminResponse;
import com.smartcourier.admin.dto.DeliveryStatusUpdateRequest;
import com.smartcourier.admin.dto.HubRequest;
import com.smartcourier.admin.dto.HubResponse;
import com.smartcourier.admin.dto.ReportResponse;
import com.smartcourier.admin.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return adminService.getDashboard();
    }

    @GetMapping("/deliveries")
    public List<DeliveryAdminResponse> deliveries() {
        return adminService.getDeliveries();
    }

    @PutMapping("/deliveries/{id}/resolve")
    public DeliveryAdminResponse resolve(@PathVariable Long id, @RequestBody DeliveryStatusUpdateRequest request) {
        return adminService.resolveException(id, request);
    }

    @GetMapping("/reports")
    public ReportResponse reports() {
        return adminService.getReport();
    }

    @GetMapping("/users")
    public List<?> users() {
        return adminService.getUsers();
    }

    @GetMapping("/hubs")
    public List<HubResponse> hubs() {
        return adminService.getHubs();
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.CREATED)
    public HubResponse createHub(@Valid @RequestBody HubRequest request) {
        return adminService.createHub(request);
    }
}
