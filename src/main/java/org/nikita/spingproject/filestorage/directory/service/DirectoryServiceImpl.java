package org.nikita.spingproject.filestorage.directory.service;

import io.minio.Result;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.nikita.spingproject.filestorage.commons.EntityStorage;
import org.nikita.spingproject.filestorage.directory.Folder;
import org.nikita.spingproject.filestorage.directory.ItemToEntityStorageMapper;
import org.nikita.spingproject.filestorage.directory.dao.DirectoryDao;
import org.nikita.spingproject.filestorage.directory.dto.FolderDto;
import org.nikita.spingproject.filestorage.directory.dto.ObjectsDirectoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    @Autowired
    private DirectoryDao directoryDao;
    @Autowired
    private UserRepository userRepository;

    @Override
    @SneakyThrows
    public ObjectsDirectoryDto listDirectoryObjects(FolderDto dto) {
        String path = createPathFolder(
                dto.getPath(),
                dto.getUserName());

        Iterable<Result<Item>> list = directoryDao.getObjectsDirectory(path);
        ArrayList<Item> items = new ArrayList<>();
        for (Result<Item> itemResult : list) {
            Item item = itemResult.get();
            if (!item.isDir()) {
                items.add(item);
            }
        }
        List<EntityStorage> entities = items.stream()
                .map(ItemToEntityStorageMapper::map)
                .collect(Collectors.toList());
        return new ObjectsDirectoryDto(entities);
    }

    @Override
    public Folder createNewFolder(FolderDto dto) {
        String path = createPathNewFolder(
                dto.getPath(),
                dto.getName(),
                dto.getUserName());
        String link = createLink(dto.getPath(), dto.getName());
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", dto.getName());
        metaData.put("link", link);
        metaData.put("folder", "");
        directoryDao.createFolder(metaData, path);
        return new Folder(
                dto.getName(),
                link,
                true);
    }

    @Override
    @SneakyThrows
    public void deleteFolder(FolderDto dto) {
        String path = createPathFolder(
                dto.getPath(),
                dto.getUserName());

        Iterable<Result<Item>> list = directoryDao.getObjectsDirectoryRecursive(path);
        List<DeleteObject> deletedList = new LinkedList<>();
        for (Result<Item> resItem : list) {
            deletedList.add(
                    new DeleteObject(
                            resItem.get().objectName()));
        }
        directoryDao.deleteObjects(deletedList);
        directoryDao.deleteObject(createPathMetaObjectFolder(dto.getPath(), dto.getUserName()));
    }

    private String createLink(String path, String name) {
        if (path == null || path.equals("/") || path.isBlank()) {
            return name;
        } else {
            return String.format("%s/%s", path, name);
        }
    }

    private String createPathNewFolder(String currentPath, String nameFolder, String nameUser) {
        String path = createPathFolder(currentPath, nameUser);

        return String.format("%s%s_",
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
            return String.format("%s/", createRootPathForUser(nameUser));
        } else {
            return String.format("%s/%s/",
                    createRootPathForUser(nameUser),
                    pathFolder);
        }
    }

    private String createPathMetaObjectFolder(String pathFolder, String nameUser) {
        if (pathFolder == null
                || pathFolder.isBlank()
                || pathFolder.equals("/")) {
            return String.format("%s_", createRootPathForUser(nameUser));
        } else {
            return String.format("%s/%s_",
                    createRootPathForUser(nameUser),
                    pathFolder);
        }
    }
}
