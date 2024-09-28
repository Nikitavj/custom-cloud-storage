package org.nikita.spingproject.filestorage.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.commons.dto.ObjectStorageDto;
import org.nikita.spingproject.filestorage.commons.dto.SearchRequest;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.path.PathUtil;
import org.nikita.spingproject.filestorage.s3manager.S3DirectoryManager;
import org.nikita.spingproject.filestorage.s3manager.S3DirectoryManagerImpl;
import org.nikita.spingproject.filestorage.s3manager.S3FileManager;
import org.nikita.spingproject.filestorage.utils.DateFormatUtil;
import org.nikita.spingproject.filestorage.utils.ToObjectStorageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    private final S3DirectoryManager s3DirectoryManager;
    private  final S3FileManager s3FileManager;

    @Autowired
    public SearchServiceImpl(S3DirectoryManagerImpl directoryDao, S3FileManager s3FileManager) {
        this.s3DirectoryManager = directoryDao;
        this.s3FileManager = s3FileManager;
    }

    @Override
    public List<ObjectStorageDto> search(SearchRequest dto) {
        List<ObjectStorageDto> findObjects = new ArrayList<>();

        Directory rootDir = s3DirectoryManager.get();








        List<Directory> directories = s3DirectoryManager.getAll();
        List<File> files = s3FileManager.getAll();





        for (Directory dir: directories) {
            if(dir.getName().contains(dto.getName())) {
                String link = PathUtil.extractDirectoryPath(dir.getPath());
                dir.setPath(link);
                findObjects.add(ToObjectStorageMapper.map(dir));
            }
        }
        for (File file: files) {
            if(file.getName().contains(dto.getName())) {
                String link = PathUtil.extractDirectoryPath(file.getPath());
                file.setPath(link);
                findObjects.add(ToObjectStorageMapper.map(file));
            }
        }
        return findObjects;



    }





}
