package org.nikita.spingproject.filestorage.file.service;

import org.nikita.spingproject.filestorage.file.dto.*;

public interface FileService {
    void uploadFile(FileUploadDto dto);
    void deleteFile(FileDto dto);
    FileDownloadDto downloadFile(FileDto dto);
    void renameFile(FileRenameDto dto);
}
