package org.nikita.spingproject.filestorage.directory.dao;

import org.nikita.spingproject.filestorage.directory.Directory;

public interface DirectoryDao {
    void add(Directory directory);
    Directory get(String path);
    Directory getRecursive(String absolutePath);
    void remove(String absolutePath);

    void rename(String previousAbsolutePath, String newAbsolutePath, String previousRelPath, String newRelPath, String newName);

}
