package org.greenflow.equipment.input.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/equipment")
@PreAuthorize("hasAuthority('MANAGER')")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<?> createEquipment(@RequestBody @Valid Equipment equipment,
                                             @RequestParam("warehouseId") String warehouseId) {
        return ResponseEntity.status(201).body(equipmentService.createEquipment(equipment, warehouseId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WORKER', 'MANAGER')")
    public ResponseEntity<?> getEquipment(@PathVariable String id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('WORKER', 'MANAGER')")
    public ResponseEntity<?> getEquipmentByWarehouseId(@RequestParam("warehouseId") String warehouseId) {
        return ResponseEntity.ok(equipmentService.getEquipmentByWarehouseId(warehouseId));
    }
    
    @PutMapping("/{equipmentId}")
    public ResponseEntity<?> updateEquipment(@PathVariable String equipmentId, @RequestBody Equipment equipment) {
        return ResponseEntity.ok(equipmentService.updateEquipment(equipmentId, equipment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquipment(@PathVariable String id) {
        equipmentService.deleteEquipmentById(id);
        return ResponseEntity.noContent().build();
    }
}
