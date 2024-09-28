package org.nikita.spingproject.filestorage.utils;

import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.nikita.spingproject.filestorage.commons.dto.InfoMetaS3;
import org.nikita.spingproject.filestorage.directory.Directory;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DirectoryUtil {
    public static Map<String, String> createMetaDataDir(String name, String relPath) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("name", name);
        metaData.put("path", relPath);
        metaData.put("dir", "");
        return metaData;
    }

    public static List<Directory> getDirFromItems(List<Item> items) {
        return items.stream()
                .filter(item -> item.userMetadata().containsKey("X-Amz-Meta-Dir"))
                .map(DirectoryUtil::itemMaptoDirectory)
                .collect(Collectors.toList());
    }

    private static Directory itemMaptoDirectory(Item item) {
        InfoMetaS3 info = buildInfo(item.userMetadata());
        return new Directory(
                info.getName(),
                info.getPath(),
                item.lastModified());
    }

    private static InfoMetaS3 buildInfo(Map<String, String> metaData) {
        return InfoMetaS3.builder()
                .name(metaData.get("X-Amz-Meta-Name"))
                .path(metaData.get("X-Amz-Meta-Path"))
                .isDir(metaData.containsKey("X-Amz-Meta-Dir"))
                .build();
    }

    public static List<Item> getListItemsNoIsMinioDir(Iterable<Result<Item>> results) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<Item> items = new ArrayList<>();
        for (Result<Item> itemResult : results) {
            Item item = itemResult.get();
            if (!item.isDir()) {
                items.add(item);
            }
        }
        return items;
    }

    public static InfoMetaS3 convertMetaToObject(Map<String, String> metaData) {
        return new InfoMetaS3(
                metaData.get("name"),
                metaData.get("path"),
                metaData.containsKey("dir"));
    }
}
