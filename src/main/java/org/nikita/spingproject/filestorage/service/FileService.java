package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.file.dto.FileDownloadDto;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;

public interface FileService {
    void uploadFile(FileUploadDto dto);

    void deleteFile(FileDto dto);

    FileDownloadDto downloadFile(FileDto dto);
}
