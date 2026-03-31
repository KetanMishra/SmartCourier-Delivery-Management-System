package com.example.demo.repository;

import com.example.demo.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {
    List<TrackingEvent> findByTrackingNumberOrderByEventTimeDesc(String trackingNumber);
    List<TrackingEvent> findByDeliveryId(Long deliveryId);
}
