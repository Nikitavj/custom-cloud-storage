package org.nikita.spingproject.filestorage.file.dao;

import io.minio.StatObjectResponse;
import io.minio.errors.*;
import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.file.File;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface FileDao {

    void putFile(Map<String, String> metaData, String path, InputStream is);

    void deleteFile(String path);

    InputStream downloadFile(String path);

    StatObjectResponse getStatFile(String path);
}
