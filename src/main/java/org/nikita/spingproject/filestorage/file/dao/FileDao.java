package org.nikita.spingproject.filestorage.file.dao;

import org.nikita.spingproject.filestorage.file.File;

public interface FileDao {
    void add(File file);
    void remove(String absolutePath);
    File get(String absolutePath);
    void rename(String prevAbsolutePath, String targetAbsolutePath, String relativePath, String name);
}
