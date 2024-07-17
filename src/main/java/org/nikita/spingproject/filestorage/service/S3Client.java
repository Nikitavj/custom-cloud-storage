package org.nikita.spingproject.filestorage.service;

import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class S3Client {
    private MinioClient minioClient;

    public S3Client() {
        minioClient = MinioClient
                .builder()
                .endpoint("http://localhost:9000")
                .credentials("user", "rootroot")
                .build();
    }
}
