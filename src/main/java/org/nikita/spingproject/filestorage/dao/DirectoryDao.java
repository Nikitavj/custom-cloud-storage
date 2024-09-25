package org.nikita.spingproject.filestorage.dao;

import org.nikita.spingproject.filestorage.directory.Directory;

public interface DirectoryDao extends BaseDao<Directory> {
    void rename(String previousAbsolutePath, String newAbsolutePath, String previousRelPath);
}
