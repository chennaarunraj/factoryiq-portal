package com.portal.document.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    // ── INIT BUCKET ───────────────────────────
    public void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
                );
                log.info("Created MinIO bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Error checking/creating bucket: {}", e.getMessage());
        }
    }

    // ── UPLOAD FILE ───────────────────────────
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        ensureBucketExists();
        String key = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
        );
        log.info("Uploaded file to MinIO: {}", key);
        return key;
    }

    // ── DOWNLOAD FILE ─────────────────────────
    public InputStream downloadFile(String key) throws Exception {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .build()
        );
    }

    // ── GET PRESIGNED URL ─────────────────────
    public String getPresignedUrl(String key) throws Exception {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(key)
                .method(Method.GET)
                .expiry(1, TimeUnit.HOURS)
                .build()
        );
    }

    // ── DELETE FILE ───────────────────────────
    public void deleteFile(String key) throws Exception {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .build()
        );
        log.info("Deleted file from MinIO: {}", key);
    }

    public String getBucketName() {
        return bucketName;
    }
}