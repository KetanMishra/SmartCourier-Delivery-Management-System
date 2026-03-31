package com.smartcourier.delivery.service;

import com.smartcourier.delivery.config.RabbitConfig;
import com.smartcourier.delivery.dto.AddressRequest;
import com.smartcourier.delivery.dto.CreateDeliveryRequest;
import com.smartcourier.delivery.dto.DeliveryResponse;
import com.smartcourier.delivery.dto.DeliveryServicesResponse;
import com.smartcourier.delivery.dto.DeliverySummaryResponse;
import com.smartcourier.delivery.dto.PackageRequest;
import com.smartcourier.delivery.dto.PaymentAuthorizationRequest;
import com.smartcourier.delivery.dto.StatusUpdateRequest;
import com.smartcourier.delivery.entity.AddressEmbeddable;
import com.smartcourier.delivery.entity.Delivery;
import com.smartcourier.delivery.entity.DeliveryStatus;
import com.smartcourier.delivery.entity.PackageEmbeddable;
import com.smartcourier.delivery.entity.ServiceType;
import com.smartcourier.delivery.integration.PaymentGatewayClient;
import com.smartcourier.delivery.messaging.DeliveryLifecycleEvent;
import com.smartcourier.delivery.repository.DeliveryRepository;
import com.smartcourier.delivery.security.JwtUserPrincipal;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final PaymentGatewayClient paymentGatewayClient;
    private final RabbitTemplate rabbitTemplate;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           PaymentGatewayClient paymentGatewayClient,
                           RabbitTemplate rabbitTemplate) {
        this.deliveryRepository = deliveryRepository;
        this.paymentGatewayClient = paymentGatewayClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public DeliveryServicesResponse getSupportedServices() {
        return new DeliveryServicesResponse(
                Arrays.stream(ServiceType.values()).map(Enum::name).toList(),
                List.of("DRAFT", "BOOKED", "PICKED_UP", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"),
                List.of("DELAYED", "FAILED", "RETURNED")
        );
    }

    public DeliveryResponse createDelivery(CreateDeliveryRequest request, JwtUserPrincipal principal) {
        BigDecimal charge = calculateCharge(request);
        String deliveryReference = "DEL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var paymentResponse = paymentGatewayClient.authorize(
                new PaymentAuthorizationRequest(deliveryReference, charge, principal.email()));
        if (!paymentResponse.approved()) {
            throw new IllegalArgumentException("Payment authorization failed");
        }

        Delivery delivery = new Delivery();
        delivery.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        delivery.setCustomerId(principal.userId());
        delivery.setCustomerEmail(principal.email());
        delivery.setSenderAddress(toAddress(request.senderAddress()));
        delivery.setReceiverAddress(toAddress(request.receiverAddress()));
        delivery.setPackageDetails(toPackage(request.packageDetails()));
        delivery.setServiceType(request.serviceType());
        delivery.setStatus(DeliveryStatus.BOOKED);
        delivery.setChargeAmount(charge);
        delivery.setDeclaredValue(request.declaredValue());
        delivery.setScheduledPickupDate(request.scheduledPickupDate());
        delivery.setPaymentReference(paymentResponse.reference());

        Delivery saved = deliveryRepository.save(delivery);
        publishLifecycle(saved, "Delivery booked and payment authorized");
        return toResponse(saved);
    }

    public List<DeliveryResponse> getMyDeliveries(JwtUserPrincipal principal) {
        return deliveryRepository.findByCustomerIdOrderByCreatedAtDesc(principal.userId()).stream().map(this::toResponse).toList();
    }

    public DeliveryResponse getDelivery(Long id, JwtUserPrincipal principal) {
        Delivery delivery = findById(id);
        if (!delivery.getCustomerId().equals(principal.userId()) && principal.roles().stream().noneMatch("ROLE_ADMIN"::equals)) {
            throw new IllegalArgumentException("You are not allowed to access this delivery");
        }
        return toResponse(delivery);
    }

    public DeliveryResponse updateStatus(Long id, StatusUpdateRequest request) {
        Delivery delivery = findById(id);
        delivery.setStatus(request.status());
        Delivery saved = deliveryRepository.save(delivery);
        publishLifecycle(saved, request.note() == null ? "Status updated" : request.note());
        return toResponse(saved);
    }

    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DeliverySummaryResponse getSummary() {
        Map<String, Long> byStatus = deliveryRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        delivery -> delivery.getStatus().name(),
                        Collectors.counting()
                ));
        return new DeliverySummaryResponse(deliveryRepository.count(), byStatus);
    }

    private Delivery findById(Long id) {
        return deliveryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
    }

    private BigDecimal calculateCharge(CreateDeliveryRequest request) {
        BigDecimal base = switch (request.serviceType()) {
            case DOMESTIC -> BigDecimal.valueOf(150);
            case EXPRESS -> BigDecimal.valueOf(280);
            case INTERNATIONAL -> BigDecimal.valueOf(650);
        };
        BigDecimal weightComponent = request.packageDetails().weightKg().multiply(BigDecimal.valueOf(35));
        BigDecimal fragileCharge = request.packageDetails().fragile() ? BigDecimal.valueOf(75) : BigDecimal.ZERO;
        return base.add(weightComponent).add(fragileCharge).setScale(2, RoundingMode.HALF_UP);
    }

    private void publishLifecycle(Delivery delivery, String note) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                new DeliveryLifecycleEvent(
                        delivery.getId(),
                        delivery.getTrackingNumber(),
                        delivery.getCustomerId(),
                        delivery.getCustomerEmail(),
                        delivery.getStatus().name(),
                        note,
                        delivery.getChargeAmount(),
                        Instant.now()
                ));
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getTrackingNumber(),
                delivery.getCustomerId(),
                delivery.getCustomerEmail(),
                toAddressRequest(delivery.getSenderAddress()),
                toAddressRequest(delivery.getReceiverAddress()),
                toPackageRequest(delivery.getPackageDetails()),
                delivery.getServiceType(),
                delivery.getStatus(),
                delivery.getChargeAmount(),
                delivery.getPaymentReference(),
                delivery.getScheduledPickupDate(),
                delivery.getCreatedAt()
        );
    }

    private AddressEmbeddable toAddress(AddressRequest request) {
        AddressEmbeddable address = new AddressEmbeddable();
        address.setContactName(request.contactName());
        address.setPhoneNumber(request.phoneNumber());
        address.setLine1(request.line1());
        address.setLine2(request.line2());
        address.setCity(request.city());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setPostalCode(request.postalCode());
        return address;
    }

    private PackageEmbeddable toPackage(PackageRequest request) {
        PackageEmbeddable pkg = new PackageEmbeddable();
        pkg.setDescription(request.description());
        pkg.setWeightKg(request.weightKg());
        pkg.setLengthCm(request.lengthCm());
        pkg.setWidthCm(request.widthCm());
        pkg.setHeightCm(request.heightCm());
        pkg.setFragile(request.fragile());
        return pkg;
    }

    private AddressRequest toAddressRequest(AddressEmbeddable address) {
        return new AddressRequest(address.getContactName(), address.getPhoneNumber(), address.getLine1(), address.getLine2(),
                address.getCity(), address.getState(), address.getCountry(), address.getPostalCode());
    }

    private PackageRequest toPackageRequest(PackageEmbeddable pkg) {
        return new PackageRequest(pkg.getDescription(), pkg.getWeightKg(), pkg.getLengthCm(), pkg.getWidthCm(), pkg.getHeightCm(), pkg.isFragile());
    }
}
