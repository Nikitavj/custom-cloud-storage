package org.nikita.spingproject.filestorage.file.dao;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.file.*;
import org.nikita.spingproject.filestorage.file.exception.FileCreateException;
import org.nikita.spingproject.filestorage.file.exception.FileDownloadException;
import org.nikita.spingproject.filestorage.file.exception.FileRemoveException;
import org.nikita.spingproject.filestorage.file.exception.FileRenameException;
import org.nikita.spingproject.filestorage.file.s3Api.FileS3Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class FileDaoImpl implements FileDao {
    private FileS3Api fileS3Api;

    @Autowired
    public FileDaoImpl(FileS3Api fileS3Api) {
        this.fileS3Api = fileS3Api;
    }

    @Override
    public void add(File file) {
        try {
            fileS3Api.putFile(
                    createMetaDataFile(
                            file.getName(),
                            file.getRelativePath()),
                    file.getAbsolutePath(),
                    file.getInputStream());
        } catch (Exception e) {
            throw new FileCreateException("File not uploaded");
        }
    }

    @Override
    public void remove(String absolutePath) {
        try {
            fileS3Api.deleteFile(absolutePath);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new FileRemoveException("File not deleted");
        }
    }

    @Override
    public InputStream get(String absolutePath) {
        try {
            return fileS3Api.downloadFile(absolutePath);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new FileDownloadException("File not download");
        }
    }

    @Override
    public void rename(String prevAbsolutePath, String targetAbsolutePath, String relativePath, String name) {
        try {
            fileS3Api.copyFile(
                    prevAbsolutePath,
                    targetAbsolutePath,
                    createMetaDataFile(name, relativePath));

            throw new FileRenameException("dsafhasdkj;lf rename exception");

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new FileRenameException("File not rename");
        }
    }

    private Map<String, String> createMetaDataFile(String name, String relPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("rel_path", relPath);
        metaData.put("file", "");
        return metaData;
    }
}
