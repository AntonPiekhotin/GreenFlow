package org.greenflow.garden.input.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.greenflow.common.model.constant.CustomHeaders;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.garden.model.dto.DeleteImageRequest;
import org.greenflow.garden.model.dto.GardenDto;
import org.greenflow.garden.model.entity.Garden;
import org.greenflow.garden.service.GardenService;
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
import org.springframework.web.multipart.MultipartFile;

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
        return ResponseEntity.ok(gardens);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> createGarden(@RequestHeader(CustomHeaders.X_USER_ID) String userId,
                                          @RequestBody @Valid GardenDto gardenDto) {
        Garden garden = gardenService.createGarden(userId, gardenDto);
        return ResponseEntity.status(201).body(garden);
    }

    @DeleteMapping("/{gardenId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> deleteGarden(@RequestHeader(CustomHeaders.X_USER_ID) String userId,
                                          @PathVariable Long gardenId) {
        gardenService.deleteGarden(userId, gardenId);
        return ResponseEntity.status(204).body("Garden deleted successfully");
    }

    @PutMapping("/{gardenId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> updateGarden(@RequestHeader(CustomHeaders.X_USER_ID) String userId,
                                          @PathVariable Long gardenId,
                                          @RequestBody @Valid GardenDto gardenDto) {
        Garden garden = gardenService.updateGarden(userId, gardenId, gardenDto);
        return ResponseEntity.ok(garden);
    }

    @PostMapping("/images")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> uploadImage(@RequestHeader(CustomHeaders.X_USER_ID) String userId,
                                         @RequestParam("file") MultipartFile imageFile,
                                         @RequestParam("gardenId") Long gardenId) {
        List<String> allowedContentTypes = List.of("image/jpeg", "image/png", "image/gif", "image/webp");

        if (imageFile.isEmpty())
            throw new GreenFlowException(400, "Image file is empty.");
        if (!allowedContentTypes.contains(imageFile.getContentType()))
            throw new GreenFlowException(400, "Invalid image file type. Allowed types: " + allowedContentTypes);

        return ResponseEntity.ok(gardenService.addImageToGarden(userId, gardenId, imageFile));
    }

    @DeleteMapping("/images")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> deleteImage(@RequestHeader(CustomHeaders.X_USER_ID) String userId,
                                         @RequestBody @Valid DeleteImageRequest deleteImageRequest) {
        return ResponseEntity.ok(gardenService.deleteImageFromGarden(userId, deleteImageRequest));
    }

    @DeleteMapping("/images/all")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> deleteAllImagesInGarden(@RequestHeader(CustomHeaders.X_USER_ID) String userId,
                                                     @RequestParam("gardenId") @NotNull Long gardenId) {
        gardenService.deleteAllImagesInGarden(userId, gardenId);
        return ResponseEntity.status(204).body("All images deleted successfully");

    }

}
