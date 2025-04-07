package org.greenflow.garden.input.web;

import lombok.RequiredArgsConstructor;
import org.greenflow.common.model.constant.CustomHeaders;
import org.greenflow.garden.model.entity.Garden;
import org.greenflow.garden.service.GardenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/garden")
@RequiredArgsConstructor
public class GardenController {

    private final GardenService gardenService;

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> getMyGardens(@RequestHeader(CustomHeaders.X_USER_ID) String userId) {
        List<Garden> gardens = gardenService.getGardensByOwnerId(userId);
        if (gardens.isEmpty()) {
            return ResponseEntity.status(204).body(Collections.emptyList());
        }
        return ResponseEntity.ok(gardens);
    }
}
