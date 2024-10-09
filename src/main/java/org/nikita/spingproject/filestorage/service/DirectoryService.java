package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.commons.dto.ObjectStorageDto;
import org.nikita.spingproject.filestorage.directory.dto.*;

import java.util.List;

public interface DirectoryService {
    DownloadDirResponse download(DownloadDirRequest request);
    List<ObjectStorageDto> getObjectsOfDir(ObjectsDirRequest dto);
    DirectoryDto create(CreateDirRequest dto);
    void delete(DeleteDirRequest dto);
    void rename(RenameDirRequest dto);
}

