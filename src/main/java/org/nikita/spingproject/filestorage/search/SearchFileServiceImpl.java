package org.nikita.spingproject.filestorage.search;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.directory.dao.DirectoryDao;
import org.nikita.spingproject.filestorage.directory.dao.DirectoryDaoImpl;
import org.nikita.spingproject.filestorage.directory.service.PathDirectoryService;
import org.nikita.spingproject.filestorage.directory.service.PathDirectoryServiceImpl;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.utils.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchFileServiceImpl implements SearchFileService {
    private PathDirectoryService pathDirectoryService;
    private DirectoryDao directoryDao;

    @Autowired
    public SearchFileServiceImpl(PathDirectoryServiceImpl pathDirectoryService, DirectoryDaoImpl directoryDao) {
        this.pathDirectoryService = pathDirectoryService;
        this.directoryDao = directoryDao;
    }

    @Override
    public List<ObjectStorageDto> search(SearchFileDto dto) {
        List<ObjectStorageDto> findObjects = new ArrayList<>();
        String rootPath = pathDirectoryService.rootPathForUser();
        Directory directory = directoryDao.getRecursive(rootPath);

        for (Directory dir: directory.getDirectories()) {
            if(dir.getName().contains(dto.getName())) {
                findObjects.add(mapDirToObjStorage(dir));
            }
        }
        for (File file: directory.getFiles()) {
            if(file.getName().contains(dto.getName())) {
                findObjects.add(mapFileToObjStorage(file));
            }
        }
        return findObjects;
    }

    private ObjectStorageDto mapFileToObjStorage(File file) {
        String link = linkObjectIncludeInDirectory(file.getRelativePath());
        return ObjectStorageDto.builder()
                .name(file.getName())
                .relativePath(link)
                .size(FileUtils.byteCountToDisplaySize(file.getSize()))
                .date(DateFormatUtil.format(file.getDate()))
                .isDir(false)
                .build();
    }

    private ObjectStorageDto mapDirToObjStorage(Directory directory) {
        String link = linkObjectIncludeInDirectory(directory.getRelativePath());
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
