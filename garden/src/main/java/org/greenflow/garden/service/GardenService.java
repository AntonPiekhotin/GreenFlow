package org.greenflow.garden.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.garden.model.entity.Garden;
import org.greenflow.garden.output.persistent.GardenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GardenService {

    private final GardenRepository gardenRepository;

    public List<Garden> getGardensByOwnerId(String ownerId) {
        return gardenRepository.findAllByOwnerId(ownerId);
    }
}
