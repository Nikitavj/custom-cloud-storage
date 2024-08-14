package org.nikita.spingproject.filestorage.directory;

import lombok.*;
import org.nikita.spingproject.filestorage.commons.EntityS3;
import org.nikita.spingproject.filestorage.file.File;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Directory{
    private String name;
    private String absolutePath;
    private String relativePath;
    private List<Directory> directories;
    private List<File> files;
}
