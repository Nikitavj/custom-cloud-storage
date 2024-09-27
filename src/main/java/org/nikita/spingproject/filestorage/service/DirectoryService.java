package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.directory.dto.*;

import java.io.IOException;
import java.util.List;

public interface DirectoryService {
    DownloadDirResponse download(DownloadDirRequest request) throws IOException;
    List<ObjectStorageDto> getObjectsOfDir(ObjectsDirDto dto);
    DirDto create(NewDirRequest dto);
    void delete(DeleteDirRequest dto);
    void rename(RenameDirRequest dto);
}

