package org.nikita.spingproject.filestorage.directory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewDirDto {
    private String currentPath;
    private String name;
    private String userName;
}
