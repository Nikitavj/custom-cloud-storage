package org.nikita.spingproject.filestorage.file.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.InputStream;

@Setter
@Getter
@Accessors(chain = true)
public class FileUploadDto {
    private InputStream inputStream;
    private String name;
    private String path;
    private String userName;
}
