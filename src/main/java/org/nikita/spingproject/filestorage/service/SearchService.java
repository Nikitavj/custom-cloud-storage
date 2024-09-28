package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.commons.dto.ObjectStorageDto;
import org.nikita.spingproject.filestorage.commons.dto.SearchRequest;

import java.util.List;

public interface SearchService {
    List<ObjectStorageDto> search(SearchRequest dto);
}
