package org.nikita.spingproject.filestorage.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class InfoMetaS3 {
    String name;
    String path;
    boolean isDir;
}
