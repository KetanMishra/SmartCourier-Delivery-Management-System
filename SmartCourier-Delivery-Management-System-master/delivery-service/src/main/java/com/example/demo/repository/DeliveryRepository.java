package com.example.demo.repository;

import com.example.demo.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    List<Delivery> findByCustomerId(Long customerId);

    Optional<Delivery> findByTrackingNumber(String trackingNumber);
}