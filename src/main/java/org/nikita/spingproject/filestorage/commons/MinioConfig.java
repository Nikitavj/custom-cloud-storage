package org.nikita.spingproject.filestorage.commons;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = MinioClient
                .builder()
                .endpoint("http://localhost:9000")
                .credentials("user", "rootroot")
                .build();

        boolean bucketExist = minioClient.bucketExists(BucketExistsArgs
                .builder()
                .bucket("user-files")
                .build());

        if (!bucketExist) {
            minioClient.makeBucket(MakeBucketArgs
                    .builder()
                    .bucket("user-files")
                    .build());
        }

        return minioClient;
    }
}
