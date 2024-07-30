package org.nikita.spingproject.filestorage.directory.dto;

import lombok.Getter;
import lombok.Setter;
import org.nikita.spingproject.filestorage.directory.Folder;
import org.nikita.spingproject.filestorage.file.File;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ObjectsDirectoryDto {
    private List<File> files = new ArrayList<>();
    private List<Folder> folders = new ArrayList<>();
}
