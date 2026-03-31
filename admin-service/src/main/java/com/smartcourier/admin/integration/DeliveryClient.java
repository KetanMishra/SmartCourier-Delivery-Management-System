package com.smartcourier.admin.integration;

import com.smartcourier.admin.dto.DeliveryAdminResponse;
import com.smartcourier.admin.dto.DeliveryStatusUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/api/deliveries/internal/admin/all")
    List<DeliveryAdminResponse> getAllDeliveries();

    @GetMapping("/api/deliveries/internal/summary")
    Map<String, Object> getSummary();

    @PutMapping("/api/deliveries/{id}/status")
    DeliveryAdminResponse resolve(@PathVariable("id") Long id, @RequestBody DeliveryStatusUpdateRequest request);
}
