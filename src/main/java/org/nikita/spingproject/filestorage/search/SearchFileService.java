package org.nikita.spingproject.filestorage.search;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;

import java.util.List;

public interface SearchFileService {
    List<ObjectStorageDto> search(SearchFileDto dto);
}
