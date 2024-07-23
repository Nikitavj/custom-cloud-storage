package org.nikita.spingproject.filestorage.dao;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Service
public class FileDao {
    private static final String BUCKET_NAME = "user-files";
    @Autowired
    private MinioClient minioClient;

    public void putObject(InputStream stream, long size, String nameFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(PutObjectArgs
                .builder()
                .stream(stream, size, -1)
                .bucket(BUCKET_NAME)
                .object(nameFile)
                .build());
    }

    public InputStream getObject(String pathFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(GetObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(pathFile)
                .build());
    }

    public Iterable<Result<Item>> getObjectsOfPath(String pathDirectory) {
        return minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(BUCKET_NAME)
                .recursive(false)
                .prefix(pathDirectory)
                .build());
    }
}
