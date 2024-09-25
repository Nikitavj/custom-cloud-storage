package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.directory.dto.*;

import java.io.IOException;
import java.util.List;

public interface DirectoryService {
    DownloadDirResponse downloadDirectory(DownloadDirRequest request) throws IOException;
    List<ObjectStorageDto> getObjectsDirectory(ObjectsDirDto dto);
    DirDto createNewDirectory(NewDirRequest dto);
    void deleteDirectory(DeleteDirRequest dto);
    void renameDirectory(RenameDirRequest dto);
}

