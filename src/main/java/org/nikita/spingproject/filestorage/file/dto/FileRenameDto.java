package org.nikita.spingproject.filestorage.file.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class FileRenameDto {
    String path;
    String newName;
    String userName;
}
