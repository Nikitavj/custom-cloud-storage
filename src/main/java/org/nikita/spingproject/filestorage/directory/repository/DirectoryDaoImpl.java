package org.nikita.spingproject.filestorage.directory.repository;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.commons.InformationEntityS3;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.s3api.DirectoryS3Api;
import org.nikita.spingproject.filestorage.file.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class DirectoryDaoImpl implements DirectoryDao {
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
    public Directory get(String absolutPath) {
        StatObjectResponse response = directoryS3Api.getInfoDirectory(absolutPath);
        InformationEntityS3 info = buildInfo(response.userMetadata());

        Iterable<Result<Item>> results = directoryS3Api.getObjectsDirectory(absolutPath);
        List<Item> items = getListItems(results);

        return Directory.builder()
                .absolutePath(absolutPath)
                .relativePath(info.getRelativePath())
                .directories(getDirFromItems(items))
                .files(getFilesFromItems(items))
                .build();
    }

    private Map<String, String> createMetaData(String name, String relPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("rel_path", relPath);
        metaData.put("dir", "");
        return metaData;
    }

    private InformationEntityS3 buildInfo(Map<String, String> metaData) {
        return InformationEntityS3.builder()
                .name(metaData.get("X-Amz-Meta-Name"))
                .relativePath(metaData.get("X-Amz-Meta-rel_path"))
                .isDir(metaData.get("X-Amz-Meta-dir").equals("dir"))
                .build();
    }

    @SneakyThrows
    private List<Item> getListItems(Iterable<Result<Item>> results) {
        List<Item> items = new ArrayList<>();
        for (Result<Item> itemResult : results) {
            Item item = itemResult.get();
            if (!item.isDir()) {
                items.add(item);
            }
        }
        return items;
    }

    private List<Directory> getDirFromItems(List<Item> items) {
        return items.stream()
                .filter(item -> item.userMetadata().containsKey("dir"))
                .map(this::itemMaptoDirectory)
                .collect(Collectors.toList());
    }

    private List<File> getFilesFromItems(List<Item> items) {
        return items.stream()
                .filter(item -> item.userMetadata().containsKey("file"))
                .map(this::itemMapToFile)
                .collect(Collectors.toList());
    }

    private File itemMapToFile(Item item) {
        InformationEntityS3 info = buildInfo(item.userMetadata());
        return File.builder()
                .name(info.getName())
                .relativePath(info.getRelativePath())
                .build();
    }

    private Directory itemMaptoDirectory(Item item) {
        InformationEntityS3 info = buildInfo(item.userMetadata());
        return Directory.builder()
                .name(info.getName())
                .relativePath(info.getRelativePath())
                .build();
    }
}
