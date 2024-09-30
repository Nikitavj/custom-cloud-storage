package org.nikita.spingproject.filestorage.s3Api;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class MinioConfig {
    private static final String ROOT_BUCKET_NAME = "user-files";
    @Value("${minio.username}")
    private String userName;
    @Value("${minio.password}")
    private String password;
    @Value("${minio.url}")
    private String url;

    @Bean
    public MinioClient minioClient() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = MinioClient
                .builder()
                .endpoint(url)
                .credentials(userName, password)
                .build();

        boolean bucketExist = minioClient.bucketExists(BucketExistsArgs
                .builder()
                .bucket(ROOT_BUCKET_NAME)
                .build());

        if (!bucketExist) {
            minioClient.makeBucket(MakeBucketArgs
                    .builder()
                    .bucket(ROOT_BUCKET_NAME)
                    .build());
        }

        return minioClient;
    }
}
