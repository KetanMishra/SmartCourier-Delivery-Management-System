package com.example.demo.controller;

import com.example.demo.dto.DeliveryProofRequest;
import com.example.demo.dto.TrackingEventRequest;
import com.example.demo.entity.DeliveryProof;
import com.example.demo.entity.Document;
import com.example.demo.entity.TrackingEvent;
import com.example.demo.service.TrackingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tracking")
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    @PostMapping("/event")
    public ResponseEntity<TrackingEvent> addTrackingEvent(@Valid @RequestBody TrackingEventRequest request) {
        TrackingEvent event = trackingService.addTrackingEvent(request);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @GetMapping("/{trackingNumber}")
    public ResponseEntity<List<TrackingEvent>> getTrackingHistory(@PathVariable String trackingNumber) {
        List<TrackingEvent> events = trackingService.getTrackingHistory(trackingNumber);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/delivery/{deliveryId}")
    public ResponseEntity<List<TrackingEvent>> getTrackingByDeliveryId(@PathVariable Long deliveryId) {
        List<TrackingEvent> events = trackingService.getTrackingByDeliveryId(deliveryId);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PostMapping("/documents/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam String trackingNumber,
            @RequestParam Long deliveryId,
            @RequestParam MultipartFile file) throws IOException {
        Document document = trackingService.uploadDocument(trackingNumber, deliveryId, file);
        return new ResponseEntity<>(document, HttpStatus.CREATED);
    }

    @GetMapping("/documents/{trackingNumber}")
    public ResponseEntity<List<Document>> getDocuments(@PathVariable String trackingNumber) {
        List<Document> documents = trackingService.getDocuments(trackingNumber);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @PostMapping("/proof")
    public ResponseEntity<DeliveryProof> addDeliveryProof(@Valid @RequestBody DeliveryProofRequest request) {
        DeliveryProof proof = trackingService.addDeliveryProof(request);
        return new ResponseEntity<>(proof, HttpStatus.CREATED);
    }

    @GetMapping("/proof/{trackingNumber}")
    public ResponseEntity<DeliveryProof> getDeliveryProof(@PathVariable String trackingNumber) {
        DeliveryProof proof = trackingService.getDeliveryProof(trackingNumber);
        return new ResponseEntity<>(proof, HttpStatus.OK);
    }
}
