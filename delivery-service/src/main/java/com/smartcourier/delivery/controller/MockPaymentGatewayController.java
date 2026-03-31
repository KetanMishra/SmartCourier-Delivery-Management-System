package com.smartcourier.delivery.controller;

import com.smartcourier.delivery.dto.PaymentAuthorizationRequest;
import com.smartcourier.delivery.dto.PaymentAuthorizationResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class MockPaymentGatewayController {

    @PostMapping("/authorize")
    public PaymentAuthorizationResponse authorize(@RequestBody PaymentAuthorizationRequest request) {
        return new PaymentAuthorizationResponse(true, "AUTH-" + request.deliveryReference(), "Authorized by mock gateway");
    }
}
