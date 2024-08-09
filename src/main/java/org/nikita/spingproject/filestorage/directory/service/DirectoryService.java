package org.nikita.spingproject.filestorage.directory.service;

import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.commons.EntityStorageDto;
import org.nikita.spingproject.filestorage.directory.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DirectoryService {

    List<EntityStorageDto> listDirectoryObjects(ObjectsDirDto dto);

    DirDto createNewDirectory(NewDirDto dto);

    void deleteDirectory(DeleteDirDto dto);

    void renameDirectory(RenameDirDto dto);
}

