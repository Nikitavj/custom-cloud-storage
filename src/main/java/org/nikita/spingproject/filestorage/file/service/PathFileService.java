package org.nikita.spingproject.filestorage.file.service;

import java.io.UnsupportedEncodingException;

public interface PathFileService {
    String renameAbsolutePath(String oldAsolurtePath, String newName) throws UnsupportedEncodingException;

    String renameRelPath(String oldAbsolutePath, String newNameFile) throws UnsupportedEncodingException;

    String createRelativePath(String path, String name) throws UnsupportedEncodingException;

    String createAbsolutePath(String path);

    String createAbsolutePathNewFile(String path, String nameFile) throws UnsupportedEncodingException;
}
