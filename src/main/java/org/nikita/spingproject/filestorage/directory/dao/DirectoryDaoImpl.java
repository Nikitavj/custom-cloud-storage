package org.nikita.spingproject.filestorage.directory.dao;

import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

@Repository
public class DirectoryDaoImpl implements DirectoryDao {
    private static String BUCKET_NAME = "user-files";

    @Autowired
    private MinioClient minioClient;

    @SneakyThrows
    @Override
    public void createFolder(Map<String, String> metaData, String path) {
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                .userMetadata(metaData)
                .build());
    }

    @Override
    public Iterable<Result<Item>> getObjectsDirectory(String path) {
        return minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(BUCKET_NAME)
                .recursive(false)
                .prefix(path)
                .includeUserMetadata(true)
                .build());
    }

    @Override
    public Iterable<Result<Item>> getObjectsDirectoryRecursive(String path) {
        return minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(BUCKET_NAME)
                .prefix(path)
                .recursive(true)
                .build());
    }

    @Override
    @SneakyThrows
    public void deleteObject(String path) {
        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }

    @SneakyThrows
    @Override
    public void deleteObjects(List<DeleteObject> objects) {
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
}
