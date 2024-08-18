package org.nikita.spingproject.filestorage.directory.dao;

import io.minio.Result;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.commons.InformationEntityS3;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.s3Api.DirectoryS3Api;
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
    private DirectoryS3Api directoryS3Api;

    @Autowired
    public DirectoryDaoImpl(DirectoryS3Api directoryS3Api) {
        this.directoryS3Api = directoryS3Api;
    }

    @Override
    public void add(Directory directory) {
        String pathForMeta = directory.getAbsolutePath() + "_meta";

        directoryS3Api.create(
                createMetaDataDir(directory.getName(), directory.getRelativePath()),
                pathForMeta);
    }

    @Override
    public Directory get(String absolutPath) {
        Iterable<Result<Item>> results = directoryS3Api.getObjects(absolutPath + "/");
        List<Item> items = getListItemsNoIsDir(results);

        return Directory.builder()
                .absolutePath(absolutPath)
                .directories(getDirFromItems(items))
                .files(getFilesFromItems(items))
                .build();
    }


    @Override
    @SneakyThrows
    public void remove(String absolutePath) {
        String pathForMeta = absolutePath + "_meta";
        directoryS3Api.deleteObject(pathForMeta);

        Iterable<Result<Item>> results = directoryS3Api.getObjectsRecursive(absolutePath + "/");
        List<DeleteObject> deleteObjects = new ArrayList<>();
        for (Result<Item> itemResult : results) {
            Item item = itemResult.get();
            deleteObjects.add(new DeleteObject(item.objectName()));
        }

        directoryS3Api.deleteObjects(deleteObjects);
    }

    @Override
    public void rename(String previousAbsolutePath, String newAbsolutePath, String previousRelPath, String newRelPath, String newName) {
        Iterable<Result<Item>> results = directoryS3Api.getObjectsRecursive(previousAbsolutePath + "/");
        List<Item> items = getListItemsNoIsDir(results);

        List<Directory> directories = getDirFromItems(items);
        List<File> files = getFilesFromItems(items);

        for (Directory dir : directories) {
            String absolutePathDir = dir.getAbsolutePath();
            String newAbsolutePathDir = absolutePathDir.replaceFirst(previousAbsolutePath, newAbsolutePath);
            String newRelPathDir = dir.getRelativePath().replaceFirst(previousRelPath, newRelPath);
            directoryS3Api.copyObject(absolutePathDir, newAbsolutePathDir, createMetaDataDir(dir.getName(), newRelPathDir));
        }

        for (File file: files) {
            String absolutePathFile = file.getAbsolutePath();
            String newAbsolutePathFile = absolutePathFile.replaceFirst(previousAbsolutePath, newAbsolutePath);
            String newRelPathFile = file.getRelativePath().replaceFirst(previousRelPath, newRelPath);
            directoryS3Api.copyObject(absolutePathFile, newAbsolutePathFile, createMetaDataFile(file.getName(), newRelPathFile));
        }

        directoryS3Api.copyObject(previousAbsolutePath + "_meta", newAbsolutePath + "_meta", createMetaDataDir(newName, newRelPath));
        this.remove(previousAbsolutePath);
    }

    @Override
    public Directory getAll(String absolutePath) {
        Iterable<Result<Item>> results = directoryS3Api.getObjectsRecursive(absolutePath + "/");
        List<Item> items = getListItemsNoIsDir(results);

        return Directory.builder()
                .absolutePath(absolutePath)
                .directories(getDirFromItems(items))
                .files(getFilesFromItems(items))
                .build();

    }


    private Map<String, String> createMetaDataDir(String name, String relPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("rel_path", relPath);
        metaData.put("dir", "");
        return metaData;
    }

    private Map<String, String> createMetaDataFile(String name, String relPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("rel_path", relPath);
        metaData.put("file", "");
        return metaData;
    }


    @SneakyThrows
    private List<Item> getListItemsNoIsDir(Iterable<Result<Item>> results) {
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
                .filter(item -> item.userMetadata().containsKey("X-Amz-Meta-Dir"))
                .map(this::itemMaptoDirectory)
                .collect(Collectors.toList());
    }

    private List<File> getFilesFromItems(List<Item> items) {
        return items.stream()
                .filter(item -> item.userMetadata().containsKey("X-Amz-Meta-File"))
                .map(this::itemMapToFile)
                .collect(Collectors.toList());
    }

    private File itemMapToFile(Item item) {
        InformationEntityS3 info = buildInfo(item.userMetadata());
        return File.builder()
                .name(info.getName())
                .relativePath(info.getRelativePath())
                .absolutePath(item.objectName())
                .build();
    }

    private Directory itemMaptoDirectory(Item item) {
        InformationEntityS3 info = buildInfo(item.userMetadata());
        return Directory.builder()
                .name(info.getName())
                .relativePath(info.getRelativePath())
                .absolutePath(item.objectName())
                .build();
    }

    private InformationEntityS3 buildInfo(Map<String, String> metaData) {
        return InformationEntityS3.builder()
                .name(metaData.get("X-Amz-Meta-Name"))
                .relativePath(metaData.get("X-Amz-Meta-Rel_path"))
                .isDir(metaData.containsKey("X-Amz-Meta-Dir"))
                .build();
    }
}
