package org.greenflow.equipment.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
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

    public Equipment leaseEquipment(@NotBlank String equipmentId, @NotBlank String lesseeId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new GreenFlowException(400, "Equipment not found"));
        if (equipment.isLeased()) {
            throw new GreenFlowException(400, "Equipment is already leased");
        }
        EquipmentLease lease = EquipmentLease.builder()
                .equipmentId(equipmentId)
                .lesseeId(lesseeId)
                .dailyRate(equipment.getDailyLeasingPrice())
                .startDate(LocalDateTime.now())
                .status(EquipmentLease.LeasingStatus.ACTIVE)
                .build();
        equipmentLeaseRepository.save(lease);

        equipment.setLeased(true);
        equipment.setLeasedBy(lesseeId);
        equipmentRepository.save(equipment);
        return equipment;
    }
    
    public List<EquipmentLease> getLeasedEquipment(@NotBlank String lesseeId) {
        return equipmentLeaseRepository.findAllByLesseeId(lesseeId);
    }
}
