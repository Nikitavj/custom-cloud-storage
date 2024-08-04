package org.nikita.spingproject.filestorage.directory;

import io.minio.messages.Item;
import org.nikita.spingproject.filestorage.file.File;

public class ItemToEntityStorageMapper {

    public static File mappToFile(Item item) {
        return new File(
                item.userMetadata().get("X-Amz-Meta-Name"),
                item.userMetadata().get("X-Amz-Meta-Link"));
    }

    public static Folder mappToFolder(Item item) {
        return new Folder(
                item.userMetadata().get("X-Amz-Meta-Name"),
                item.userMetadata().get("X-Amz-Meta-Link"));
//
//        String path = item.objectName();
//        String path1 = createLinkForPath(path);
//        String newPath = path1.substring(0, path1.length()-1);
//        return new Folder(
//                createNameFromPath(path),
//                newPath);
    }

    private static String createLinkForPath(String path) {
        String[] arrayPaths = path.split("/", 2);
        return arrayPaths[arrayPaths.length -1];
    }

    private static String createNameFromPath(String path) {
        String[] arrayNames = path.split("/");
        return arrayNames[arrayNames.length -1];
    }
}
