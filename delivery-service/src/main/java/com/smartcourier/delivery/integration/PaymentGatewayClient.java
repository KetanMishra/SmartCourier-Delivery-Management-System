package com.smartcourier.delivery.integration;

import com.smartcourier.delivery.dto.PaymentAuthorizationRequest;
import com.smartcourier.delivery.dto.PaymentAuthorizationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "paymentGatewayClient", url = "${payment.gateway.url:http://localhost:8082}")
public interface PaymentGatewayClient {

    @PostMapping("/api/payments/authorize")
    PaymentAuthorizationResponse authorize(@RequestBody PaymentAuthorizationRequest request);
}
