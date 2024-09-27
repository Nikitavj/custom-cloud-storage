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
