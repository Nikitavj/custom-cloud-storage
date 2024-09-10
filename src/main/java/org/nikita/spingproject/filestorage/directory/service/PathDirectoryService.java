package org.nikita.spingproject.filestorage.directory.service;

import java.io.UnsupportedEncodingException;

public interface PathDirectoryService {
    String rootPathForUser();

    String absolutPath(String relativePath);

    String absolutePathNewDir(String currentPath, String name);

    String relativePath(String path, String name);

    String renameAbsolutePath(String previousAbsolutePath, String newName);

    String renameRelativePath(String previousPath, String newName);
}
