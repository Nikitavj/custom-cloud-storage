package org.nikita.spingproject.filestorage.directory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nikita.spingproject.filestorage.file.File;

import java.util.List;

@Data
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
