package org.nikita.spingproject.filestorage.directory.service;

import io.minio.Result;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.commons.EntityStorageDto;
import org.nikita.spingproject.filestorage.directory.DirPathService;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.ItemToEntityStorageMapper;
import org.nikita.spingproject.filestorage.directory.repository.DirectoryRepositoryImpl;
import org.nikita.spingproject.filestorage.directory.s3api.DirectoryS3Api;
import org.nikita.spingproject.filestorage.directory.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    @Autowired
    private DirectoryS3Api directoryS3Api;
    @Autowired
    private DirPathService dirPathService;
    @Autowired
    private RenameDirectoryService renameDirectoryService;
    @Autowired
    private DirectoryRepositoryImpl directRepo;

    @Override
    @SneakyThrows
    public List<EntityStorageDto> listDirectoryObjects(ObjectsDirDto dto) {

        String path = dirPathService.createFullPath(
                dto.getRelativePath(),
                dto.getUserName());

        Iterable<Result<Item>> list = directoryS3Api.getObjectsDirectory(path);
        ArrayList<Item> items = new ArrayList<>();
        for (Result<Item> itemResult : list) {
            Item item = itemResult.get();
            if (!item.isDir()) {
                items.add(item);
            }
        }
        List<EntityStorageDto> entities = items.stream()
                .map(ItemToEntityStorageMapper::map)
                .collect(Collectors.toList());
            return entities;
    }

    @Override
    public DirDto createNewDirectory(NewDirDto dto) {
        String absolutePath = dirPathService.createFullPathNewDir(dto.getCurrentPath(), dto.getName(), dto.getUserName());
        String relativePath = dirPathService.createRelativePath(dto.getCurrentPath(), dto.getName());

        Directory directory = Directory.builder()
                .name(dto.getName())
                .absolutePath(absolutePath)
                .relativePath(relativePath)
                .build();
        directRepo.add(directory);

        return new DirDto(dto.getName(), relativePath);
    }

    @Override
    @SneakyThrows
    public void deleteDirectory(DeleteDirDto dto) {
        String fullPath = dirPathService.createFullPath(
                dto.getRelativePath(),
                dto.getUserName());

        List<DeleteObject> deleteObjects = new LinkedList<>();
        Iterable<Result<Item>> results = directoryS3Api.getObjectsDirectoryRecursive(fullPath);
        for (Result<Item> result : results) {
            deleteObjects.add(
                    new DeleteObject(
                            result.get().objectName()));
        }
        directoryS3Api.deleteObjects(deleteObjects);

        String pathMetaDataObject = dirPathService.createPathMetaDataObject(
                dto.getRelativePath(),
                dto.getUserName());
        directoryS3Api.deleteObject(pathMetaDataObject);
    }

    @Override
    @SneakyThrows
    public void renameDirectory(RenameDirDto dto) {

        renameDirectoryService.rename(dto.getPreviousPath(), dto.getNewName(), dto.getUserName());










    }

    private Map<String, String> createMetaData(String name, String relPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("link", relPath);
        metaData.put("folder", "");
        return metaData;
    }
}
