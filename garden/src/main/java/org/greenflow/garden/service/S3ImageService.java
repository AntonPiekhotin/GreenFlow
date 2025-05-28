package org.greenflow.garden.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ImageService {

    private final S3Client s3Client;
    private final String bucketName = "greenflow-images";

    @Value("${api.spaces.uri}")
    private String SPACES_URI;

    public String uploadImage(MultipartFile file) {

        try (InputStream inputStream = file.getInputStream()) {
            String originalFileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            long contentLength = file.getSize();
            return uploadImage(inputStream, originalFileName, contentType, contentLength);
        } catch (IOException e) {
            log.error("Failed to upload image: {}", e.getMessage());
            throw new GreenFlowException(500, "Failed to upload image", e);
        }

    }

    public String uploadImage(InputStream inputStream, String originalFileName, String contentType,
                              long contentLength) {
        String fileName = "images/" + UUID.randomUUID() + "-" + originalFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .acl("public-read")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));

        return String.format("%s/%s/%s", SPACES_URI, bucketName, fileName);
    }

    public void deleteImage(@NotBlank String imageUrl) {
        String key = URI.create(imageUrl).getPath().substring(1);
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}
