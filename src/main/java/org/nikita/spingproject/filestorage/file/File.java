package org.nikita.spingproject.filestorage.file;
import org.nikita.spingproject.filestorage.commons.EntityStorage;

public class File extends EntityStorage {
    public File() {
    }
    public File(String name, String path) {
        super(name, path);
    }
}
