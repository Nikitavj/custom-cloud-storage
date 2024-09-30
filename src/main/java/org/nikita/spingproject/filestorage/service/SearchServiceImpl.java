package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.commons.dto.ObjectStorageDto;
import org.nikita.spingproject.filestorage.commons.dto.SearchRequest;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.path.PathUtil;
import org.nikita.spingproject.filestorage.s3manager.S3DirectoryManager;
import org.nikita.spingproject.filestorage.s3manager.S3DirectoryManagerImpl;
import org.nikita.spingproject.filestorage.utils.ToObjectStorageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    private final S3DirectoryManager s3DirectoryManager;

    @Autowired
    public SearchServiceImpl(S3DirectoryManagerImpl directoryDao) {
        this.s3DirectoryManager = directoryDao;
    }

    @Override
    public List<ObjectStorageDto> search(SearchRequest dto) {
        List<ObjectStorageDto> findObjects = new LinkedList<>();
        Directory rootDir = s3DirectoryManager.getRoot();
        searchRecursiveInDir(rootDir, dto.getName(), findObjects);

        return findObjects;
    }

    private void searchRecursiveInDir(Directory directory, String searchName, List<ObjectStorageDto> list) {
        for (Directory dir : directory.getDirectories()) {
            searchRecursiveInDir(dir, searchName, list);

            if (dir.getName().contains(searchName)) {
                dir.setPath(PathUtil.extractDirectoryPath(dir.getPath()));
                list.add(ToObjectStorageMapper.map(dir));
            }
        }
        for (File file : directory.getFiles()) {
            if (file.getName().contains(searchName)) {
                file.setPath(PathUtil.extractDirectoryPath(file.getPath()));
                list.add(ToObjectStorageMapper.map(file));
            }
        }
    }
}