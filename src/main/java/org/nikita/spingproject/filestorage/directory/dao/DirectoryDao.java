package org.nikita.spingproject.filestorage.directory.dao;

import org.nikita.spingproject.filestorage.commons.BaseDao;
import org.nikita.spingproject.filestorage.directory.Directory;

public interface DirectoryDao extends BaseDao<Directory> {
    Directory getRecursive(String absolutePath);
    void rename(String previousAbsolutePath, String newAbsolutePath, String previousRelPath, String newRelPath, String newName);
}
