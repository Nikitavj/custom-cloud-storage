package org.nikita.spingproject.filestorage.directory.s3api;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DirectoryS3Api {

    StatObjectResponse getInfoDirectory(String path);

    void createDirectory(Map<String, String> metaData, String path);

    Iterable<Result<Item>> getObjectsDirectory(String path);

    Iterable<Result<Item>> getObjectsDirectoryRecursive(String path);

    void deleteObjects(List<DeleteObject> objects);

    void deleteObject(String path);
}
