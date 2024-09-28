package org.nikita.spingproject.filestorage.s3manager;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.file.exception.*;
import org.nikita.spingproject.filestorage.path.S3FilePathBuilder;
import org.nikita.spingproject.filestorage.s3Api.FileS3Api;
import org.nikita.spingproject.filestorage.utils.DirectoryUtil;
import org.nikita.spingproject.filestorage.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Repository
public class S3FileManagerImpl implements S3FileManager {
    private final FileS3Api fileS3Api;
    private final S3FilePathBuilder s3pathBuilder;

    @Autowired
    public S3FileManagerImpl(FileS3Api fileS3Api, S3FilePathBuilder s3pathBuilder) {
        this.fileS3Api = fileS3Api;
        this.s3pathBuilder = s3pathBuilder;
    }

    @Override
    public void add(File file) {
        try {
            String pathS3 = s3pathBuilder.buildPath(file.getPath());
            checkExistsFile(pathS3);
            fileS3Api.putFile(
                    FileUtil.createMetaDataFile(
                            file.getName(),
                            file.getPath()),
                    pathS3,
                    file.getInputStream());
        }catch (FileAlreadyExistsException e) {
            throw new FileAlreadyExistsException(e.getMessage());
        } catch (Exception e) {
            log.warn("File {} dont add", file.getPath());
            throw new FileUploadException("File upload error");
        }
    }

    @Override
    public void remove(String path) {
        try {
            String pathS3 = s3pathBuilder.buildPath(path);
            fileS3Api.removeObject(pathS3);

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("File {} dont remove", path);
            throw new FileRemoveException("File not deleted");
        }
    }

    @Override
    public File get(String path) {
        try {
            String pathS3 = s3pathBuilder.buildPath(path);
            InputStream is = fileS3Api.getInputStream(pathS3);
            StatObjectResponse stat = fileS3Api.getInfo(pathS3);

            return File.builder()
                    .inputStream(is)
                    .name(stat.userMetadata().get("name"))
                    .build();

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("File {} dont get", path);
            throw new FileDownloadException("File download error");
        }
    }

    @Override
    public void copy(String prevPath, String targPath, String name) {
        try {
            String prevPathS3 = s3pathBuilder.buildPath(prevPath);
            String targPathS3 = s3pathBuilder.buildPath(targPath);
            checkExistsFile(targPathS3);
            fileS3Api.copyFile(
                    prevPathS3,
                    targPathS3,
                    FileUtil.createMetaDataFile(name, targPath));

            this.remove(prevPath);
        }catch (FileAlreadyExistsException e) {
            throw new FileAlreadyExistsException(e.getMessage());
        } catch (IllegalArgumentException | ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("File {} dont rename", prevPath);
            throw new FileRenameException("File not rename");
        }
    }


    @Override
    public List<File> getAll() {
        try {
            Iterable<Result<Item>> results = fileS3Api
                    .listObjectsRecursive(s3pathBuilder.userPath());
            List<Item> items = DirectoryUtil.getListItemsNoIsMinioDir(results);
            return FileUtil.getFilesFromItems(items);

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.warn("Dont get all objects");
            throw new SearchFilesException("File search error");
        }
    }

    private void checkExistsFile(String pathS3) {
        StatObjectResponse stat;
        try {
            stat = fileS3Api.getInfo(pathS3);
        } catch (Exception e) {
            return;
        }

        if(stat != null) {
            throw new FileAlreadyExistsException("File already exists");
        }
    }
}