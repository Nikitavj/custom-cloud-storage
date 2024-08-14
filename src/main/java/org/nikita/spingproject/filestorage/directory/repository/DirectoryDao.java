package org.nikita.spingproject.filestorage.directory.repository;

import org.nikita.spingproject.filestorage.directory.Directory;

public interface DirectoryDao {
    void add(Directory directory);

    void remove(String path);

    Directory get(String path);

    void rename(String previousAbsolutePath, String newAbsolutePath, String previousRelPath, String newRelPath, String newName);
}
