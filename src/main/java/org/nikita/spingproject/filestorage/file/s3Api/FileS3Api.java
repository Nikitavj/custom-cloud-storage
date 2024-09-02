package org.nikita.spingproject.filestorage.file.s3Api;

import io.minio.StatObjectResponse;
import io.minio.errors.*;
import org.nikita.spingproject.filestorage.commons.S3Api;
import org.nikita.spingproject.filestorage.file.File;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface FileS3Api extends S3Api<File> {
    void putFile(Map<String, String> metaData, String path, InputStream is) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    InputStream getInputStream(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    void copyFile(String path, String newPath, Map<String, String> metaData) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
