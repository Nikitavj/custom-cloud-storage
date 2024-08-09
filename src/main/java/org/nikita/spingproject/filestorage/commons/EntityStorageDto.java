package org.nikita.spingproject.filestorage.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntityStorageDto {
    private String name;
    private String relativePath;
    private boolean isDir;
}
