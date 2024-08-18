package org.nikita.spingproject.filestorage.directory.service;

public interface PathDirectoryService {
    String rootPathForUser(String userName);

    String absolutPath(String relativePath, String userName);

    String absolutePathNewDir(String currentPath, String name, String userName);

    String relativePath(String path, String name);

    String renameAbsolutePath(String previousAbsolutePath, String newName);

    String renameRelativePath(String previousPath, String newName);
}
