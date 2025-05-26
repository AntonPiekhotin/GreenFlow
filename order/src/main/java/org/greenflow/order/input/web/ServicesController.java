package org.greenflow.order.input.web;

import lombok.RequiredArgsConstructor;
import org.greenflow.order.model.dto.ServiceDto;
import org.greenflow.order.output.persistent.ServiceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServicesController {

    private final ServiceRepository serviceRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<List<ServiceDto>> listServices() {
        List<ServiceDto> services = serviceRepository.findAll().stream()
                .map(service -> ServiceDto.builder()
                        .id(service.getId())
                        .name(service.getName())
                        .unit(service.getUnit())
                        .rate(service.getPricePerUnit())
                        .build())
                .toList();
        return ResponseEntity.ok(services);
    }
}
