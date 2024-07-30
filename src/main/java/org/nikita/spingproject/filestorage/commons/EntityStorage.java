package org.nikita.spingproject.filestorage.commons;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityStorage {
    private String name;
    private String path;
}
