package com.smartcourier.admin.service;

import com.smartcourier.admin.dto.DashboardResponse;
import com.smartcourier.admin.dto.DeliveryAdminResponse;
import com.smartcourier.admin.dto.DeliveryStatusUpdateRequest;
import com.smartcourier.admin.dto.HubRequest;
import com.smartcourier.admin.dto.HubResponse;
import com.smartcourier.admin.dto.ReportResponse;
import com.smartcourier.admin.dto.UserSummaryResponse;
import com.smartcourier.admin.integration.AuthClient;
import com.smartcourier.admin.integration.DeliveryClient;
import com.smartcourier.admin.integration.TrackingClient;
import com.smartcourier.admin.repository.HubRepository;
import com.smartcourier.admin.entity.Hub;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final DeliveryClient deliveryClient;
    private final TrackingClient trackingClient;
    private final AuthClient authClient;
    private final HubRepository hubRepository;

    public AdminService(DeliveryClient deliveryClient,
                        TrackingClient trackingClient,
                        AuthClient authClient,
                        HubRepository hubRepository) {
        this.deliveryClient = deliveryClient;
        this.trackingClient = trackingClient;
        this.authClient = authClient;
        this.hubRepository = hubRepository;
    }

    public DashboardResponse getDashboard() {
        Map<String, Object> summary = deliveryClient.getSummary();
        long totalDeliveries = ((Number) summary.getOrDefault("totalDeliveries", 0)).longValue();
        Map<String, Long> byStatus = normalizeStatusCounts(summary.get("byStatus"));
        return new DashboardResponse(
                totalDeliveries,
                byStatus,
                hubRepository.count(),
                authClient.getUsers().size(),
                trackingClient.getRecentTrackingEvents()
        );
    }

    public List<DeliveryAdminResponse> getDeliveries() {
        return deliveryClient.getAllDeliveries();
    }

    public DeliveryAdminResponse resolveException(Long id, DeliveryStatusUpdateRequest request) {
        return deliveryClient.resolve(id, request);
    }

    public ReportResponse getReport() {
        List<DeliveryAdminResponse> deliveries = deliveryClient.getAllDeliveries();
        long delivered = deliveries.stream().filter(delivery -> "DELIVERED".equals(delivery.status())).count();
        long delayed = deliveries.stream().filter(delivery -> "DELAYED".equals(delivery.status())).count();
        BigDecimal revenue = deliveries.stream()
                .map(DeliveryAdminResponse::chargeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReportResponse("Operational Delivery Report", deliveries.size(), delivered, delayed, revenue, Instant.now());
    }

    public List<HubResponse> getHubs() {
        return hubRepository.findAll().stream().map(this::toResponse).toList();
    }

    public HubResponse createHub(HubRequest request) {
        Hub hub = new Hub();
        hub.setHubCode(request.hubCode());
        hub.setCity(request.city());
        hub.setState(request.state());
        hub.setCountry(request.country());
        hub.setManagerName(request.managerName());
        return toResponse(hubRepository.save(hub));
    }

    public List<UserSummaryResponse> getUsers() {
        return authClient.getUsers();
    }

    private Map<String, Long> normalizeStatusCounts(Object rawStatusMap) {
        if (!(rawStatusMap instanceof Map<?, ?> source)) {
            return Map.of();
        }
        return source.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        entry -> String.valueOf(entry.getKey()),
                        entry -> ((Number) entry.getValue()).longValue()
                ));
    }

    private HubResponse toResponse(Hub hub) {
        return new HubResponse(hub.getId(), hub.getHubCode(), hub.getCity(), hub.getState(), hub.getCountry(), hub.getManagerName());
    }
}
