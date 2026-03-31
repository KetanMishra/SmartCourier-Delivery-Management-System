package com.smartcourier.tracking.service;

import com.smartcourier.tracking.messaging.DeliveryLifecycleEvent;
import com.smartcourier.tracking.repository.DeliveryProofRepository;
import com.smartcourier.tracking.repository.DocumentRecordRepository;
import com.smartcourier.tracking.repository.TrackingEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @Mock
    private TrackingEventRepository trackingEventRepository;
    @Mock
    private DocumentRecordRepository documentRecordRepository;
    @Mock
    private DeliveryProofRepository deliveryProofRepository;

    @InjectMocks
    private TrackingService trackingService;

    @Test
    void consumeDeliveryLifecycleShouldPersistTimelineEntry() {
        trackingService.consumeDeliveryLifecycle(new DeliveryLifecycleEvent(
                1L, "TRK-1", 2L, "customer@example.com", "DELIVERED", "Done", BigDecimal.TEN, Instant.now()
        ));

        verify(trackingEventRepository).save(any());
        verify(deliveryProofRepository).save(any());
    }
}
