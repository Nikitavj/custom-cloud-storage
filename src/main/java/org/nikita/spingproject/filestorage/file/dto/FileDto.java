package org.nikita.spingproject.filestorage.file.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileDto {
    private String path;
    private String nameUser;
}
