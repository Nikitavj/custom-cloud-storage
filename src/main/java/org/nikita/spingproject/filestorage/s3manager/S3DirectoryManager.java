package org.nikita.spingproject.filestorage.s3manager;

import org.nikita.spingproject.filestorage.directory.Directory;

public interface S3DirectoryManager extends S3Manager<Directory> {
    void rename(String previousAbsolutePath, String newAbsolutePath, String previousRelPath);
    void copy(Directory directory, String fromPath, String toPath);
}
