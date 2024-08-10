package org.nikita.spingproject.filestorage.commons;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntityS3 {
    private String name;
    private String absolutePath;
    private String relativePath;
    private boolean isDir;
}
