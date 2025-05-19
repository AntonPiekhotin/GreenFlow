package org.greenflow.equipment.input.web;

import jakarta.validation.constraints.NotBlank;
import org.greenflow.common.model.constant.CustomHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leasing")
@PreAuthorize("hasAuthority('WORKER')")
public class LeasingController {

    @PostMapping("/{equipmentId}")
    public ResponseEntity<?> leaseEquipment(@PathVariable @NotBlank String equipmentId,
                                            @RequestHeader(CustomHeaders.X_USER_ID) String lesseeId) {

    }

}
