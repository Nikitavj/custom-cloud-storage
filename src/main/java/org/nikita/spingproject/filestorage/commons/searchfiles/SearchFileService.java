package org.nikita.spingproject.filestorage.commons.searchfiles;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;

import java.util.List;

public interface SearchFileService {
    List<ObjectStorageDto> search(SearchRequest dto);
}
