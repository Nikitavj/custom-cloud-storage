package org.nikita.spingproject.filestorage.service;

public interface PathDirectoryService {

    String createPath(String path, String name);

    String renamePath(String previousPath, String newName);
}
