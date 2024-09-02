package org.nikita.spingproject.filestorage.file.service;

public interface PathFileService {
    String renameAbsolutePath(String oldAsolurtePath, String newName);

    String renameLink(String oldAbsolutePath, String newNameFile);

    String createRelativePath(String path, String name);

    String createAbsolutePath(String path);

    String createAbsolutePathNewFile(String path, String nameFile);
}
