package org.nikita.spingproject.filestorage.commons.searchfiles;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.commons.ObjectStorageDto;
import org.nikita.spingproject.filestorage.dao.FileDao;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.dao.DirectoryDao;
import org.nikita.spingproject.filestorage.dao.DirectoryDaoImpl;
import org.nikita.spingproject.filestorage.file.File;

import org.nikita.spingproject.filestorage.utils.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchFileServiceImpl implements SearchFileService {
    private final DirectoryDao directoryDao;
    private  final FileDao fileDao;

    @Autowired
    public SearchFileServiceImpl(DirectoryDaoImpl directoryDao, FileDao fileDao) {
        this.directoryDao = directoryDao;
        this.fileDao = fileDao;
    }

    @Override
    public List<ObjectStorageDto> search(SearchRequest dto) {
        List<ObjectStorageDto> findObjects = new ArrayList<>();
        List<Directory> directories = directoryDao.getAll();
        List<File> files = fileDao.getAll();

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
