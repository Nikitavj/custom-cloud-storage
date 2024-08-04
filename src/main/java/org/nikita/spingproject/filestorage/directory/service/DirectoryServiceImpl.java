package org.nikita.spingproject.filestorage.directory.service;

import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.persistence.EntityNotFoundException;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.nikita.spingproject.filestorage.directory.Folder;
import org.nikita.spingproject.filestorage.directory.ItemToEntityStorageMapper;
import org.nikita.spingproject.filestorage.directory.dao.DirectoryDao;
import org.nikita.spingproject.filestorage.directory.dto.FolderDto;
import org.nikita.spingproject.filestorage.directory.dto.ObjectsDirectoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    @Autowired
    private DirectoryDao directoryDao;
    @Autowired
    private UserRepository userRepository;

    @Override
    public ObjectsDirectoryDto listDirectoryObjects(FolderDto dto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ObjectsDirectoryDto objectsDir = new ObjectsDirectoryDto();
        String path = createPathFolder(
                dto.getPath(),
                dto.getUserName());

        Iterable<Result<Item>> list = directoryDao.getObjectsDirectory(path);
        for (Result<Item> itemResult : list) {
            Item item = itemResult.get();
//            if (item.isDir()) {
//                Map<String, String> metadata = item.userMetadata();
//
//                objectsDir.addFolder(
//                        ItemToEntityStorageMapper.mappToFolder(item));
//            } else {
            if (!item.isDir()) {
                objectsDir.addFile(
                        ItemToEntityStorageMapper.mappToFile(item));
            }
        }
        return objectsDir;
    }

@Override
public Folder createNewFolder(FolderDto dto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
    String path = createPathNewFolder(
            dto.getPath(),
            dto.getName(),
            dto.getUserName());

    Map<String, String> metaData = new HashMap<>();
    metaData.put("name", dto.getName());
    metaData.put("link", createLink(dto.getPath(), dto.getName()));

    directoryDao.createFolder(metaData, path);

    return new Folder(
            dto.getName(),
            createLinkFolderFromPath(path));
}

private String createLink(String path, String name) {
    if (path == null || path.equals("/") || path.isBlank()) {
        return name;
    } else {
        return String.format("%s/%s", path, name);
    }
}

@Override
public void deleteFolder(FolderDto dto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
    Folder folder = new Folder();
    folder.setLink(createPathFolder(
            dto.getPath(),
            dto.getUserName()));

    directoryDao.deleteFolder(folder);
}

private String createPathNewFolder(String currentPath, String nameFolder, String nameUser) {
    String path = createPathFolder(currentPath, nameUser);

    return String.format("%s%s",
            path,
            nameFolder);
}

private String createRootPathForUser(String userName) {
    User user = userRepository.findUserByEmail(userName)
            .orElseThrow(() -> new EntityNotFoundException("User " + userName + "not exist"));
    return String.format("user-%s-files", user.getId());
}

private String createPathFolder(String pathFolder, String nameUser) {
    if (pathFolder == null
            || pathFolder.isBlank()
            || pathFolder.equals("/")) {
        pathFolder = "";
        return String.format("%s/%s",
                createRootPathForUser(nameUser),
                pathFolder);
    } else {
        return String.format("%s/%s/",
                createRootPathForUser(nameUser),
                pathFolder);
    }
}

private String createLinkFolderFromPath(String path) {
    String[] paths = path.split("/", 2);
    String pathWithSlesh = paths[paths.length - 1];
    return pathWithSlesh.substring(0, pathWithSlesh.length() - 1);
}
}
