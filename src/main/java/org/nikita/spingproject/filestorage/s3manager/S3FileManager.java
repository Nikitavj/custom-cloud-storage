package org.nikita.spingproject.filestorage.s3manager;

import org.nikita.spingproject.filestorage.file.File;

public interface S3FileManager extends S3Manager<File> {
    void copy(String prevAbsolutePath, String targetAbsolutePath, String relativePath);
}
