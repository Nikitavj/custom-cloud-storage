package org.nikita.spingproject.filestorage.directory;

import io.minio.messages.Item;
import org.nikita.spingproject.filestorage.commons.EntityStorage;
import org.nikita.spingproject.filestorage.file.File;

import java.util.Map;

public class ItemToEntityStorageMapper {

    public static EntityStorage map(Item item) {
        Map<String, String> metaData = item.userMetadata();

        if (metaData.containsKey("X-Amz-Meta-File")) {
            return new File(
                    metaData.get("X-Amz-Meta-Name"),
                    metaData.get("X-Amz-Meta-Link"),
                    false);
        } else {
            return new Folder(
                    metaData.get("X-Amz-Meta-Name"),
                    metaData.get("X-Amz-Meta-Link"),
                    true);
        }
    }
}
