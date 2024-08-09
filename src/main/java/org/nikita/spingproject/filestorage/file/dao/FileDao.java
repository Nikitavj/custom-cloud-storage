package org.nikita.spingproject.filestorage.file.dao;

import io.minio.StatObjectResponse;

import java.io.InputStream;
import java.util.Map;

public interface FileDao {

    void putFile(Map<String, String> metaData, String path, InputStream is);

    void deleteFile(String path);

    InputStream downloadFile(String path);

    void copyFile(String path, String newPath, Map<String, String> metaData);

    StatObjectResponse getStatFile(String path);

}
