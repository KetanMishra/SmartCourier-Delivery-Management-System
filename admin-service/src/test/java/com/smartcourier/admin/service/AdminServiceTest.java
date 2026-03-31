package com.smartcourier.admin.service;

import com.smartcourier.admin.dto.DeliveryAdminResponse;
import com.smartcourier.admin.integration.AuthClient;
import com.smartcourier.admin.integration.DeliveryClient;
import com.smartcourier.admin.integration.TrackingClient;
import com.smartcourier.admin.repository.HubRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private DeliveryClient deliveryClient;
    @Mock
    private TrackingClient trackingClient;
    @Mock
    private AuthClient authClient;
    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void getReportShouldAggregateRevenueAndStatuses() {
        when(deliveryClient.getAllDeliveries()).thenReturn(List.of(
                new DeliveryAdminResponse(1L, "TRK1", 1L, "a@x.com", "EXPRESS", "DELIVERED", BigDecimal.valueOf(100), "AUTH1"),
                new DeliveryAdminResponse(2L, "TRK2", 2L, "b@x.com", "DOMESTIC", "DELAYED", BigDecimal.valueOf(150), "AUTH2")
        ));

        var report = adminService.getReport();

        assertThat(report.totalDeliveries()).isEqualTo(2);
        assertThat(report.deliveredCount()).isEqualTo(1);
        assertThat(report.delayedCount()).isEqualTo(1);
        assertThat(report.totalRevenue()).isEqualByComparingTo("250");
    }
}
