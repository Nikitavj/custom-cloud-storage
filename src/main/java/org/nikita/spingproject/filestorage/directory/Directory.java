package org.nikita.spingproject.filestorage.directory;

import lombok.*;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.s3manager.EntityS3;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Directory extends EntityS3 {
    private String name;
    private String path;
    private ZonedDateTime date;
    private List<Directory> directories;
    private List<File> files;

    {
        this.directories = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public Directory(String name, String path, ZonedDateTime date) {
        this.name = name;
        this.path = path;
        this.date = date;
    }

    public Directory(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void putDirectory(Directory directory) {
        directories.add(directory);
    }

    public void putFile(File file) {
        files.add(file);
    }
}
