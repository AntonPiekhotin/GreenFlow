package org.greenflow.equipment.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.event.BalanceChangeMessage;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.equipment.model.constant.EquipmentStatus;
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

/**
 * Service for managing equipment leasing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LeasingService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentLeaseRepository equipmentLeaseRepository;
    private final RabbitMQProducer rabbitMQProducer;

    /**
     * Requests a lease for equipment.
     *
     * @param equipmentId the ID of the equipment to lease
     * @param lesseeId the ID of the lessee
     * @return the created lease
     */
    public EquipmentLease requestLeaseEquipment(@NotBlank String equipmentId, @NotBlank String lesseeId) {
        log.debug("request lease equipment {} from worker {}", equipmentId, lesseeId);
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new GreenFlowException(400, "Equipment not found"));
        if (equipment.getStatus() != EquipmentStatus.AVAILABLE) {
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

        equipment.setStatus(EquipmentStatus.PENDING);
        equipment.setLeasedBy(lesseeId);
        equipmentRepository.save(equipment);
        log.info("Equipment {} is now pending for lease", equipmentId);
        return lease;
    }

    /**
     * Retrieves the list of leased equipment for a lessee.
     *
     * @param lesseeId the ID of the lessee
     * @return the list of leased equipment
     */
    public List<EquipmentLease> getLeasedEquipment(@NotBlank String lesseeId) {
        return equipmentLeaseRepository.findAllByLesseeId(lesseeId);
    }

    /**
     * Approves a lease.
     *
     * @param leaseId the ID of the lease to approve
     * @return the approved lease
     */
    public EquipmentLease approveLease(@NotNull Long leaseId) {
        EquipmentLease lease = equipmentLeaseRepository.findById(leaseId)
                .orElseThrow(() -> new GreenFlowException(400, "Lease not found"));
        if (lease.getStatus() != LeasingStatus.PENDING) {
            throw new GreenFlowException(400, "Lease is not pending");
        }
        Equipment equipment = equipmentRepository.findById(lease.getEquipmentId())
                .orElseThrow(() -> new GreenFlowException(400, "Equipment not found"));
        lease.setStatus(LeasingStatus.ACTIVE);
        lease.setStartDate(LocalDateTime.now());

        equipment.setStatus(EquipmentStatus.LEASED);
        equipmentRepository.save(equipment);
        log.info("Lease {} approved", leaseId);
        return equipmentLeaseRepository.save(lease);
    }

    /**
     * Closes a lease.
     *
     * @param leaseId the ID of the lease to close
     * @return the closed lease
     */
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
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(equipment);

        changeWorkerBalance(lease);

        log.info("Lease {} closed", leaseId);
        return equipmentLeaseRepository.save(lease);
    }

    private void changeWorkerBalance(EquipmentLease lease) {
        BalanceChangeMessage balanceChange = BalanceChangeMessage.builder()
                .userId(lease.getLesseeId())
                .amount(calculateTotalAmount(lease).negate())
                .description("For equipment lease " + lease.getId())
                .build();
        rabbitMQProducer.sendBalanceChangeMessage(balanceChange);
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
        double totalDays = (double) totalHours / 24;
        BigDecimal totalAmount = lease.getDailyRate().multiply(BigDecimal.valueOf(totalDays));
        BigDecimal res = totalAmount.setScale(2, RoundingMode.HALF_UP);
        if (res.compareTo(BigDecimal.ONE) < 0) {
            return BigDecimal.ONE;
        }
        return res;
    }

}
