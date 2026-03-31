package com.smartcourier.delivery.controller;

import com.smartcourier.delivery.dto.CreateDeliveryRequest;
import com.smartcourier.delivery.dto.DeliveryResponse;
import com.smartcourier.delivery.dto.DeliveryServicesResponse;
import com.smartcourier.delivery.dto.DeliverySummaryResponse;
import com.smartcourier.delivery.dto.StatusUpdateRequest;
import com.smartcourier.delivery.security.JwtUserPrincipal;
import com.smartcourier.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/services")
    public DeliveryServicesResponse getServices() {
        return deliveryService.getSupportedServices();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryResponse createDelivery(@Valid @RequestBody CreateDeliveryRequest request,
                                           @AuthenticationPrincipal JwtUserPrincipal principal) {
        return deliveryService.createDelivery(request, principal);
    }

    @GetMapping("/my")
    public List<DeliveryResponse> getMyDeliveries(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return deliveryService.getMyDeliveries(principal);
    }

    @GetMapping("/{id}")
    public DeliveryResponse getDelivery(@PathVariable Long id, @AuthenticationPrincipal JwtUserPrincipal principal) {
        return deliveryService.getDelivery(id, principal);
    }

    @PutMapping("/{id}/status")
    public DeliveryResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return deliveryService.updateStatus(id, request);
    }

    @GetMapping("/internal/admin/all")
    public List<DeliveryResponse> getAllForAdmin() {
        return deliveryService.getAllDeliveries();
    }

    @GetMapping("/internal/summary")
    public DeliverySummaryResponse getSummary() {
        return deliveryService.getSummary();
    }
}
