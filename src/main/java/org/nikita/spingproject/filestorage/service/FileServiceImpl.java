package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.s3manager.S3FileManager;
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
    private final PathFileService pathFileService;
    private final S3FileManager s3FileManager;

    @Autowired
    public FileServiceImpl(PathFileService pathFileService, S3FileManager s3FileManager) {
        this.pathFileService = pathFileService;
        this.s3FileManager = s3FileManager;
    }

    @Override
    public void upload(FileUploadDto dto) {
        try {
            String relativePath = pathFileService
                    .createRelativePath(dto.getPath(), dto.getName());

            s3FileManager.add(File.builder()
                    .path(relativePath)
                    .name(dto.getName())
                    .inputStream(dto.getInputStream())
                    .build());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(FileDto dto) {
        s3FileManager.remove(dto.getPath());
    }

    @Override
    public FileDownloadDto download(FileDto dto) {

        File file = s3FileManager.get(dto.getPath());
        return new FileDownloadDto(file.getInputStream(), file.getName());
    }

    @Override
    public void rename(RenameFileRequest dto) {
        try {
            String newRelativePath = pathFileService.renameRelPath(dto.getPath(), dto.getNewName());
            s3FileManager.rename(
                    dto.getPath(),
                    newRelativePath,
                    dto.getNewName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
