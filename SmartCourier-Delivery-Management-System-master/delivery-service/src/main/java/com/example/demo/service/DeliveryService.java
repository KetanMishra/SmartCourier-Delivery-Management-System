package com.example.demo.service;

import com.example.demo.dto.AddressDto;
import com.example.demo.dto.DeliveryRequest;
import com.example.demo.dto.DeliveryResponse;
import com.example.demo.dto.PackageDto;
import com.example.demo.entity.Address;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Package;
import com.example.demo.enums.DeliveryStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    public DeliveryResponse createDelivery(DeliveryRequest request) {
        Address senderAddress = mapToAddressEntity(request.getSenderAddress());
        Address receiverAddress = mapToAddressEntity(request.getReceiverAddress());
        Package packageDetails = mapToPackageEntity(request.getPackageDetails());

        Delivery delivery = new Delivery();
        delivery.setCustomerId(request.getCustomerId());
        delivery.setServiceType(request.getServiceType());
        delivery.setSenderAddress(senderAddress);
        delivery.setReceiverAddress(receiverAddress);
        delivery.setPackageDetails(packageDetails);
        delivery.setCharges(calculateCharges(request.getServiceType(), request.getPackageDetails().getWeightKg()));

        Delivery saved = deliveryRepository.save(delivery);
        return mapToResponse(saved);
    }

    public DeliveryResponse getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        return mapToResponse(delivery);
    }

    public DeliveryResponse getDeliveryByTrackingNumber(String trackingNumber) {
        Delivery delivery = deliveryRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with tracking number: " + trackingNumber));
        return mapToResponse(delivery);
    }

    public List<DeliveryResponse> getDeliveriesByCustomerId(Long customerId) {
        return deliveryRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DeliveryResponse updateStatus(Long id, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        delivery.setStatus(status);
        Delivery updated = deliveryRepository.save(delivery);
        return mapToResponse(updated);
    }

    public DeliveryResponse schedulePickup(Long id, String scheduledPickup) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        delivery.setScheduledPickup(java.time.LocalDateTime.parse(scheduledPickup));
        delivery.setStatus(DeliveryStatus.BOOKED);
        Delivery updated = deliveryRepository.save(delivery);
        return mapToResponse(updated);
    }

    public void deleteDelivery(Long id) {
        deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        deliveryRepository.deleteById(id);
    }

    private Double calculateCharges(String serviceType, Double weightKg) {
        double baseCharge = 50.0;
        double perKgRate;

        switch (serviceType.toUpperCase()) {
            case "EXPRESS":
                perKgRate = 30.0;
                break;
            case "INTERNATIONAL":
                perKgRate = 100.0;
                break;
            default:
                perKgRate = 15.0;
                break;
        }
        return baseCharge + (perKgRate * weightKg);
    }

    private Address mapToAddressEntity(AddressDto dto) {
        Address address = new Address();
        address.setFullName(dto.getFullName());
        address.setPhone(dto.getPhone());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPincode(dto.getPincode());
        address.setCountry(dto.getCountry());
        return address;
    }

    private Package mapToPackageEntity(PackageDto dto) {
        Package pkg = new Package();
        pkg.setDescription(dto.getDescription());
        pkg.setWeightKg(dto.getWeightKg());
        pkg.setLengthCm(dto.getLengthCm());
        pkg.setWidthCm(dto.getWidthCm());
        pkg.setHeightCm(dto.getHeightCm());
        pkg.setCategory(dto.getCategory());
        return pkg;
    }

    private AddressDto mapToAddressDto(Address address) {
        AddressDto dto = new AddressDto();
        dto.setFullName(address.getFullName());
        dto.setPhone(address.getPhone());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPincode(address.getPincode());
        dto.setCountry(address.getCountry());
        return dto;
    }

    private PackageDto mapToPackageDto(Package pkg) {
        PackageDto dto = new PackageDto();
        dto.setDescription(pkg.getDescription());
        dto.setWeightKg(pkg.getWeightKg());
        dto.setLengthCm(pkg.getLengthCm());
        dto.setWidthCm(pkg.getWidthCm());
        dto.setHeightCm(pkg.getHeightCm());
        dto.setCategory(pkg.getCategory());
        return dto;
    }

    private DeliveryResponse mapToResponse(Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(delivery.getId());
        response.setTrackingNumber(delivery.getTrackingNumber());
        response.setCustomerId(delivery.getCustomerId());
        response.setStatus(delivery.getStatus());
        response.setServiceType(delivery.getServiceType());
        response.setSenderAddress(mapToAddressDto(delivery.getSenderAddress()));
        response.setReceiverAddress(mapToAddressDto(delivery.getReceiverAddress()));
        response.setPackageDetails(mapToPackageDto(delivery.getPackageDetails()));
        response.setCharges(delivery.getCharges());
        response.setCreatedAt(delivery.getCreatedAt());
        response.setUpdatedAt(delivery.getUpdatedAt());
        response.setScheduledPickup(delivery.getScheduledPickup());
        return response;
    }
}