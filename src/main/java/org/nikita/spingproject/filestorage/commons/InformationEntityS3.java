package org.nikita.spingproject.filestorage.commons;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InformationEntityS3 {
    String name;
    String relativePath;
    boolean isDir;
}
