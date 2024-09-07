package org.nikita.spingproject.filestorage.upload;

import org.nikita.spingproject.filestorage.file.dto.FilesUploadDto;

import java.io.IOException;

public interface UploadFilesService {
    void upload(FilesUploadDto dto) throws IOException;
}
