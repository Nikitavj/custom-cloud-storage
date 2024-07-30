package org.nikita.spingproject.filestorage.directory.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain=true)
public class FolderDto {
    private String name;
    private String path;
    private String userName;
}
