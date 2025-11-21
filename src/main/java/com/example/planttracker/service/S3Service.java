package com.example.planttracker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String region;

    public S3Service(
            @Value("${AWS_REGION}") String region,
            @Value("${AWS_ACCESS_KEY_ID}") String accessKey,
            @Value("${AWS_SECRET_ACCESS_KEY}") String secretKey) {
        this.region = region;

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    public String uploadFile(String bucket, String key, MultipartFile file) {

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // IMPORTANT: Stream the upload to avoid corrupted images
            s3Client.putObject(
                    putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return "https://" + bucket + ".s3." +
                    this.region +
                    ".amazonaws.com/" + key;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public void deleteFile(String bucket, String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}
