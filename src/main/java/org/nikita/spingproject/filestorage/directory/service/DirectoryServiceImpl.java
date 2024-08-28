package org.nikita.spingproject.filestorage.directory.service;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.dao.DirectoryDao;
import org.nikita.spingproject.filestorage.directory.dto.*;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.file.dao.FileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    private PathDirectoryService pathDirectoryService;
    private DirectoryDao directoryDao;
    private FileDao fileDao;

    @Autowired
    public DirectoryServiceImpl(PathDirectoryService pathDirectoryService, DirectoryDao directoryDao, FileDao fileDao) {
        this.pathDirectoryService = pathDirectoryService;
        this.directoryDao = directoryDao;
        this.fileDao = fileDao;
    }

    @Override
    public DirDownloadResponse downloadDirectory(DirDownloadRequest request) throws IOException {
        String absolutePath = pathDirectoryService.absolutPath(
                request.getPath(),
                request.getUserName());
        Directory directory = directoryDao.get(absolutePath);

        String nameZipFile = directory.getName() + ".zip";
        Path zipPath = Files.createTempFile(directory.getName(), ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zipDirectory(directory, directory.getName(), zos);
            FileInputStream fis = new FileInputStream(zipPath.toFile());
            return new DirDownloadResponse(fis, nameZipFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void zipDirectory(Directory dir, String prefixPath, ZipOutputStream zos) throws IOException {
        List<File> files = dir.getFiles();
        List<Directory> directories = dir.getDirectories();

        for (Directory directory: directories) {
            String basePath = prefixPath + "/" + directory.getName();
            zipDirectory(directory, basePath, zos);
        }

        for (File file: files) {
            File file1 = fileDao.get(file.getAbsolutePath());
            InputStream is = file1.getInputStream();
            ZipEntry zipEntry = new ZipEntry(prefixPath + "/" + file.getName());
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) >= 0) {
                zos.write(bytes);
            }
        }
    }

    @Override
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
    public void deleteDirectory(DeleteDirDto dto) {
        String absolutePath = pathDirectoryService.absolutPath(dto.getRelativePath(), dto.getUserName());
        directoryDao.remove(absolutePath);
    }

    @Override
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
