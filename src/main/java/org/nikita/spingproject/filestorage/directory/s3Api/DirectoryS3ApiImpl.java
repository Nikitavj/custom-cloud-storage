package org.nikita.spingproject.filestorage.directory.s3Api;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@Repository
public class DirectoryS3ApiImpl implements DirectoryS3Api {
    private static String BUCKET_NAME = "user-files";
    private MinioClient minioClient;

    @Autowired
    public DirectoryS3ApiImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }


    @Override
    public StatObjectResponse getInfo(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.statObject(StatObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }

    @Override
    public void create(Map<String, String> metaData, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(BUCKET_NAME)
                    .object(path)
                    .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                    .userMetadata(metaData)
                    .build());
    }

    @Override
    public Iterable<Result<Item>> getObjects(String path) {
        return minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(BUCKET_NAME)
                .recursive(false)
                .prefix(path)
                .includeUserMetadata(true)
                .build());
    }

    @Override
    public Iterable<Result<Item>> getObjectsRecursive(String path) {
        return minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(BUCKET_NAME)
                .prefix(path)
                .recursive(true)
                .includeUserMetadata(true)
                .build());
    }

    @Override
    public void deleteObject(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }

    @Override
    public void deleteObjects(List<DeleteObject> objects) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs
                .builder()
                .bucket(BUCKET_NAME)
                .objects(objects)
                .build());

        for (Result<DeleteError> result : results) {
            DeleteError error = result.get();
            System.out.println(
                    "Error in deleting object " + error.objectName() + "; " + error.message());
        }
    }

    @Override
    public void copyObject(String path, String newPath, Map<String, String> metaData) throws IllegalArgumentException, ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.copyObject(CopyObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(newPath)
                .source(CopySource.builder()
                        .bucket(BUCKET_NAME)
                        .object(path)
                        .build())
                .metadataDirective(Directive.REPLACE)
                .userMetadata(metaData)
                .build());
    }
}
