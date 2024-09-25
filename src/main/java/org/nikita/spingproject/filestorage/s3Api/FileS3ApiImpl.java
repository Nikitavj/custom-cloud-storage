package org.nikita.spingproject.filestorage.s3Api;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.nikita.spingproject.filestorage.file.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Repository
public class FileS3ApiImpl extends S3ApiImpl<File> implements FileS3Api {
    private static final String BUCKET_NAME = "user-files";
    private final MinioClient minioClient;

    @Autowired
    public FileS3ApiImpl(MinioClient minioClient) {
        super(minioClient);
        this.minioClient = minioClient;
    }

    @Override
    public void putFile(Map<String, String> metaData, String path, InputStream is) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .stream(is, is.available(), -1)
                .userMetadata(metaData)
                .build());
    }

    @Override
    public InputStream getInputStream(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(GetObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }

    @Override
    public void copyFile(String path, String newPath, Map<String, String> metaData) throws IllegalArgumentException, ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
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
    public Iterable<Result<Item>> getObjectsRecursive(String path) {
        return minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(BUCKET_NAME)
                .prefix(path)
                .recursive(true)
                .includeUserMetadata(true)
                .build());
    }
}
