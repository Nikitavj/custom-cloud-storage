package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.commons.SearchRequest;

import java.util.List;

public interface SearchFileService {
    List<ObjectStorageDto> search(SearchRequest dto);
}
