package org.nikita.spingproject.filestorage.directory.service;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.directory.Folder;
import org.nikita.spingproject.filestorage.directory.dto.FolderDto;
import org.nikita.spingproject.filestorage.directory.dto.ObjectsDirectoryDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public interface DirectoryService {

    ObjectsDirectoryDto listDirectoryObjects(FolderDto dto) throws InsufficientDataException, ServerException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    Folder createNewFolder(FolderDto dto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    void deleteFolder(FolderDto dto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

}

