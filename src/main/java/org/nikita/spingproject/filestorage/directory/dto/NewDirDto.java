package org.nikita.spingproject.filestorage.directory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewDirDto {
    private String currentPath;
    private String name;
}
