package org.nikita.spingproject.filestorage.directory.dao;

import io.minio.Result;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DirectoryDao {

    void createFolder(Map<String, String> metaData, String path);

    Iterable<Result<Item>> getObjectsDirectory(String path);

    Iterable<Result<Item>> getObjectsDirectoryRecursive(String path);

    void deleteObjects(List<DeleteObject> objects);

    void deleteObject(String path);
}
