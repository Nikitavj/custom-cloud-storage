package org.nikita.spingproject.filestorage.file;
import lombok.*;
import org.nikita.spingproject.filestorage.commons.EntityS3;

import java.io.InputStream;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File{
    private InputStream inputStream;
    private String name;
    private String absolutePath;
    private String relativePath;
}
