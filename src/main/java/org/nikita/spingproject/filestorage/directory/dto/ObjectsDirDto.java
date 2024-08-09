package org.nikita.spingproject.filestorage.directory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ObjectsDirDto {
    private String relativePath;
    private String userName;
}
