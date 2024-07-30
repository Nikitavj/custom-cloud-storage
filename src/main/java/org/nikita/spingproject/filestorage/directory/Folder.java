package org.nikita.spingproject.filestorage.directory;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.nikita.spingproject.filestorage.commons.EntityStorage;

public class Folder extends EntityStorage {
    public Folder(String name, String path) {
        super(name, path);
    }

    public Folder() {
    }
}
