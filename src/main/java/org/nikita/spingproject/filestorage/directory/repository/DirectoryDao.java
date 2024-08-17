package org.nikita.spingproject.filestorage.directory.repository;

import org.nikita.spingproject.filestorage.directory.Directory;

public interface DirectoryDao {
    void add(Directory directory);
    Directory get(String path);
    Directory getAll(String absolutePath);
    void remove(String path);

    void rename(String previousAbsolutePath, String newAbsolutePath, String previousRelPath, String newRelPath, String newName);

}
