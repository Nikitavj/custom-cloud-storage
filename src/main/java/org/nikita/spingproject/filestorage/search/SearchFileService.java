package org.nikita.spingproject.filestorage.search;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.search.dto.SearchFileDto;

import java.util.List;

public interface SearchFileService {
    List<ObjectStorageDto> search(SearchFileDto dto);
}
