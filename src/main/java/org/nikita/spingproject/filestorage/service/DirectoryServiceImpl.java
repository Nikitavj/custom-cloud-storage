package org.nikita.spingproject.filestorage.service;

import org.apache.commons.io.FileUtils;
import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.s3manager.S3FileManager;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.s3manager.S3DirectoryManager;
import org.nikita.spingproject.filestorage.directory.dto.*;
import org.nikita.spingproject.filestorage.file.File;

import org.nikita.spingproject.filestorage.utils.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    private final PathDirectoryService pathDirectoryService;
    private final S3DirectoryManager s3DirectoryManager;
    private final S3FileManager s3FileManager;

    @Autowired
    public DirectoryServiceImpl(PathDirectoryService pathDirectoryService, S3DirectoryManager s3DirectoryManager, S3FileManager s3FileManager) {
        this.pathDirectoryService = pathDirectoryService;
        this.s3DirectoryManager = s3DirectoryManager;
        this.s3FileManager = s3FileManager;
    }

    @Override
    public DownloadDirResponse download(DownloadDirRequest request) throws IOException {
        Directory directory = s3DirectoryManager.get(request.getPath());

        String nameZipFile = directory.getName() + ".zip";
        Path zipPath = Files.createTempFile(directory.getName(), ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zip(directory, directory.getName(), zos);
            FileInputStream fis = new FileInputStream(zipPath.toFile());
            return new DownloadDirResponse(fis, nameZipFile);
        }
    }

    @Override
    public List<ObjectStorageDto> getObjectsOfDir(ObjectsDirDto dto) {
        Directory directory = s3DirectoryManager.get(dto.getRelativePath());

        List<ObjectStorageDto> objects = new ArrayList<>();
        for (Directory dir : directory.getDirectories()) {
            objects.add(mapDirToObjStorage(dir));
        }
        for (File file : directory.getFiles()) {
            objects.add(mapFileToObjStorage(file));
        }

        long id = 1;
        for (ObjectStorageDto o: objects) {
            o.setId(id++);
        }

        return objects;
    }

    @Override
    public DirDto create(NewDirRequest dto) {
        String relativePath = pathDirectoryService.relativePath(dto.getCurrentPath(), dto.getName());
        Directory directory = Directory.builder()
                .name(dto.getName())
                .path(relativePath)
                .build();
        s3DirectoryManager.add(directory);

        return new DirDto(dto.getName(), relativePath);
    }

    @Override
    public void delete(DeleteDirRequest dto) {

        s3DirectoryManager.remove(dto.getRelativePath());
    }

    @Override
    public void rename(RenameDirRequest dto) {



        String newRelativePath = pathDirectoryService.renameRelativePath(dto.getPreviousPath(), dto.getNewName());

        s3DirectoryManager.rename(
                dto.getPreviousPath(),
                newRelativePath,
                dto.getNewName());
    }

    private ObjectStorageDto mapFileToObjStorage(File file) {
        return ObjectStorageDto.builder()
                .name(file.getName())
                .relativePath(file.getPath())
                .date(DateFormatUtil.format(file.getDate()))
                .size(FileUtils.byteCountToDisplaySize(file.getSize()))
                .isDir(false)
                .build();
    }

    private ObjectStorageDto mapDirToObjStorage(Directory dir) {
        return ObjectStorageDto.builder()
                .name(dir.getName())
                .relativePath(dir.getPath())
                .date(DateFormatUtil.format(dir.getDate()))
                .isDir(true)
                .build();
    }

    private void zip(Directory dir, String prefixPath, ZipOutputStream zos) throws IOException {
        List<File> files = dir.getFiles();
        List<Directory> directories = dir.getDirectories();

        for (Directory directory : directories) {
            String basePath = prefixPath + "/" + directory.getName();
            zip(directory, basePath, zos);
        }

        for (File file : files) {
            File fileWithIS = s3FileManager.get(file.getPath());

            try (InputStream is = fileWithIS.getInputStream()) {
                ZipEntry zipEntry = new ZipEntry(prefixPath + "/" + file.getName());
                zos.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    zos.write(bytes);
                }
                zos.closeEntry();
            }
        }
    }
}
