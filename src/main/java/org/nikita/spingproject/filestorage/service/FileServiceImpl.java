package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.dao.FileDao;
import org.nikita.spingproject.filestorage.file.File;

import org.nikita.spingproject.filestorage.file.dto.FileDownloadDto;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.RenameFileRequest;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
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
            String relativePath = pathFileService
                    .createRelativePath(dto.getPath(), dto.getName());

            fileDao.add(File.builder()
                    .path(relativePath)
                    .name(dto.getName())
                    .inputStream(dto.getInputStream())
                    .build());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteFile(FileDto dto) {
        fileDao.remove(dto.getPath());
    }

    @Override
    public FileDownloadDto downloadFile(FileDto dto) {

        File file = fileDao.get(dto.getPath());
        return new FileDownloadDto(file.getInputStream(), file.getName());
    }

    @Override
    public void renameFile(RenameFileRequest dto) {
        try {
            String newRelativePath = pathFileService.renameRelPath(dto.getPath(), dto.getNewName());
            fileDao.rename(
                    dto.getPath(),
                    newRelativePath,
                    dto.getNewName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
