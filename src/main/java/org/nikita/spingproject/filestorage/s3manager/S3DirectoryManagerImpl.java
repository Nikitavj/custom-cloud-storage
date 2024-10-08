package org.nikita.spingproject.filestorage.s3manager;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.nikita.spingproject.filestorage.commons.dto.InfoMetaS3;
import org.nikita.spingproject.filestorage.commons.exception.StorageException;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.exception.*;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.path.PathUtil;
import org.nikita.spingproject.filestorage.path.S3DirectoryPathBuilder;
import org.nikita.spingproject.filestorage.s3Api.DirectoryS3Api;
import org.nikita.spingproject.filestorage.utils.DirectoryUtil;
import org.nikita.spingproject.filestorage.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class S3DirectoryManagerImpl implements S3DirectoryManager {
    private final DirectoryS3Api s3Api;
    private final S3DirectoryPathBuilder S3pathBuilder;
    private final S3FileManager s3Manager;

    @Autowired
    public S3DirectoryManagerImpl(DirectoryS3Api s3Api, S3DirectoryPathBuilder S3pathBuilder, S3FileManager s3Manager) {
        this.s3Api = s3Api;
        this.S3pathBuilder = S3pathBuilder;
        this.s3Manager = s3Manager;
    }

    @Override
    public void add(Directory dir) {
        try {
            String pathS3 = S3pathBuilder.buildPathMeta(dir.getPath());
            checkExists(dir.getPath());

            s3Api.put(
                    DirectoryUtil.createMetaDataDir(
                            dir.getName(),
                            dir.getPath()),
                    pathS3);

        } catch (DirectoryAlreadyExistsException e) {
            throw new DirectoryAlreadyExistsException(e.getMessage());
        } catch (MinioException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            log.warn("Directory {} dont create", dir.getPath());
            throw new DirectoryCreatedException("Directory not created");
        }
    }

    @Override
    public Directory get(String path) {
        Directory directory = new Directory();
        try {
            if (!PathUtil.isRoot(path)) {
                String pathS3Meta = S3pathBuilder.buildPathMeta(path);
                StatObjectResponse stat = s3Api.getInfo(pathS3Meta);
                InfoMetaS3 info = DirectoryUtil.convertMetaToObject(stat.userMetadata());
                directory.setName(info.getName());
                directory.setPath(info.getPath());
            }

            fillDirectory(directory);
            return directory;

        } catch (ServerException | InsufficientDataException |
                 ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException |
                 InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont get", path);
            throw new DirectoryException("Unable to get directory objects");
        }
    }

    @Override
    public Directory getRoot() {
        return get("");
    }

    @Override
    public void remove(String path) {
        try {
            String pathS3 = S3pathBuilder.buildPathDirObjects(path);
            String pathS3Meta = S3pathBuilder.buildPathMeta(path);
            s3Api.removeObject(pathS3Meta);

            Iterable<Result<Item>> results = s3Api.listObjectsRecursive(pathS3);
            List<DeleteObject> deleteObjects = new ArrayList<>();
            for (Result<Item> itemResult : results) {
                Item item = itemResult.get();
                deleteObjects.add(new DeleteObject(item.objectName()));
            }
            s3Api.removeObjects(deleteObjects);

        } catch (ServerException | InsufficientDataException |
                 ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException |
                 InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont remove", path);
            throw new DirectoryRemoveException("Directory not remove");
        }
    }

    @Override
    public void copy(Directory dir, String targetPath, String newName) {
        try {
            String prevPathS3Meta = S3pathBuilder.buildPathMeta(dir.getPath());
            String targPathS3Meta = S3pathBuilder.buildPathMeta(targetPath);

            checkExists(targetPath);

            copyInsides(dir, dir.getPath(), targetPath);
            s3Api.copyObject(
                    prevPathS3Meta,
                    targPathS3Meta,
                    DirectoryUtil.createMetaDataDir(
                            newName,
                            targetPath));

        } catch (IllegalArgumentException | ServerException |
                 InsufficientDataException | ErrorResponseException |
                 IOException | NoSuchAlgorithmException |
                 InvalidKeyException | InvalidResponseException |
                 XmlParserException | InternalException e) {
            log.warn("Directory {} dont rename", dir.getPath());
            throw new DirectoryRenameException("Directory dont rename");
        }
    }

    @Override
    public void checkExists(String path) {
        try {
            String pathS3 = S3pathBuilder.buildPathMeta(path);

            StatObjectResponse stat;
            try {
                stat = s3Api.getInfo(pathS3);
            } catch (Exception e) {
                return;
            }

            if (stat != null) {
                throw new DirectoryAlreadyExistsException("Directory already exists");
            }
        } catch (UnsupportedEncodingException e) {
            throw new StorageException();
        }
    }

    private void copyInsides(Directory directory, String fromPath, String toPath) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (Directory dir : directory.getDirectories()) {
            copyInsides(dir, fromPath, toPath);

            String newPath = dir.getPath().replaceFirst(fromPath, toPath);
            String pathS3Meta = S3pathBuilder.buildPathMeta(directory.getPath());
            String newPathS3Meta = S3pathBuilder.buildPathMeta(newPath);

            s3Api.copyObject(
                    pathS3Meta,
                    newPathS3Meta,
                    DirectoryUtil.createMetaDataDir(
                            dir.getName(),
                            newPath));
        }

        for (File file : directory.getFiles()) {
            String newPath = file.getPath().replaceFirst(fromPath, toPath);
            s3Manager.copy(file.getPath(), newPath, file.getName());
        }
    }

    private void fillDirectory(Directory dir) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String pathS3 = S3pathBuilder.buildPathDirObjects(dir.getPath());

        Iterable<Result<Item>> results = s3Api.listObjects(pathS3);
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
}
