package org.nikita.spingproject.filestorage.commons;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntityStorage {
    private String name;
    private String link;
    private boolean isDir;
}
