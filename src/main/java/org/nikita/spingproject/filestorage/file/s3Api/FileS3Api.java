package org.nikita.spingproject.filestorage.file.s3Api;

import java.io.InputStream;
import java.util.Map;

public interface FileS3Api {
    void putFile(Map<String, String> metaData, String path, InputStream is);
    void deleteFile(String path);
    InputStream downloadFile(String path);
    void copyFile(String path, String newPath, Map<String, String> metaData);
}
