package org.nikita.spingproject.filestorage.directory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@AllArgsConstructor
public class DirDto {
    private String name;
    private String relativePath;
}
