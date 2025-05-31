package org.greenflow.equipment.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.greenflow.equipment.model.dto.WarehouseCreationDto;
import org.greenflow.equipment.model.dto.WarehouseDto;
import org.greenflow.equipment.output.persistent.WarehouseRepository;
import org.greenflow.equipment.service.mapper.WarehouseMapper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseService warehouseService;

    public WarehouseServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateWarehouse() {
        WarehouseCreationDto creationDto = new WarehouseCreationDto();
        // Set fields for creationDto

        when(warehouseRepository.save(any())).thenReturn(WarehouseMapper.INSTANCE.toEntity(creationDto));

        WarehouseDto result = warehouseService.createWarehouse(creationDto);

        assertNotNull(result);
        verify(warehouseRepository, times(1)).save(any());
    }

}
