package org.nikita.spingproject.filestorage.directory.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Setter
@Getter
public class RenameDirDto {
    private String previousPath;
    private String newName;
    private String userName;
}
