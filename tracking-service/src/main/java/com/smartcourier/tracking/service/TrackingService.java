package com.smartcourier.tracking.service;

import com.smartcourier.tracking.dto.DeliveryProofResponse;
import com.smartcourier.tracking.dto.DocumentUploadRequest;
import com.smartcourier.tracking.dto.TrackingEventResponse;
import com.smartcourier.tracking.dto.TrackingTimelineResponse;
import com.smartcourier.tracking.entity.DeliveryProof;
import com.smartcourier.tracking.entity.DocumentRecord;
import com.smartcourier.tracking.entity.TrackingEvent;
import com.smartcourier.tracking.messaging.DeliveryLifecycleEvent;
import com.smartcourier.tracking.repository.DeliveryProofRepository;
import com.smartcourier.tracking.repository.DocumentRecordRepository;
import com.smartcourier.tracking.repository.TrackingEventRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackingService {

    private final TrackingEventRepository trackingEventRepository;
    private final DocumentRecordRepository documentRecordRepository;
    private final DeliveryProofRepository deliveryProofRepository;

    public TrackingService(TrackingEventRepository trackingEventRepository,
                           DocumentRecordRepository documentRecordRepository,
                           DeliveryProofRepository deliveryProofRepository) {
        this.trackingEventRepository = trackingEventRepository;
        this.documentRecordRepository = documentRecordRepository;
        this.deliveryProofRepository = deliveryProofRepository;
    }

    @RabbitListener(queues = com.smartcourier.tracking.config.RabbitConfig.QUEUE)
    public void consumeDeliveryLifecycle(DeliveryLifecycleEvent event) {
        TrackingEvent trackingEvent = new TrackingEvent();
        trackingEvent.setDeliveryId(event.deliveryId());
        trackingEvent.setTrackingNumber(event.trackingNumber());
        trackingEvent.setStatus(event.status());
        trackingEvent.setLocation("SmartCourier Hub");
        trackingEvent.setNote(event.note());
        trackingEvent.setEventTime(event.happenedAt());
        trackingEventRepository.save(trackingEvent);

        if ("DELIVERED".equals(event.status())) {
            DeliveryProof proof = new DeliveryProof();
            proof.setDeliveryId(event.deliveryId());
            proof.setRecipientName("Receiver Confirmed");
            proof.setProofImageUrl("https://smartcourier.local/proofs/" + event.trackingNumber());
            proof.setNote("Package delivered successfully");
            proof.setDeliveredAt(event.happenedAt());
            deliveryProofRepository.save(proof);
        }
    }

    public DocumentRecord uploadDocument(DocumentUploadRequest request) {
        DocumentRecord document = new DocumentRecord();
        document.setDeliveryId(request.deliveryId());
        document.setTrackingNumber(request.trackingNumber());
        document.setDocumentType(request.documentType());
        document.setFileName(request.fileName());
        document.setContentType(request.contentType());
        document.setStorageUrl(request.storageUrl());
        return documentRecordRepository.save(document);
    }

    public TrackingTimelineResponse getTimeline(String trackingNumber) {
        List<TrackingEventResponse> events = trackingEventRepository.findByTrackingNumberOrderByEventTimeAsc(trackingNumber).stream()
                .map(event -> new TrackingEventResponse(event.getTrackingNumber(), event.getStatus(), event.getLocation(), event.getNote(), event.getEventTime()))
                .toList();
        return new TrackingTimelineResponse(trackingNumber, events);
    }

    public DeliveryProofResponse getProof(Long deliveryId) {
        DeliveryProof proof = deliveryProofRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery proof not found"));
        return new DeliveryProofResponse(proof.getDeliveryId(), proof.getRecipientName(), proof.getProofImageUrl(), proof.getNote(), proof.getDeliveredAt());
    }

    public List<TrackingEventResponse> getRecentEvents() {
        return trackingEventRepository.findTop10ByOrderByEventTimeDesc().stream()
                .map(event -> new TrackingEventResponse(event.getTrackingNumber(), event.getStatus(), event.getLocation(), event.getNote(), event.getEventTime()))
                .toList();
    }
}
