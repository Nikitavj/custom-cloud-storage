package org.nikita.spingproject.filestorage.directory.dao;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.nikita.spingproject.filestorage.directory.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Repository
public class DirectoryDaoImpl implements DirectoryDao {
    private static String BUCKET_NAME = "user-files";

    @Autowired
    private MinioClient minioClient;

    @Override
    public void createFolder(Folder folder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(folder.getPath())
                .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                .build());
    }

    @Override
    public Iterable<Result<Item>> getObjectsDirectory(Folder folder) {
        return minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(BUCKET_NAME)
                .recursive(false)
                .prefix(folder.getPath())
                .build());
    }

    @Override
    public void deleteFolder(Folder folder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(folder.getPath())
                .build());
    }
}
