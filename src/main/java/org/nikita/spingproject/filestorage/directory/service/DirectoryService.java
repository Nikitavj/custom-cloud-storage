package org.nikita.spingproject.filestorage.directory.service;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.directory.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DirectoryService {

    List<ObjectStorageDto> listObjectsDirectory(ObjectsDirDto dto);

    DirDto createNewDirectory(NewDirDto dto);

    void deleteDirectory(DeleteDirDto dto);

    void renameDirectory(RenameDirDto dto);
}

