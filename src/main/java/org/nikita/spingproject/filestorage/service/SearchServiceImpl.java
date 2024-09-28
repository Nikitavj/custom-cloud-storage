package org.nikita.spingproject.filestorage.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.commons.dto.ObjectStorageDto;
import org.nikita.spingproject.filestorage.commons.dto.SearchRequest;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.file.File;
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

        List<Directory> directories = s3DirectoryManager.getAll();
        List<File> files = s3FileManager.getAll();

        for (Directory dir: directories) {
            if(dir.getName().contains(dto.getName())) {
                findObjects.add(mapDirToObjStorage(dir));
            }
        }
        for (File file: files) {
            if(file.getName().contains(dto.getName())) {
                findObjects.add(mapFileToObjStorage(file));
            }
        }
        return findObjects;
    }

    private ObjectStorageDto mapFileToObjStorage(File file) {
        String link = linkObjectIncludeInDirectory(file.getPath());

        return ToObjectStorageMapper.mapFileToObjStorage(file);
//        return ObjectStorageDto.builder()
//                .name(file.getName())
//                .relativePath(link)
//                .size(FileUtils.byteCountToDisplaySize(file.getSize()))
//                .date(DateFormatUtil.format(file.getDate()))
//                .isDir(false)
//                .build();
    }

    private ObjectStorageDto mapDirToObjStorage(Directory directory) {
        String link = linkObjectIncludeInDirectory(directory.getPath());
        return ObjectStorageDto.builder()
                .name(directory.getName())
                .relativePath(link)
                .date(DateFormatUtil.format(directory.getDate()))
                .isDir(true)
                .build();
    }

    private String linkObjectIncludeInDirectory(String relativePath) {
        if(relativePath.contains("/")) {
            return StringUtils.substringBeforeLast(relativePath, "/");
        } else {
            return "";
        }
    }
}
