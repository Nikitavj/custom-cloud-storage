package org.nikita.spingproject.filestorage.directory.service;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.directory.dto.*;

import java.io.IOException;
import java.util.List;

public interface DirectoryService {
    DirDownloadResponse downloadDirectory(DirDownloadRequest request) throws IOException;
    List<ObjectStorageDto> getObjectsDirectory(ObjectsDirDto dto);
    DirDto createNewDirectory(NewDirDto dto);
    void deleteDirectory(DeleteDirDto dto);
    void renameDirectory(RenameDirDto dto);
}

