package org.nikita.spingproject.filestorage.commons;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InfoOfObjectS3 {
    String name;
    String relativePath;
    String absPath;
    boolean isDir;
}
