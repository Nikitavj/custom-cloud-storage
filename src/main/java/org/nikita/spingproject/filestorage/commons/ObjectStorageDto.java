package org.nikita.spingproject.filestorage.commons;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ObjectStorageDto {
    private String name;
    private String relativePath;
    private boolean isDir;
}
