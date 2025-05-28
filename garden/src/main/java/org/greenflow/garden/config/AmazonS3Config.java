package org.greenflow.garden.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class AmazonS3Config {

    @Value("${api.spaces.access-key}")
    private String SPACES_ACCESS_KEY;

    @Value("${api.spaces.secret-key}")
    private String SPACES_SECRET_KEY;

    @Value("${api.spaces.uri}")
    private String SPACES_URI;

    @Value("${api.spaces.region}")
    private String SPACES_REGION;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(SPACES_ACCESS_KEY, SPACES_SECRET_KEY);

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(SPACES_URI))
                .region(Region.of(SPACES_REGION))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}
