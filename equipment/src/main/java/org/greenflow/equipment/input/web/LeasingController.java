package org.greenflow.equipment.input.web;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.greenflow.common.model.constant.CustomHeaders;
import org.greenflow.equipment.model.entity.Equipment;
import org.greenflow.equipment.model.entity.EquipmentLease;
import org.greenflow.equipment.service.LeasingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leasing")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('WORKER')")
public class LeasingController {

    private final LeasingService leasingService;

    @PostMapping("/{equipmentId}")
    public ResponseEntity<?> leaseEquipment(@PathVariable @NotBlank String equipmentId,
                                            @RequestHeader(CustomHeaders.X_USER_ID) String userId) {
        Equipment equipment = leasingService.leaseEquipment(equipmentId, userId);
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getLeasedEquipment(@RequestHeader(CustomHeaders.X_USER_ID) String userId) {
        List<EquipmentLease> equipment = leasingService.getLeasedEquipment(userId);
        if (equipment.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(equipment);
    }

}
