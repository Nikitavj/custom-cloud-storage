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
import org.nikita.spingproject.filestorage.file.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    @Autowired
    private DirectoryDao directoryDao;
    @Autowired
    private UserRepository userRepository;


    @Override
    public ObjectsDirectoryDto listDirectoryObjects(FolderDto dto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Folder folder = new Folder();
        folder.setPath(createPathFolder(
                dto.getPath(),
                dto.getUserName()));

        Iterable<Result<Item>> list = directoryDao.getObjectsDirectory(folder);

        ObjectsDirectoryDto objectsDir = new ObjectsDirectoryDto();
        for (Result<Item> itemResult : list) {
            Item item = itemResult.get();
            if (item.isDir()) {
                objectsDir.getFolders()
                        .add(ItemToEntityStorageMapper.mappToFolder(item));
            } else {
                objectsDir.getFiles()
                        .add(ItemToEntityStorageMapper.mappToFile(item));
            }
        }
        return objectsDir;
    }

    @Override
    public String createNewFolder(FolderDto dto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String pathFolder = createPathNewFolder(
                dto.getPath(),
                dto.getName(),
                dto.getUserName());
        directoryDao.createFolder(new Folder(
                pathFolder,
                dto.getName()));
        return pathFolder;
    }

    private String createPathNewFolder(String currentPath, String nameFolder, String nameUser) {
        if (currentPath.equals("/")) {
            currentPath = "";
        }
        return String.format("%s/%s/%s/",
                createRootPathForUser(nameUser),
                currentPath,
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
}
