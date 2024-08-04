package org.nikita.spingproject.filestorage.directory.dao;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.directory.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Repository
public class DirectoryDaoImpl implements DirectoryDao {
    private static String BUCKET_NAME = "user-files";

    @Autowired
    private MinioClient minioClient;

    @SneakyThrows
    @Override
    public void createFolder(Map<String, String> metaData, String path) {
        ObjectWriteResponse owr = minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                .userMetadata(metaData)
                .build());

        System.out.println();
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
    public void deleteFolder(Folder folder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(folder.getLink())
                .build());
    }
}
