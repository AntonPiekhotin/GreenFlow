package org.greenflow.garden.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.garden.model.dto.GardenDto;
import org.greenflow.garden.model.entity.Garden;
import org.greenflow.garden.output.persistent.GardenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GardenService {

    private final GardenRepository gardenRepository;
    private final S3Uploader s3Uploader;

    public List<Garden> getGardensByOwnerId(String ownerId) {
        return gardenRepository.findAllByOwnerId(ownerId);
    }

    public Garden createGarden(String ownerId, @Valid GardenDto gardenDto) {
        if (gardenDto == null) {
            throw new GreenFlowException(HttpStatus.BAD_REQUEST.value(), "Garden creation DTO cannot be null");
        } else if (ownerId == null || ownerId.isBlank()) {
            throw new GreenFlowException(HttpStatus.BAD_REQUEST.value(), "Owner ID cannot be null or empty");
        }
        Garden garden = Garden.fromDto(gardenDto);
        garden.setOwnerId(ownerId);
        garden = gardenRepository.save(garden);
        log.info("Client {} created garden: {}", garden.getOwnerId(), garden.getId());
        return garden;
    }

    public void deleteGarden(String userId, Long gardenId) {
        if (userId == null || userId.isBlank()) {
            throw new GreenFlowException(HttpStatus.BAD_REQUEST.value(), "User ID cannot be null or empty");
        }
        if (gardenId == null) {
            throw new GreenFlowException(HttpStatus.BAD_REQUEST.value(), "Garden ID cannot be null");
        }
        Garden garden = gardenRepository.findById(gardenId)
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), "Garden not found"));
        if (!garden.getOwnerId().equals(userId)) {
            throw new GreenFlowException(HttpStatus.FORBIDDEN.value(), "You do not have access to this resource");
        }
        gardenRepository.delete(garden);
        log.info("Client {} deleted garden: {}", userId, garden.getId());
    }

    public Garden updateGarden(String userId, Long gardenId, @Valid GardenDto gardenDto) {
        if (userId == null || userId.isBlank()) {
            throw new GreenFlowException(HttpStatus.BAD_REQUEST.value(), "User ID cannot be null or empty");
        }
        if (gardenId == null) {
            throw new GreenFlowException(HttpStatus.BAD_REQUEST.value(), "Garden ID cannot be null");
        }
        if (gardenDto == null) {
            throw new GreenFlowException(HttpStatus.BAD_REQUEST.value(), "Garden update DTO cannot be null");
        }
        Garden garden = gardenRepository.findById(gardenId)
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), "Garden not found"));
        if (!garden.getOwnerId().equals(userId)) {
            throw new GreenFlowException(HttpStatus.FORBIDDEN.value(), "You do not have access to this resource");
        }
        garden.setName(gardenDto.getName());
        garden.setAddress(gardenDto.getAddress());
        garden.setLatitude(gardenDto.getLatitude());
        garden.setLongitude(gardenDto.getLongitude());
        garden.setDescription(gardenDto.getDescription());
        return gardenRepository.save(garden);
    }

    public String addImageToGarden(String userId, String gardenId, MultipartFile imageFile) {
        Garden garden = gardenRepository.findById(Long.valueOf(gardenId))
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), "Garden not found"));
        if (!garden.getOwnerId().equals(userId)) {
            throw new GreenFlowException(403, "You do not have access to this resource");
        }
        if (imageFile == null || imageFile.isEmpty()) {
            throw new GreenFlowException(HttpStatus.BAD_REQUEST.value(), "Image file cannot be null or empty");
        }
        String imageUrl = s3Uploader.uploadImage(imageFile);
        garden.getImagesUrl().add(imageUrl);
        gardenRepository.save(garden);
        log.info("Client {} added image to garden {}: {}", userId, gardenId, imageUrl);
        return imageUrl;
    }
}
