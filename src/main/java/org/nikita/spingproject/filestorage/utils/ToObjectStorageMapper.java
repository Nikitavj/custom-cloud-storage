package org.nikita.spingproject.filestorage.utils;

import org.apache.commons.io.FileUtils;
import org.nikita.spingproject.filestorage.commons.dto.ObjectStorageDto;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.s3manager.EntityS3;

public class ToObjectStorageMapper {
    public static ObjectStorageDto map(EntityS3 entity) {
        boolean isDir = true;
        String size = null;
        if(entity instanceof File) {
            size = FileUtils.byteCountToDisplaySize(((File) entity).getSize());
            isDir = false;
        }

        return ObjectStorageDto.builder()
                .name(entity.getName())
                .relativePath(entity.getPath())
                .date(DateFormatUtil.format(entity.getDate()))
                .size(size)
                .isDir(isDir)
                .build();
    }
}
