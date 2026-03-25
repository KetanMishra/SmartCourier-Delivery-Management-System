package com.example.demo.controller;

import com.example.demo.dto.DeliveryRequest;
import com.example.demo.dto.DeliveryResponse;
import com.example.demo.enums.DeliveryStatus;
import com.example.demo.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@Valid @RequestBody DeliveryRequest request) {
        DeliveryResponse response = deliveryService.createDelivery(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Long id) {
        DeliveryResponse response = deliveryService.getDeliveryById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<DeliveryResponse> getDeliveryByTrackingNumber(@PathVariable String trackingNumber) {
        DeliveryResponse response = deliveryService.getDeliveryByTrackingNumber(trackingNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByCustomerId(@PathVariable Long customerId) {
        List<DeliveryResponse> responses = deliveryService.getDeliveriesByCustomerId(customerId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        List<DeliveryResponse> responses = deliveryService.getAllDeliveries();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam DeliveryStatus status) {
        DeliveryResponse response = deliveryService.updateStatus(id, status);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/schedule")
    public ResponseEntity<DeliveryResponse> schedulePickup(
            @PathVariable Long id,
            @RequestParam String scheduledPickup) {
        DeliveryResponse response = deliveryService.schedulePickup(id, scheduledPickup);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return new ResponseEntity<>("Delivery deleted successfully", HttpStatus.OK);
    }
}