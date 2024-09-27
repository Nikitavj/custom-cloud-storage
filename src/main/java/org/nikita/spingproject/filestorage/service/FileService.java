package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.file.dto.FileDownloadDto;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.RenameFileRequest;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;

public interface FileService {
    void upload(FileUploadDto dto);
    void delete(FileDto dto);
    FileDownloadDto download(FileDto dto);
    void rename(RenameFileRequest dto);
}
