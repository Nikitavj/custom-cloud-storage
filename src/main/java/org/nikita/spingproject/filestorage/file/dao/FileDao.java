package org.nikita.spingproject.filestorage.file.dao;

import org.nikita.spingproject.filestorage.file.File;

import java.io.InputStream;

public interface FileDao {

    void add(File file);

    void remove(String absolutePath);

    InputStream get(String absolutePath);

    void rename(String prevAbsolutePath, String targetAbsolutePath, String relativePath, String name);
}
