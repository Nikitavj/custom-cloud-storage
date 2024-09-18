package org.nikita.spingproject.filestorage.directory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nikita.spingproject.filestorage.file.File;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Directory{
    private String name;
    private String absolutePath;
    private String relativePath;
    private ZonedDateTime date;
    private List<Directory> directories;
    private List<File> files;


    {
        this.directories = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public Directory(String name, String absolutePath, String relativePath, ZonedDateTime date) {
        this.name = name;
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
        this.date = date;
    }

    public void putDirectory(Directory directory) {
        directories.add(directory);
    }

    public void putFile(File file) {
        files.add(file);
    }
}
