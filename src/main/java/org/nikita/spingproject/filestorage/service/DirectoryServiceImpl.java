package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.commons.dto.ObjectStorageDto;
import org.nikita.spingproject.filestorage.commons.ZipArchiver;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.dto.*;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryDownloadException;
import org.nikita.spingproject.filestorage.path.PathUtil;
import org.nikita.spingproject.filestorage.s3manager.S3DirectoryManager;
import org.nikita.spingproject.filestorage.utils.ToObjectStorageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    private static final String ZIP_SUFFIX = ".zip";
    private final S3DirectoryManager s3DirManager;
    private final ZipArchiver zipArchiver;

    @Autowired
    public DirectoryServiceImpl(S3DirectoryManager s3DirManager, ZipArchiver zipArchiver) {
        this.s3DirManager = s3DirManager;
        this.zipArchiver = zipArchiver;
    }

    @Override
    public DownloadDirResponse download(DownloadDirRequest request) {
        Directory directory = s3DirManager.get(request.getPath());
        String nameZipFile = directory.getName() + ZIP_SUFFIX;

        Path zipPath = null;
        try {
            zipPath = Files.createTempFile(directory.getName(), ZIP_SUFFIX);
        } catch (IOException e) {
            throw new DirectoryDownloadException("Error download file");
        }

        try(ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zipArchiver.zipDirectory(directory, directory.getName(), zos);
            FileInputStream fis = new FileInputStream(zipPath.toFile());
            return new DownloadDirResponse(fis, nameZipFile);

        } catch (IOException e) {
            throw new DirectoryDownloadException("Error download file");
        }
    }

    @Override
    public List<ObjectStorageDto> getObjectsOfDir(ObjectsDirRequest dto) {
        Directory directory = s3DirManager.get(dto.getRelativePath());
        List<ObjectStorageDto> objects = new ArrayList<>();

        directory.getDirectories()
                .stream()
                .map(ToObjectStorageMapper::map)
                .forEach(objects::add);

        directory.getFiles()
                .stream()
                .map(ToObjectStorageMapper::map)
                .forEach(objects::add);

        long id = 1;
        for (ObjectStorageDto o : objects) {
            o.setId(id++);
        }
        return objects;
    }

    @Override
    public DirectoryDto create(NewDirRequest dto) {
        String path = PathUtil.createPath(dto.getCurrentPath(), dto.getName());
        s3DirManager.add(
                new Directory(
                        dto.getName(),
                        path));

        return new DirectoryDto(dto.getName(), path);
    }

    @Override
    public void delete(DeleteDirRequest dto) {
        s3DirManager.remove(dto.getRelativePath());
    }

    @Override
    public void rename(RenameDirRequest dto) {
        Directory dir = s3DirManager.get(dto.getPath());
        String targetPath = PathUtil.renamePath(dir.getPath(), dto.getNewName());
        s3DirManager.copy(dir, targetPath, dto.getNewName());
        s3DirManager.remove(dir.getPath());
    }
}
