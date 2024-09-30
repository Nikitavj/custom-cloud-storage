package org.nikita.spingproject.filestorage.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadFileRequest {
    private InputStream inputStream;
    private String path;
    private String name;
}
