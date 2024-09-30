package org.nikita.spingproject.filestorage.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteFileRequest {
    private String path;
}
