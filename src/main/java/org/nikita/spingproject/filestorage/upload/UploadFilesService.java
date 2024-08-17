package org.nikita.spingproject.filestorage.upload;

import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;

public interface UploadFilesService {
    void upload(FileUploadDto dto);
}
