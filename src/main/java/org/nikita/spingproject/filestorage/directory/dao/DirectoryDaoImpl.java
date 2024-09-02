package org.nikita.spingproject.filestorage.directory.dao;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.nikita.spingproject.filestorage.commons.InfoOfObjectS3;
import org.nikita.spingproject.filestorage.commons.S3ApiImpl;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.exception.*;
import org.nikita.spingproject.filestorage.directory.s3Api.DirectoryS3Api;
import org.nikita.spingproject.filestorage.file.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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

        try {
            directoryS3Api.create(
                    createMetaDataDir(
                            directory.getName(),
                            directory.getRelativePath(),
                            directory.getAbsolutePath()
                    ),
                    pathForMeta);

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.warn("Directory {} dont create", directory.getAbsolutePath());
            throw new DirectoryCreatedException("Directory not created");
        }
    }

    @Override
    public Directory get(String absolutePath) {
        Directory directory = new Directory();
        ;
        try {
            if (absolutePath.contains("/")) {
                StatObjectResponse info = directoryS3Api.getInfo(absolutePath + "_meta");
                InfoOfObjectS3 stat = buildStatObject(info.userMetadata());

                directory.setName(stat.getName());
                directory.setRelativePath(stat.getRelativePath());
                directory.setAbsolutePath(absolutePath);
            } else {
                directory.setAbsolutePath(absolutePath);
            }

            fillDirectory(directory);
            return directory;

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont get", absolutePath);
            throw new GetDirectoryObjectsExcepton("Unable to get directory objects");
        }
    }

    private void fillDirectory(Directory dir) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> results = directoryS3Api.getObjects(dir.getAbsolutePath() + "/");
        List<Item> items = getListItemsNoIsMinioDir(results);

        List<Directory> directories = getDirFromItems(items);
        List<File> files = getFilesFromItems(items);

        for (Directory directory : directories) {
            dir.putDirectory(directory);
            fillDirectory(directory);
        }
        for (File file : files) {
            dir.putFile(file);
        }
    }


    @Override
    public void remove(String absolutePath) {
        String pathForMeta = absolutePath + "_meta";
        try {
            directoryS3Api.removeObject(pathForMeta);

            Iterable<Result<Item>> results = directoryS3Api.getObjectsRecursive(absolutePath + "/");
            List<DeleteObject> deleteObjects = new ArrayList<>();
            for (Result<Item> itemResult : results) {
                Item item = itemResult.get();
                deleteObjects.add(new DeleteObject(item.objectName()));
            }
            directoryS3Api.deleteObjects(deleteObjects);

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont remove", absolutePath);
            throw new DirectoryRemoveException("Directory not remove");
        }
    }

    @Override
    public void rename(String previousAbsolutePath, String newAbsolutePath, String previousRelPath, String newRelPath, String newName) {
        Iterable<Result<Item>> results = directoryS3Api.getObjectsRecursive(previousAbsolutePath + "/");
        try {
            List<Item> items = getListItemsNoIsMinioDir(results);

            List<Directory> directories = getDirFromItems(items);
            List<File> files = getFilesFromItems(items);

            for (Directory dir : directories) {
                String absolutePathDir = dir.getAbsolutePath();
                String newAbsolutePathDir = absolutePathDir.replaceFirst(previousAbsolutePath, newAbsolutePath);
                String newRelPathDir = dir.getRelativePath().replaceFirst(previousRelPath, newRelPath);
                directoryS3Api.copyObject(
                        absolutePathDir + "_meta",
                        newAbsolutePathDir + "_meta",
                        createMetaDataDir(
                                dir.getName(),
                                newRelPathDir,
                                newAbsolutePathDir));
            }

            for (File file : files) {
                String absolutePathFile = file.getAbsolutePath();
                String newAbsolutePathFile = absolutePathFile.replaceFirst(previousAbsolutePath, newAbsolutePath);
                String newRelPathFile = file.getRelativePath().replaceFirst(previousRelPath, newRelPath);
                directoryS3Api.copyObject(
                        absolutePathFile,
                        newAbsolutePathFile,
                        createMetaDataFile(
                                file.getName(),
                                newRelPathFile,
                                newAbsolutePathFile));
            }

            directoryS3Api.copyObject(
                    previousAbsolutePath + "_meta",
                    newAbsolutePath + "_meta",
                    createMetaDataDir(
                            newName,
                            newRelPath,
                            newAbsolutePath));
            this.remove(previousAbsolutePath);

        } catch (IllegalArgumentException | ServerException | InsufficientDataException | ErrorResponseException |
                 IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont rename", previousAbsolutePath);
            throw new DirectoryRenameException("Directory dont rename");
        }
    }

    @Override
    public Directory getRecursive(String absolutePath) {
        Iterable<Result<Item>> results = directoryS3Api.getObjectsRecursive(absolutePath + "/");
        try {
            List<Item> items = getListItemsNoIsMinioDir(results);
            return Directory.builder()
                    .absolutePath(absolutePath)
                    .directories(getDirFromItems(items))
                    .files(getFilesFromItems(items))
                    .build();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont get all objects", absolutePath);
            throw new DirectorySearchFilesException("File search error");
        }
    }

    private Map<String, String> createMetaDataDir(String name, String relPath, String absPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("rel_path", relPath);
        metaData.put("abs_path", absPath);
        metaData.put("dir", "");
        return metaData;
    }

    private Map<String, String> createMetaDataFile(String name, String relPath, String absPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("rel_path", relPath);
        metaData.put("abs_path", absPath);
        metaData.put("file", "");
        return metaData;
    }

    private List<Item> getListItemsNoIsMinioDir(Iterable<Result<Item>> results) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
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
        InfoOfObjectS3 info = buildInfo(item.userMetadata());
        return File.builder()
                .name(info.getName())
                .relativePath(info.getRelativePath())
                .absolutePath(item.objectName())
                .build();
    }

    private Directory itemMaptoDirectory(Item item) {
        InfoOfObjectS3 info = buildInfo(item.userMetadata());
        return new Directory(
                info.getName(),
                info.getAbsPath(),
                info.getRelativePath());
    }

    private InfoOfObjectS3 buildInfo(Map<String, String> metaData) {
        return InfoOfObjectS3.builder()
                .name(metaData.get("X-Amz-Meta-Name"))
                .relativePath(metaData.get("X-Amz-Meta-Rel_path"))
                .absPath(metaData.get("X-Amz-Meta-Abs_path"))
                .isDir(metaData.containsKey("X-Amz-Meta-Dir"))
                .build();
    }

    private InfoOfObjectS3 buildStatObject(Map<String, String> metaData) {
        return InfoOfObjectS3.builder()
                .name(metaData.get("name"))
                .relativePath(metaData.get("rel_path"))
                .absPath(metaData.get("abs_path"))
                .isDir(metaData.containsKey("dir"))
                .build();
    }
}
