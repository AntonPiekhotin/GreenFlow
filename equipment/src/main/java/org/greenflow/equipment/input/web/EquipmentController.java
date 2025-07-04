package org.greenflow.equipment.input.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.greenflow.common.model.constant.CustomHeaders;
import org.greenflow.equipment.model.constant.EquipmentSortBy;
import org.greenflow.equipment.model.entity.Equipment;
import org.greenflow.equipment.service.EquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> createEquipment(@RequestBody @Valid Equipment equipment,
                                             @RequestParam("warehouseId") Long warehouseId) {
        return ResponseEntity.status(201).body(equipmentService.createEquipment(equipment, warehouseId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WORKER', 'MANAGER')")
    public ResponseEntity<?> getEquipment(@PathVariable String id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('WORKER', 'MANAGER')")
    public ResponseEntity<?> getEquipmentByWarehouseId(@RequestParam("warehouseId") Long warehouseId) {
        return ResponseEntity.ok(equipmentService.getEquipmentByWarehouseId(warehouseId));
    }
    
    @PutMapping("/{equipmentId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> updateEquipment(@PathVariable String equipmentId, @RequestBody Equipment equipment) {
        return ResponseEntity.ok(equipmentService.updateEquipment(equipmentId, equipment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> deleteEquipment(@PathVariable String id) {
        equipmentService.deleteEquipmentById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<List<Equipment>> findNearby(
            @RequestHeader("X-User-Latitude") Double lat,
            @RequestHeader("X-User-Longitude") Double lon,
            @RequestParam("radiusKm") Double radiusKm,
            @RequestParam("sortBy") EquipmentSortBy sortBy,
            @RequestParam("sortDirection") EquipmentSortBy.SortDirection sortDirection
    ) {
        var equipment = equipmentService.findAvailableNear(lat, lon, radiusKm, sortBy, sortDirection);
        return ResponseEntity.ok(equipment);
    }

}
