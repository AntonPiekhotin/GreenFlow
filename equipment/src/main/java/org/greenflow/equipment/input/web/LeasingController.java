package org.greenflow.equipment.input.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.greenflow.common.model.constant.CustomHeaders;
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
public class LeasingController {

    private final LeasingService leasingService;

    @PostMapping("/{equipmentId}")
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<?> requestLeaseEquipment(@PathVariable @NotBlank String equipmentId,
                                            @RequestHeader(CustomHeaders.X_USER_ID) String userId) {
        return ResponseEntity.ok(leasingService.requestLeaseEquipment(equipmentId, userId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<?> getLeasedEquipment(@RequestHeader(CustomHeaders.X_USER_ID) String userId) {
        List<EquipmentLease> equipment = leasingService.getLeasedEquipment(userId);
        if (equipment.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(equipment);
    }

    @PostMapping("/approve/{leaseId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> approveLease(@PathVariable @NotNull Long leaseId) {
        return ResponseEntity.ok(leasingService.approveLease(leaseId));
    }

    @PostMapping("/close/{leaseId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> closeLease(@PathVariable @NotNull Long leaseId) {
        return ResponseEntity.ok(leasingService.closeLease(leaseId));
    }

}
