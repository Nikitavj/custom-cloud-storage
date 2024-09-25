package org.nikita.spingproject.filestorage.dao;

import org.nikita.spingproject.filestorage.file.File;

public interface FileDao extends BaseDao<File> {
    void rename(String prevAbsolutePath, String targetAbsolutePath, String relativePath);
}
