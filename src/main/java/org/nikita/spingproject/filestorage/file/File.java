package org.nikita.spingproject.filestorage.file;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nikita.spingproject.filestorage.commons.EntityStorage;

import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File extends EntityStorage {
    private InputStream inputStream;

    public File(String name, String link, boolean isDir) {
        super(name, link, isDir);
    }
}
