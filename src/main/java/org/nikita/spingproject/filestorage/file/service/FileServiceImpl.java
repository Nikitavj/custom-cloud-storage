package org.nikita.spingproject.filestorage.file.service;

import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.file.dao.FileDao;
import org.nikita.spingproject.filestorage.file.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class FileServiceImpl implements FileService {
    private PathFileService pathFileService;
    private FileDao fileDao;

    @Autowired
    public FileServiceImpl(PathFileService pathFileService, FileDao fileDao) {
        this.pathFileService = pathFileService;
        this.fileDao = fileDao;
    }

    @Override
    public void uploadFile(FileUploadDto dto) {
        try {
            String absolutePath = pathFileService
                    .createAbsolutePathNewFile(
                            dto.getPath(),
                            dto.getName());

            String relativePath = pathFileService
                    .createRelativePath(dto.getPath(), dto.getName());

            fileDao.add(File.builder()
                    .absolutePath(absolutePath)
                    .relativePath(relativePath)
                    .name(dto.getName())
                    .inputStream(dto.getInputStream())
                    .build());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteFile(FileDto dto) {
        String absolutePath = pathFileService.
                createAbsolutePath(dto.getPath());

        fileDao.remove(absolutePath);
    }

    @Override
    public FileDownloadDto downloadFile(FileDto dto) {
        String absolutePath = pathFileService
                .createAbsolutePath(
                        dto.getPath());
        File file = fileDao.get(absolutePath);
        return new FileDownloadDto(file.getInputStream(), file.getName());
    }

    @Override
    public void renameFile(FileRenameDto dto) {
        try {
            String path = pathFileService
                    .createAbsolutePath(dto.getPath());
            String newPath = pathFileService
                    .renameAbsolutePath(
                            path,
                            dto.getNewName());
            String relativeNewPath = pathFileService
                    .renameRelPath(
                            path,
                            dto.getNewName());
            fileDao.rename(
                    path,
                    newPath,
                    relativeNewPath,
                    dto.getNewName());
            fileDao.remove(path);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
