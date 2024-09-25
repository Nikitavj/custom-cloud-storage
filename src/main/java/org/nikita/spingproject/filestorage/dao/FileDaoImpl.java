package org.nikita.spingproject.filestorage.dao;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.nikita.spingproject.filestorage.directory.exception.DirectorySearchFilesException;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.file.exception.*;

import org.nikita.spingproject.filestorage.path.S3FilePathBuilder;
import org.nikita.spingproject.filestorage.s3Api.FileS3Api;
import org.nikita.spingproject.filestorage.utils.DirectoryUtil;
import org.nikita.spingproject.filestorage.utils.FileUtil;
import org.nikita.spingproject.filestorage.utils.PathEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Repository
public class FileDaoImpl implements FileDao {
    private final FileS3Api fileS3Api;
    private final S3FilePathBuilder s3pathBuilder;

    @Autowired
    public FileDaoImpl(FileS3Api fileS3Api, S3FilePathBuilder s3pathBuilder) {
        this.fileS3Api = fileS3Api;
        this.s3pathBuilder = s3pathBuilder;
    }

    @Override
    public void add(File file) {
        try {
            String pathS3 = s3pathBuilder.buildPath(file.getPath());
            String encodePathS3 = PathEncoderUtil.encode(pathS3);
            checkExistsFile(encodePathS3);
            fileS3Api.putFile(
                    FileUtil.createMetaDataFile(
                            file.getName(),
                            file.getPath(),
                            pathS3),
                    encodePathS3,
                    file.getInputStream());
        }catch (FileAlreadyExistsException e) {
            throw new FileAlreadyExistsException(e.getMessage());
        } catch (Exception e) {
            log.warn("File {} dont add", file.getPathS3());
            throw new FileUploadException("File upload error");
        }
    }

    @Override
    public void remove(String relPath) {
        String pathS3 = s3pathBuilder.buildPath(relPath);
        try {
            String encodePathS3 = PathEncoderUtil.encode(pathS3);
            fileS3Api.removeObject(encodePathS3);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("File {} dont remove", pathS3);
            throw new FileRemoveException("File not deleted");
        }
    }

    @Override
    public File get(String relPath) {
        String pathS3 = s3pathBuilder.buildPath(relPath);
        try {
            String encodePath = PathEncoderUtil.encode(pathS3);
            InputStream is = fileS3Api.getInputStream(encodePath);
            StatObjectResponse stat = fileS3Api.getInfo(encodePath);

            return File.builder()
                    .inputStream(is)
                    .name(stat.userMetadata().get("name"))
                    .build();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("File {} dont get", pathS3);
            throw new FileDownloadException("File download error");
        }
    }

    @Override
    public void rename(String relativePath, String newRelativePath, String name) {
        String prevPathS3 = s3pathBuilder.buildPath(relativePath);
        String targPathS3 = s3pathBuilder.buildPath(newRelativePath);
        try {
            String encodePrevPathS3 = PathEncoderUtil.encode(prevPathS3);
            String encodeTargetPathS3 = PathEncoderUtil.encode(targPathS3);

            checkExistsFile(encodeTargetPathS3);
            fileS3Api.copyFile(
                    encodePrevPathS3,
                    encodeTargetPathS3,
                    FileUtil.createMetaDataFile(name, relativePath, newRelativePath));

            this.remove(relativePath);
        }catch (FileAlreadyExistsException e) {
            throw new FileAlreadyExistsException(e.getMessage());
        } catch (IllegalArgumentException | ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("File {} dont rename", prevPathS3);
            throw new FileRenameException("File not rename");
        }
    }


    @Override
    public List<File> getAll() {
        String pathS3 = s3pathBuilder.rootPathForUser();
        try {
            String encodePathS3 = PathEncoderUtil.encode(pathS3);
            Iterable<Result<Item>> results = fileS3Api
                    .getObjectsRecursive(encodePathS3 + "/");
            List<Item> items = DirectoryUtil.getListItemsNoIsMinioDir(results);
            return FileUtil.getFilesFromItems(items);

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Directory {} dont get all objects", pathS3);
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