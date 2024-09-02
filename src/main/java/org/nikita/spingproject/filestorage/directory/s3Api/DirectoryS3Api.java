package org.nikita.spingproject.filestorage.directory.s3Api;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.nikita.spingproject.filestorage.commons.S3Api;
import org.nikita.spingproject.filestorage.directory.Directory;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface DirectoryS3Api extends S3Api<Directory> {
    void create(Map<String, String> metaData, String path) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException;
    Iterable<Result<Item>> getObjects(String path);
    Iterable<Result<Item>> getObjectsRecursive(String path);
    void deleteObjects(List<DeleteObject> objects) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    void copyObject(String path, String newPath, Map<String, String> metaData) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
