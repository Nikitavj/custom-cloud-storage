package org.nikita.spingproject.filestorage.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectStorageDto {
    private String name;
    private String date;
    private String size;
    private String relativePath;
    private boolean isDir;
}
