package org.nikita.spingproject.filestorage.directory.dao;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.nikita.spingproject.filestorage.commons.InfoMetaS3;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.service.PathDirectoryService;
import org.nikita.spingproject.filestorage.utils.DirectoryUtil;
import org.nikita.spingproject.filestorage.directory.exception.*;
import org.nikita.spingproject.filestorage.directory.s3Api.DirectoryS3Api;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.utils.FileUtil;
import org.nikita.spingproject.filestorage.utils.PathEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class DirectoryDaoImpl implements DirectoryDao {
    private static final String POSTFIX = "_meta";
    private final DirectoryS3Api directoryS3Api;
    private final PathDirectoryService pathDirectoryService;

    @Autowired
    public DirectoryDaoImpl(DirectoryS3Api directoryS3Api, PathDirectoryService directoryService) {
        this.directoryS3Api = directoryS3Api;
        this.pathDirectoryService = directoryService;
    }

    @Override
    public void add(Directory directory) {
        String absolutePath = pathDirectoryService.absolutePathNewDir(
                directory.getRelativePath(),
                directory.getName());
        try {
            String encodePath = PathEncoderUtil.encode(absolutePath);
            String pathForMeta = encodePath + POSTFIX;
            checkExistsDirectory(pathForMeta);

            directoryS3Api.create(
                    DirectoryUtil.createMetaDataDir(
                            directory.getName(),
                            directory.getRelativePath(),
                            directory.getAbsolutePath()
                    ),
                    pathForMeta);
        } catch (DirectoryAlreadyExistsException e) {
            throw new DirectoryAlreadyExistsException(e.getMessage());
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.warn("Directory {} dont create", directory.getAbsolutePath());
            throw new DirectoryCreatedException("Directory not created");
        }
    }

    @Override
    public Directory get(String relPath) {
        String absolutePath = pathDirectoryService.absolutPath(relPath);
        Directory directory = new Directory();

        try {
            String encodePath = PathEncoderUtil.encode(absolutePath);
            directory.setAbsolutePath(absolutePath);

            if (absolutePath.contains("/")) {
                StatObjectResponse stat = directoryS3Api.getInfo(encodePath + POSTFIX);
                InfoMetaS3 info = convertMetaToObject(stat.userMetadata());
                directory.setName(info.getName());
                directory.setRelativePath(info.getRelativePath());
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

    @Override
    public void remove(String relPath) {
        String absolutePath = pathDirectoryService.absolutPath(relPath);
        try {
            String encodePath = PathEncoderUtil.encode(absolutePath);
            String pathForMeta = encodePath + POSTFIX;
            directoryS3Api.removeObject(pathForMeta);

            Iterable<Result<Item>> results = directoryS3Api.getObjectsRecursive(encodePath + "/");
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
    public void rename(String previousRelPath, String newRelPath, String newName) {
        String previousAbsolutePath = pathDirectoryService.absolutPath(previousRelPath);
        String targetAbsolutePath = pathDirectoryService.renameAbsolutePath(previousAbsolutePath, newName);
        try {
            String encodePrevPath = PathEncoderUtil.encode(previousAbsolutePath);
            String encodeTargPath = PathEncoderUtil.encode(targetAbsolutePath);

            checkExistsDirectory(encodeTargPath + POSTFIX);
            Iterable<Result<Item>> results = directoryS3Api.getObjectsRecursive(encodePrevPath + "/");
            List<Item> items = DirectoryUtil.getListItemsNoIsMinioDir(results);

            List<Directory> directories = DirectoryUtil.getDirFromItems(items);
            List<File> files = FileUtil.getFilesFromItems(items);

            for (Directory dir : directories) {
                String absolutePathDir = dir.getAbsolutePath();
                String newAbsolutePathDir = absolutePathDir.replaceFirst(previousAbsolutePath, targetAbsolutePath);
                String newRelPathDir = dir.getRelativePath().replaceFirst(previousRelPath, newRelPath);
                directoryS3Api.copyObject(
                        PathEncoderUtil.encode(absolutePathDir) + POSTFIX,
                        PathEncoderUtil.encode(newAbsolutePathDir) + POSTFIX,
                        DirectoryUtil.createMetaDataDir(
                                dir.getName(),
                                newRelPathDir,
                                newAbsolutePathDir));
            }

            for (File file : files) {
                String absolutePathFile = file.getAbsolutePath();
                String newAbsolutePathFile = absolutePathFile.replaceFirst(previousAbsolutePath, targetAbsolutePath);
                String newRelPathFile = file.getRelativePath().replaceFirst(previousRelPath, newRelPath);
                directoryS3Api.copyObject(
                        PathEncoderUtil.encode(absolutePathFile),
                        PathEncoderUtil.encode(newAbsolutePathFile),
                        FileUtil.createMetaDataFile(
                                file.getName(),
                                newRelPathFile,
                                newAbsolutePathFile));
            }

            directoryS3Api.copyObject(
                    encodePrevPath + POSTFIX,
                    encodeTargPath + POSTFIX,
                    DirectoryUtil.createMetaDataDir(
                            newName,
                            newRelPath,
                            targetAbsolutePath));
            this.remove(previousRelPath);

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
        try {
            Iterable<Result<Item>> results = directoryS3Api
                    .getObjectsRecursive(PathEncoderUtil.encode(absolutePath) + "/");
            List<Item> items = DirectoryUtil.getListItemsNoIsMinioDir(results);
            return Directory.builder()
                    .absolutePath(absolutePath)
                    .directories(DirectoryUtil.getDirFromItems(items))
                    .files(FileUtil.getFilesFromItems(items))
                    .build();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont get all objects", absolutePath);
            throw new DirectorySearchFilesException("File search error");
        }
    }

    private void fillDirectory(Directory dir) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> results = directoryS3Api
                .getObjects(PathEncoderUtil.encode(dir.getAbsolutePath()) + "/");
        List<Item> items = DirectoryUtil.getListItemsNoIsMinioDir(results);

        List<Directory> directories = DirectoryUtil.getDirFromItems(items);
        List<File> files = FileUtil.getFilesFromItems(items);

        for (Directory directory : directories) {
            dir.putDirectory(directory);
            fillDirectory(directory);
        }
        for (File file : files) {
            dir.putFile(file);
        }
    }

    private void checkExistsDirectory(String absPath) {
        StatObjectResponse stat = null;
        try {
            stat = directoryS3Api.getInfo(absPath);
        } catch (Exception e) {
        }

        if (stat != null) {
            throw new DirectoryAlreadyExistsException("Directory already exists");
        }
    }

    private static InfoMetaS3 convertMetaToObject(Map<String, String> metaData) {
        return new InfoMetaS3(
                metaData.get("name"),
                metaData.get("rel_path"),
                metaData.get("abs_path"),
                metaData.containsKey("dir"));
    }
}
