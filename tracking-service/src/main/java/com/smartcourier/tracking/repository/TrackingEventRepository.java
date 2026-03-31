package com.smartcourier.tracking.repository;

import com.smartcourier.tracking.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {
    List<TrackingEvent> findByTrackingNumberOrderByEventTimeAsc(String trackingNumber);
    List<TrackingEvent> findTop10ByOrderByEventTimeDesc();
}
