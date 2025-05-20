package org.greenflow.equipment.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.equipment.model.constant.LeasingStatus;
import org.greenflow.equipment.model.entity.Equipment;
import org.greenflow.equipment.model.entity.EquipmentLease;
import org.greenflow.equipment.output.persistent.EquipmentLeaseRepository;
import org.greenflow.equipment.output.persistent.EquipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeasingService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentLeaseRepository equipmentLeaseRepository;

    public EquipmentLease requestLeaseEquipment(@NotBlank String equipmentId, @NotBlank String lesseeId) {
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

        equipment.setStatus(LeasingStatus.PENDING);
        equipment.setLeasedBy(lesseeId);
        equipmentRepository.save(equipment);
        return lease;
    }
    
    public List<EquipmentLease> getLeasedEquipment(@NotBlank String lesseeId) {
        return equipmentLeaseRepository.findAllByLesseeId(lesseeId);
    }

    public EquipmentLease approveLease(@NotNull Long leaseId) {
        EquipmentLease lease = equipmentLeaseRepository.findById(leaseId)
                .orElseThrow(() -> new GreenFlowException(400, "Lease not found"));
        lease.setStatus(LeasingStatus.ACTIVE);
        lease.setStartDate(LocalDateTime.now());
        return equipmentLeaseRepository.save(lease);
    }

}
