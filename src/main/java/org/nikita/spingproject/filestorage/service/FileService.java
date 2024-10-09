package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.file.dto.*;

public interface FileService {
    void upload(UploadFileRequest dto);
    void delete(DeleteFileRequest dto);
    DownloadFileResponse download(DownloadFileRequest dto);
    void rename(RenameFileRequest dto);

    void exist(ExistFileRequest req);
}
