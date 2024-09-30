package org.nikita.spingproject.filestorage.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadFileResponse {
    private InputStream inputStream;
    private String name;
}
