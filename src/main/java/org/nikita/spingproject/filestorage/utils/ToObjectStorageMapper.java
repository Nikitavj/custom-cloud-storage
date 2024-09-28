package org.nikita.spingproject.filestorage.utils;

import org.apache.commons.io.FileUtils;
import org.nikita.spingproject.filestorage.commons.dto.ObjectStorageDto;
import org.nikita.spingproject.filestorage.directory.Directory;
import org.nikita.spingproject.filestorage.file.File;

public class ToObjectStorageMapper {
    public static ObjectStorageDto mapFileToObjStorage(File file) {
        return ObjectStorageDto.builder()
                .name(file.getName())
                .relativePath(file.getPath())
                .date(DateFormatUtil.format(file.getDate()))
                .size(FileUtils.byteCountToDisplaySize(file.getSize()))
                .isDir(false)
                .build();
    }

    public static ObjectStorageDto mapDirToObjStorage(Directory dir) {
        return ObjectStorageDto.builder()
                .name(dir.getName())
                .relativePath(dir.getPath())
                .date(DateFormatUtil.format(dir.getDate()))
                .isDir(true)
                .build();
    }
}
