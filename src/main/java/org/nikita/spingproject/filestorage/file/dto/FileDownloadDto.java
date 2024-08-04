package org.nikita.spingproject.filestorage.file.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;

@Data
@Accessors(chain = true)
public class FileDownloadDto {
    private String name;
    private InputStream inputStream;
}
