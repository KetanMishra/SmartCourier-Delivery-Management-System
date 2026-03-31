package com.smartcourier.delivery.repository;

import com.smartcourier.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
