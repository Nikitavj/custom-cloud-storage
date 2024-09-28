package org.nikita.spingproject.filestorage.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectStorageDto {
    private long id;
    private String name;
    private String date;
    private String size;
    private String relativePath;
    private boolean isDir;
}
