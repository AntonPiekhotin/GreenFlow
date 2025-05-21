package org.greenflow.equipment.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.event.PaymentCreationMessage;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.equipment.model.constant.LeasingStatus;
import org.greenflow.equipment.model.entity.Equipment;
import org.greenflow.equipment.model.entity.EquipmentLease;
import org.greenflow.equipment.output.event.RabbitMQProducer;
import org.greenflow.equipment.output.persistent.EquipmentLeaseRepository;
import org.greenflow.equipment.output.persistent.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeasingService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentLeaseRepository equipmentLeaseRepository;
    private final RabbitMQProducer rabbitMQProducer;

    public EquipmentLease requestLeaseEquipment(@NotBlank String equipmentId, @NotBlank String lesseeId) {
        log.debug("request lease equipment {} from worker {}", equipmentId, lesseeId);
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new GreenFlowException(400, "Equipment not found"));
        if (equipment.getStatus() != null &&
                (equipment.getStatus().equals(LeasingStatus.ACTIVE)
                        || equipment.getStatus().equals(LeasingStatus.PENDING))) {
            throw new GreenFlowException(400, "Equipment is already leased");
        }
        EquipmentLease lease = EquipmentLease.builder()
                .equipmentId(equipmentId)
                .lesseeId(lesseeId)
                .dailyRate(equipment.getDailyLeasingPrice())
                .startDate(LocalDateTime.now())
                .status(LeasingStatus.PENDING)
                .build();
        equipmentLeaseRepository.save(lease);
        log.info("Lease request created with id {} for worker {}", lease.getId(), lease.getLesseeId());

        equipment.setStatus(LeasingStatus.PENDING);
        equipment.setLeasedBy(lesseeId);
        equipmentRepository.save(equipment);
        log.info("Equipment {} is now pending for lease", equipmentId);
        return lease;
    }

    public List<EquipmentLease> getLeasedEquipment(@NotBlank String lesseeId) {
        return equipmentLeaseRepository.findAllByLesseeId(lesseeId);
    }

    public EquipmentLease approveLease(@NotNull Long leaseId) {
        EquipmentLease lease = equipmentLeaseRepository.findById(leaseId)
                .orElseThrow(() -> new GreenFlowException(400, "Lease not found"));
        if (lease.getStatus() != LeasingStatus.PENDING) {
            throw new GreenFlowException(400, "Lease is not pending");
        }
        lease.setStatus(LeasingStatus.ACTIVE);
        lease.setStartDate(LocalDateTime.now());
        log.info("Lease {} approved", leaseId);
        return equipmentLeaseRepository.save(lease);
    }

    public EquipmentLease closeLease(@NotNull Long leaseId) {
        log.debug("request to close lease {}", leaseId);
        EquipmentLease lease = equipmentLeaseRepository.findById(leaseId)
                .orElseThrow(() -> new GreenFlowException(400, "Lease not found"));
        Equipment equipment = equipmentRepository.findById(lease.getEquipmentId())
                .orElseThrow(() -> new GreenFlowException(400, "Equipment not found"));
        if (lease.getStatus() != LeasingStatus.ACTIVE) {
            throw new GreenFlowException(400, "Lease is not active");
        }
        lease.setStatus(LeasingStatus.CLOSED);
        lease.setEndDate(LocalDateTime.now());

        equipment.setLeasedBy(null);
        equipment.setStatus(null);
        equipmentRepository.save(equipment);

        createPayment(lease);

        log.info("Lease {} closed", leaseId);
        return equipmentLeaseRepository.save(lease);
    }

    private void createPayment(EquipmentLease lease) {
        PaymentCreationMessage payment = PaymentCreationMessage.builder()
                .userId(lease.getLesseeId())
                .amount(calculateTotalAmount(lease))
                .currency("EUR")
                .description("Payment for equipment lease " + lease.getId())
                .build();
        rabbitMQProducer.sendPaymentCreationMessage(payment);
    }

    /**
     * Calculates the total amount for the lease based on total hours in the lease period.
     *
     * @param lease
     * @return total payment amount for the lease
     */
    private BigDecimal calculateTotalAmount(EquipmentLease lease) {
        LocalDateTime startDate = lease.getStartDate();
        LocalDateTime endDate = lease.getEndDate();
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        long totalHours = java.time.Duration.between(startDate, endDate).toHours();
        long totalDays = totalHours / 24;
        BigDecimal totalAmount = lease.getDailyRate().multiply(BigDecimal.valueOf(totalDays));
        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

}
