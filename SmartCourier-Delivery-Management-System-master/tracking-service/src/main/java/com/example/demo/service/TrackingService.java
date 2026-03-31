package com.example.demo.service;

import com.example.demo.dto.DeliveryProofRequest;
import com.example.demo.dto.TrackingEventRequest;
import com.example.demo.entity.DeliveryProof;
import com.example.demo.entity.Document;
import com.example.demo.entity.TrackingEvent;
import com.example.demo.enums.TrackingStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.DeliveryProofRepository;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.TrackingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TrackingService {

    @Autowired
    private TrackingEventRepository trackingEventRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DeliveryProofRepository deliveryProofRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public TrackingEvent addTrackingEvent(TrackingEventRequest request) {
        TrackingEvent event = new TrackingEvent();
        event.setTrackingNumber(request.getTrackingNumber());
        event.setDeliveryId(request.getDeliveryId());
        event.setStatus(TrackingStatus.valueOf(request.getStatus()));
        event.setLocation(request.getLocation());
        event.setDescription(request.getDescription());
        return trackingEventRepository.save(event);
    }

    public List<TrackingEvent> getTrackingHistory(String trackingNumber) {
        List<TrackingEvent> events = trackingEventRepository
                .findByTrackingNumberOrderByEventTimeDesc(trackingNumber);
        if (events.isEmpty()) {
            throw new ResourceNotFoundException("No tracking events found for: " + trackingNumber);
        }
        return events;
    }

    public List<TrackingEvent> getTrackingByDeliveryId(Long deliveryId) {
        return trackingEventRepository.findByDeliveryId(deliveryId);
    }

    public Document uploadDocument(String trackingNumber, Long deliveryId, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        Document document = new Document();
        document.setTrackingNumber(trackingNumber);
        document.setDeliveryId(deliveryId);
        document.setFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setFilePath(filePath.toString());
        return documentRepository.save(document);
    }

    public List<Document> getDocuments(String trackingNumber) {
        return documentRepository.findByTrackingNumber(trackingNumber);
    }

    public DeliveryProof addDeliveryProof(DeliveryProofRequest request) {
        DeliveryProof proof = new DeliveryProof();
        proof.setTrackingNumber(request.getTrackingNumber());
        proof.setDeliveryId(request.getDeliveryId());
        proof.setReceiverName(request.getReceiverName());
        proof.setSignature(request.getSignature());
        return deliveryProofRepository.save(proof);
    }

    public DeliveryProof getDeliveryProof(String trackingNumber) {
        return deliveryProofRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Proof not found for: " + trackingNumber));
    }
}
