package org.nikita.spingproject.filestorage.file.dao;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.nikita.spingproject.filestorage.file.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Repository
public class FileDaoImpl implements FileDao {
    private static String BUCKET_NAME = "user-files";
    @Autowired
    private MinioClient minioClient;

    @Override
    public void putFile(File file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(file.getPath())
                .stream(file.getInputStream(), file.getInputStream().available(), -1)
                .build());
    }

    @Override
    public void deleteFile(File file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(file.getPath())
                .build());
    }
}
