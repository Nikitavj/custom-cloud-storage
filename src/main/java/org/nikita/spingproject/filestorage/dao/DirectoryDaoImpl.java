package org.nikita.spingproject.filestorage.dao;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.commons.InfoMetaS3;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.exception.*;
import org.nikita.spingproject.filestorage.path.S3DirectoryPathBuilder;
import org.nikita.spingproject.filestorage.service.PathDirectoryService;
import org.nikita.spingproject.filestorage.s3Api.DirectoryS3Api;
import org.nikita.spingproject.filestorage.utils.DirectoryUtil;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.utils.FileUtil;
import org.nikita.spingproject.filestorage.utils.PathEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    private final S3DirectoryPathBuilder pathBuilder;
    private final FileDao fileDao;

    @Autowired
    public DirectoryDaoImpl(DirectoryS3Api directoryS3Api, PathDirectoryService directoryService, S3DirectoryPathBuilder pathBuilder, FileDao fileDao) {
        this.directoryS3Api = directoryS3Api;
        this.pathDirectoryService = directoryService;
        this.pathBuilder = pathBuilder;
        this.fileDao = fileDao;
    }

    @Override
    public void add(Directory dir) {
        try {
            String pathS3 = pathBuilder.buildPathMeta(dir.getPath());
            checkExistsDirectory(pathS3);

            directoryS3Api.create(
                    DirectoryUtil.createMetaDataDir(
                            dir.getName(),
                            dir.getPath()),
                    pathS3);
        } catch (DirectoryAlreadyExistsException e) {
            throw new DirectoryAlreadyExistsException(e.getMessage());
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.warn("Directory {} dont create", dir.getPath());
            throw new DirectoryCreatedException("Directory not created");
        }
    }

    @Override
    public Directory get(String path) {
        Directory directory = new Directory();
        try {
            String pathS3 = pathBuilder.buildPath(path);
            String pathS3Meta = pathBuilder.buildPathMeta(path);

            if (pathS3.contains("/")) {
                StatObjectResponse stat = directoryS3Api.getInfo(pathS3Meta);
                InfoMetaS3 info = convertMetaToObject(stat.userMetadata());
                directory.setName(info.getName());
                directory.setPath(info.getPath());
            }
            fillDirectory(directory);
            return directory;

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont get", path);
            throw new GetDirectoryObjectsExcepton("Unable to get directory objects");
        }
    }

    @Override
    public void remove(String path) {
        try {
            String pathS3 = pathBuilder.buildPath(path);
            String pathS3Meta = pathBuilder.buildPathMeta(path);
            directoryS3Api.removeObject(pathS3Meta);

            Iterable<Result<Item>> results = directoryS3Api.getObjectsRecursive(pathS3 + "/");
            List<DeleteObject> deleteObjects = new ArrayList<>();
            for (Result<Item> itemResult : results) {
                Item item = itemResult.get();
                deleteObjects.add(new DeleteObject(item.objectName()));
            }
            directoryS3Api.deleteObjects(deleteObjects);

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont remove", path);
            throw new DirectoryRemoveException("Directory not remove");
        }
    }

    @Override
    public void rename(String prevPath, String targPath, String newName) {
        try {
            String prevPathS3Meta = pathBuilder.buildPathMeta(prevPath);
            String targPathS3Meta = pathBuilder.buildPathMeta(targPath);

            checkExistsDirectory(targPathS3Meta);
            Directory dir = this.get(prevPath);
            copy(dir, prevPath, targPath);
            directoryS3Api.copyObject(
                    prevPathS3Meta,
                    targPathS3Meta,
                    DirectoryUtil.createMetaDataDir(
                            newName,
                            targPath));
            this.remove(prevPath);

        } catch (IllegalArgumentException | ServerException | InsufficientDataException | ErrorResponseException |
                 IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont rename", prevPath);
            throw new DirectoryRenameException("Directory dont rename");
        }
    }

    @Override
    public List<Directory> getAll() {
        String rootPath = pathDirectoryService.rootPathForUser();
        try {
            Iterable<Result<Item>> results = directoryS3Api
                    .getObjectsRecursive(PathEncoderUtil.encode(rootPath) + "/");
            List<Item> items = DirectoryUtil.getListItemsNoIsMinioDir(results);
            return DirectoryUtil.getDirFromItems(items);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont get all objects", rootPath);
            throw new DirectorySearchFilesException("File search error");
        }
    }

    private void fillDirectory(Directory dir) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String pathS3 = pathBuilder.buildPath(dir.getPath());

        Iterable<Result<Item>> results = directoryS3Api.getObjects(pathS3 + "/");
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

    private void copy(Directory directory, String fromPath, String toPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (Directory dir : directory.getDirectories()) {
            copy(dir, fromPath, toPath);

            String newPath = dir.getPath().replaceFirst(fromPath, toPath);
            String pathS3Meta = pathBuilder.buildPathMeta(directory.getPath());
            String newPathS3Meta = pathBuilder.buildPathMeta(newPath);

            directoryS3Api.copyObject(
                    pathS3Meta,
                    newPathS3Meta,
                    DirectoryUtil.createMetaDataDir(
                            dir.getName(),
                            newPath));
        }

        for (File file : directory.getFiles()) {
            String newPath = file.getPath().replaceFirst(fromPath, toPath);
            fileDao.rename(file.getPath(), newPath, file.getName());
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
                metaData.get("path"),
                metaData.containsKey("dir"));
    }
}
