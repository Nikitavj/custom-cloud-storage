package org.nikita.spingproject.filestorage.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectStorageDto {
    private String name;
    private String relativePath;
    private boolean isDir;
}
