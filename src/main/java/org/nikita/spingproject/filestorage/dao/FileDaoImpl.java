package org.nikita.spingproject.filestorage.dao;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.nikita.spingproject.filestorage.directory.exception.DirectorySearchFilesException;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.file.exception.*;
import org.nikita.spingproject.filestorage.file.s3Api.FileS3Api;
import org.nikita.spingproject.filestorage.file.service.PathFileService;
import org.nikita.spingproject.filestorage.utils.DirectoryUtil;
import org.nikita.spingproject.filestorage.utils.FileUtil;
import org.nikita.spingproject.filestorage.utils.PathEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class FileDaoImpl implements FileDao {
    private final FileS3Api fileS3Api;
    private final PathFileService pathFileService;

    @Autowired
    public FileDaoImpl(FileS3Api fileS3Api, PathFileService pathFileService) {
        this.fileS3Api = fileS3Api;
        this.pathFileService = pathFileService;
    }

    @Override
    public void add(File file) {
        try {
            String encodePath = PathEncoderUtil.encode(file.getAbsolutePath());
            checkExistsFile(encodePath);
            fileS3Api.putFile(
                    FileUtil.createMetaDataFile(
                            file.getName(),
                            file.getRelativePath(),
                            file.getAbsolutePath()),
                    encodePath,
                    file.getInputStream());
        }catch (FileAlreadyExistsException e) {
            throw new FileAlreadyExistsException(e.getMessage());
        } catch (Exception e) {
            log.warn("File {} dont add", file.getAbsolutePath());
            throw new FileUploadException("File upload error");
        }
    }

    @Override
    public void remove(String absolutePath) {
        try {
            String encodePath = PathEncoderUtil.encode(absolutePath);
            fileS3Api.removeObject(encodePath);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("File {} dont remove", absolutePath);
            throw new FileRemoveException("File not deleted");
        }
    }

    @Override
    public File get(String absolutePath) {
        try {
            String encodePath = PathEncoderUtil.encode(absolutePath);
            InputStream is = fileS3Api.getInputStream(encodePath);
            StatObjectResponse stat = fileS3Api.getInfo(encodePath);

            return File.builder()
                    .inputStream(is)
                    .name(stat.userMetadata().get("name"))
                    .build();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("File {} dont get", absolutePath);
            throw new FileDownloadException("File download error");
        }
    }

    @Override
    public void rename(String prevAbsolutePath, String targetAbsolutePath, String relativePath, String name) {
        try {
            String encodePrevPath = PathEncoderUtil.encode(prevAbsolutePath);
            String encodeTargetPath = PathEncoderUtil.encode(targetAbsolutePath);

            checkExistsFile(encodeTargetPath);
            fileS3Api.copyFile(
                    encodePrevPath,
                    encodeTargetPath,
                    FileUtil.createMetaDataFile(name, relativePath, targetAbsolutePath));

        }catch (FileAlreadyExistsException e) {
            throw new FileAlreadyExistsException(e.getMessage());
        } catch (IllegalArgumentException | ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("File {} dont rename", prevAbsolutePath);
            throw new FileRenameException("File not rename");
        }
    }


    @Override
    public List<File> getAll() {
        String rootPath = pathFileService.rootPathForUser();
        try {
            Iterable<Result<Item>> results = fileS3Api
                    .getObjectsRecursive(PathEncoderUtil.encode(rootPath) + "/");
            List<Item> items = DirectoryUtil.getListItemsNoIsMinioDir(results);
            return FileUtil.getFilesFromItems(items);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont get all objects", rootPath);
            throw new DirectorySearchFilesException("File search error");
        }
    }

    private void checkExistsFile(String absPath) {
        StatObjectResponse stat = null;
        try {
            stat = fileS3Api.getInfo(absPath);
            System.out.println();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if(stat != null) {
            throw new FileAlreadyExistsException("File already exists");
        }
    }
}