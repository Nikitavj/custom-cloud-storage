package org.nikita.spingproject.filestorage.directory.repository;

import io.minio.Result;
import io.minio.messages.Item;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.s3api.DirectoryS3Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DirectoryRepositoryImpl implements DirectoryRepository{
    @Autowired
    private DirectoryS3Api directoryS3Api;

    @Override
    public void add(Directory directory) {
        String pathForMeta = directory.getAbsolutePath() + "_meta";

        directoryS3Api.createDirectory(
                createMetaData(directory.getName(), directory.getRelativePath()),
                pathForMeta);
    }

    @Override
    public void remove(Directory directory) {
        String pathForMeta = directory.getAbsolutePath() + "_meta";
        directoryS3Api.deleteObject(pathForMeta);

        Iterable<Result<Item>> results = directoryS3Api.getObjectsDirectoryRecursive(directory.getAbsolutePath());





    }

    @Override
    public Directory get(String path) {
        return null;
    }

    private Map<String, String> createMetaData(String name, String relPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("rel_path", relPath);
        metaData.put("type", "dir");
        return metaData;
    }
}
