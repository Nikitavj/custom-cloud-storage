package org.nikita.spingproject.filestorage.s3Api;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public abstract class S3ApiImpl<T> implements S3Api<T> {
    private static final String BUCKET_NAME = "user-files";
    private final MinioClient minioClient;

    public S3ApiImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void removeObject(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }

    @Override
    public StatObjectResponse getInfo(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.statObject(StatObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }
}
