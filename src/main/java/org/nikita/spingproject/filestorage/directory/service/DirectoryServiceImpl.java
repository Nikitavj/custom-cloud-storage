package org.nikita.spingproject.filestorage.directory.service;

import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.PathDirectoryService;
import org.nikita.spingproject.filestorage.directory.PathDirectoryServiceImpl;
import org.nikita.spingproject.filestorage.directory.dto.*;
import org.nikita.spingproject.filestorage.directory.dao.DirectoryDao;
import org.nikita.spingproject.filestorage.file.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    private PathDirectoryService pathDirectoryService;
    private DirectoryDao directoryDao;

    @Autowired
    public DirectoryServiceImpl(PathDirectoryServiceImpl pathDirectoryService, DirectoryDao directoryDao) {
        this.pathDirectoryService = pathDirectoryService;
        this.directoryDao = directoryDao;
    }

    @Override
    @SneakyThrows
    public List<ObjectStorageDto> getObjectsDirectory(ObjectsDirDto dto) {
        String absolutePath = pathDirectoryService.absolutPath(dto.getRelativePath(), dto.getUserName());
        Directory directory = directoryDao.get(absolutePath);

        List<ObjectStorageDto> objects = new ArrayList<>();
        for (Directory dir: directory.getDirectories()) {
            objects.add(mapDirToObjStorage(dir));
        }
        for (File file: directory.getFiles()) {
            objects.add(mapFileToObjStorage(file));
        }
        return objects;
    }

    @Override
    public DirDto createNewDirectory(NewDirDto dto) {
        String absolutePath = pathDirectoryService.absolutePathNewDir(dto.getCurrentPath(), dto.getName(), dto.getUserName());
        String relativePath = pathDirectoryService.relativePath(dto.getCurrentPath(), dto.getName());

        Directory directory = Directory.builder()
                .name(dto.getName())
                .absolutePath(absolutePath)
                .relativePath(relativePath)
                .build();
        directoryDao.add(directory);

        return new DirDto(dto.getName(), relativePath);
    }

    @Override
    @SneakyThrows
    public void deleteDirectory(DeleteDirDto dto) {
        String absolutePath = pathDirectoryService.absolutPath(dto.getRelativePath(), dto.getUserName());
        directoryDao.remove(absolutePath);
    }

    @Override
    @SneakyThrows
    public void renameDirectory(RenameDirDto dto) {
        String previousAbsolutePath = pathDirectoryService.absolutPath(dto.getPreviousPath(), dto.getUserName());
        String newAbsolutePath = pathDirectoryService.renameAbsolutePath(previousAbsolutePath, dto.getNewName());
        String newRelativePath = pathDirectoryService.renameRelativePath(dto.getPreviousPath(), dto.getNewName());
        directoryDao.rename(
                previousAbsolutePath,
                newAbsolutePath,
                dto.getPreviousPath(),
                newRelativePath,
                dto.getNewName());
    }

    private ObjectStorageDto mapFileToObjStorage(File file) {
        return ObjectStorageDto.builder()
                .name(file.getName())
                .relativePath(file.getRelativePath())
                .isDir(false)
                .build();
    }

    private ObjectStorageDto mapDirToObjStorage(Directory directory) {
        return ObjectStorageDto.builder()
                .name(directory.getName())
                .relativePath(directory.getRelativePath())
                .isDir(true)
                .build();
    }
}
