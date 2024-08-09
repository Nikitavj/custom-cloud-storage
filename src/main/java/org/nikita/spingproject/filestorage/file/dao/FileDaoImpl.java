package org.nikita.spingproject.filestorage.file.dao;
import io.minio.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.io.InputStream;
import java.util.Map;

@Repository
public class FileDaoImpl implements FileDao {
    private static String BUCKET_NAME = "user-files";
    @Autowired
    private MinioClient minioClient;

    @Override
    @SneakyThrows
    public void putFile(Map<String, String> metaData, String path, InputStream is) {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .stream(is, is.available(), -1)
                .userMetadata(metaData)
                .build());
    }

    @Override
    @SneakyThrows
    public void deleteFile(String path) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }

    @Override
    @SneakyThrows
    public InputStream downloadFile(String path) {
        return minioClient.getObject(GetObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }

    @Override
    @SneakyThrows
    public void copyFile(String path, String newPath, Map<String, String> metaData) {
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

    @Override
    @SneakyThrows
    public StatObjectResponse getStatFile(String path) {
        return minioClient.statObject(StatObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }
}
