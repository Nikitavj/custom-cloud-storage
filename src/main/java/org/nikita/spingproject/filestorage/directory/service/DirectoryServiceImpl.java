package org.nikita.spingproject.filestorage.directory.service;

import org.apache.commons.io.FileUtils;
import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.dao.FileDao;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.dao.DirectoryDao;
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
    private final DirectoryDao directoryDao;
    private final FileDao fileDao;

    @Autowired
    public DirectoryServiceImpl(PathDirectoryService pathDirectoryService, DirectoryDao directoryDao, FileDao fileDao) {
        this.pathDirectoryService = pathDirectoryService;
        this.directoryDao = directoryDao;
        this.fileDao = fileDao;
    }

    @Override
    public DownloadDirResponse downloadDirectory(DownloadDirRequest request) throws IOException {
        Directory directory = directoryDao.get(request.getPath());

        String nameZipFile = directory.getName() + ".zip";
        Path zipPath = Files.createTempFile(directory.getName(), ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zipDirectory(directory, directory.getName(), zos);
            FileInputStream fis = new FileInputStream(zipPath.toFile());
            return new DownloadDirResponse(fis, nameZipFile);
        }
    }

    @Override
    public List<ObjectStorageDto> getObjectsDirectory(ObjectsDirDto dto) {
        Directory directory = directoryDao.get(dto.getRelativePath());

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
    public DirDto createNewDirectory(NewDirRequest dto) {
        String relativePath = pathDirectoryService.relativePath(dto.getCurrentPath(), dto.getName());
        Directory directory = Directory.builder()
                .name(dto.getName())
                .relativePath(relativePath)
                .build();
        directoryDao.add(directory);

        return new DirDto(dto.getName(), relativePath);
    }

    @Override
    public void deleteDirectory(DeleteDirRequest dto) {

        directoryDao.remove(dto.getRelativePath());
    }

    @Override
    public void renameDirectory(RenameDirRequest dto) {
        String newRelativePath = pathDirectoryService.renameRelativePath(dto.getPreviousPath(), dto.getNewName());

        directoryDao.rename(
                dto.getPreviousPath(),
                newRelativePath,
                dto.getNewName());
    }

    private ObjectStorageDto mapFileToObjStorage(File file) {
        return ObjectStorageDto.builder()
                .name(file.getName())
                .relativePath(file.getRelativePath())
                .date(DateFormatUtil.format(file.getDate()))
                .size(FileUtils.byteCountToDisplaySize(file.getSize()))
                .isDir(false)
                .build();
    }

    private ObjectStorageDto mapDirToObjStorage(Directory dir) {
        return ObjectStorageDto.builder()
                .name(dir.getName())
                .relativePath(dir.getRelativePath())
                .date(DateFormatUtil.format(dir.getDate()))
                .isDir(true)
                .build();
    }

    private void zipDirectory(Directory dir, String prefixPath, ZipOutputStream zos) throws IOException {
        List<File> files = dir.getFiles();
        List<Directory> directories = dir.getDirectories();

        for (Directory directory : directories) {
            String basePath = prefixPath + "/" + directory.getName();
            zipDirectory(directory, basePath, zos);
        }

        for (File file : files) {
            File fileWithIS = fileDao.get(file.getAbsolutePath());

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
