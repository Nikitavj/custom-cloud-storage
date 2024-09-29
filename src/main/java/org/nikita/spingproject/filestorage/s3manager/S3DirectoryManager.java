package org.nikita.spingproject.filestorage.s3manager;

import org.nikita.spingproject.filestorage.directory.Directory;

public interface S3DirectoryManager extends S3Manager<Directory> {
    void copy(Directory dir, String targetPath, String newName);
    Directory getRoot();
}
