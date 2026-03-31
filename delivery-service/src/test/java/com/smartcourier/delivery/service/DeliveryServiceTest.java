package com.smartcourier.delivery.service;

import com.smartcourier.delivery.dto.AddressRequest;
import com.smartcourier.delivery.dto.CreateDeliveryRequest;
import com.smartcourier.delivery.dto.PackageRequest;
import com.smartcourier.delivery.dto.PaymentAuthorizationResponse;
import com.smartcourier.delivery.entity.Delivery;
import com.smartcourier.delivery.entity.ServiceType;
import com.smartcourier.delivery.integration.PaymentGatewayClient;
import com.smartcourier.delivery.repository.DeliveryRepository;
import com.smartcourier.delivery.security.JwtUserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private PaymentGatewayClient paymentGatewayClient;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DeliveryService deliveryService;

    @Test
    void createDeliveryShouldAuthorizePaymentAndPersistBooking() {
        CreateDeliveryRequest request = new CreateDeliveryRequest(
                new AddressRequest("Sender", "9999999999", "Street 1", null, "Pune", "MH", "India", "411001"),
                new AddressRequest("Receiver", "8888888888", "Street 2", null, "Delhi", "DL", "India", "110001"),
                new PackageRequest("Documents", BigDecimal.valueOf(2), BigDecimal.valueOf(10), BigDecimal.valueOf(5), BigDecimal.valueOf(3), false),
                ServiceType.EXPRESS,
                LocalDate.now().plusDays(1),
                BigDecimal.valueOf(1000)
        );
        JwtUserPrincipal principal = new JwtUserPrincipal(1L, "customer@example.com", List.of("ROLE_CUSTOMER"));
        when(paymentGatewayClient.authorize(any())).thenReturn(new PaymentAuthorizationResponse(true, "AUTH-1", "OK"));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> {
            Delivery saved = invocation.getArgument(0);
            saved.setId(50L);
            return saved;
        });

        var response = deliveryService.createDelivery(request, principal);

        ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository).save(captor.capture());
        assertThat(captor.getValue().getCustomerEmail()).isEqualTo("customer@example.com");
        assertThat(response.status().name()).isEqualTo("BOOKED");
        assertThat(response.paymentReference()).isEqualTo("AUTH-1");
    }
}
