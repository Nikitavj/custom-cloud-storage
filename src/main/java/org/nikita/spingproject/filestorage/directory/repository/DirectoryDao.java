package org.nikita.spingproject.filestorage.directory.repository;

import org.nikita.spingproject.filestorage.directory.Directory;

public interface DirectoryDao {
    void add(Directory directory);

    void remove(Directory directory);

    Directory get(String path);
}
