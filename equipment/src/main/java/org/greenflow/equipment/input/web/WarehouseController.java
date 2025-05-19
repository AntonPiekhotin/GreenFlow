package org.greenflow.equipment.input.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.greenflow.equipment.model.dto.WarehouseCreationDto;
import org.greenflow.equipment.model.dto.WarehouseUpdateDto;
import org.greenflow.equipment.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/warehouse")
@PreAuthorize("hasAuthority('MANAGER')")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<?> createWarehouse(@RequestBody @Valid WarehouseCreationDto warehouse) {
        return ResponseEntity.status(201).body(warehouseService.createWarehouse(warehouse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouse(@PathVariable String id) {
        return ResponseEntity.ok(warehouseService.getWarehouse(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWarehouse(@PathVariable String id, @RequestBody @Valid WarehouseUpdateDto warehouse) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable String id) {
        warehouseService.deleteWarehouseById(id);
        return ResponseEntity.noContent().build();
    }
}
