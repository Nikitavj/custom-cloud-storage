package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.file.dto.UploadFilesRequest;

import java.io.IOException;

public interface UploadFilesService {
    void upload(UploadFilesRequest dto) throws IOException;
}
