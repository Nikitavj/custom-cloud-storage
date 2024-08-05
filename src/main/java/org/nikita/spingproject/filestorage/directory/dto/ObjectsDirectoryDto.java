package org.nikita.spingproject.filestorage.directory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.nikita.spingproject.filestorage.commons.EntityStorage;
import org.nikita.spingproject.filestorage.directory.Folder;
import org.nikita.spingproject.filestorage.file.File;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ObjectsDirectoryDto {
    private List<EntityStorage> list;
}
