package org.nikita.spingproject.filestorage.utils;

import io.minio.messages.Item;
import org.nikita.spingproject.filestorage.commons.dto.InfoMetaS3;
import org.nikita.spingproject.filestorage.file.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileUtil {
    public static Map<String, String> createMetaDataFile(String name, String relPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("path", relPath);
        metaData.put("file", "");
        return metaData;
    }

    public static List<File> getFilesFromItems(List<Item> items) {
        return items.stream()
                .filter(item -> item.userMetadata().containsKey("X-Amz-Meta-File"))
                .map(FileUtil::itemMapToFile)
                .collect(Collectors.toList());
    }

    private static File itemMapToFile(Item item) {
        InfoMetaS3 info = buildInfo(item.userMetadata());
        return File.builder()
                .name(info.getName())
                .path(info.getPath())
                .date(item.lastModified())
                .size(item.size())
                .build();
    }

    private static InfoMetaS3 buildInfo(Map<String, String> metaData) {
        return InfoMetaS3.builder()
                .name(metaData.get("X-Amz-Meta-Name"))
                .path(metaData.get("X-Amz-Meta-Path"))
                .isDir(metaData.containsKey("X-Amz-Meta-Dir"))
                .build();
    }
}
