package org.greenflow.garden.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.garden.model.dto.DeleteImageRequest;
import org.greenflow.garden.model.dto.GardenDto;
import org.greenflow.garden.model.entity.Garden;
import org.greenflow.garden.output.persistent.GardenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GardenService {

    private final GardenRepository gardenRepository;
    private final S3ImageService s3ImageService;

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

    public String addImageToGarden(String userId, @NotNull Long gardenId, @NotNull MultipartFile imageFile) {
        Garden garden = gardenRepository.findById(gardenId)
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), "Garden not found"));
        if (!garden.getOwnerId().equals(userId)) {
            throw new GreenFlowException(403, "You do not have access to this resource");
        }
        String imageUrl = s3ImageService.uploadImage(imageFile);
        garden.getImagesUrl().add(imageUrl);
        gardenRepository.save(garden);
        log.info("Client {} added image to garden {}: {}", userId, gardenId, imageUrl);
        return imageUrl;
    }

    public boolean deleteImageFromGarden(String userId, @Valid DeleteImageRequest deleteImageRequest) {
        Long gardenId = deleteImageRequest.gardenId();
        String imageUrl = deleteImageRequest.imageUrl();
        Garden garden = gardenRepository.findById(gardenId)
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), "Garden not found"));
        if (!garden.getOwnerId().equals(userId)) {
            throw new GreenFlowException(403, "You do not have access to this resource");
        }
        if (!garden.getImagesUrl().remove(imageUrl)) {
            throw new GreenFlowException(400, "Image not found in garden");
        }
        gardenRepository.save(garden);
        s3ImageService.deleteImage(imageUrl);
        log.info("Client {} deleted image from garden {}: {}", userId, gardenId, imageUrl);
        return true;
    }
}
