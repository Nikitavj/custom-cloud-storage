package org.nikita.spingproject.filestorage.s3manager;

import lombok.*;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EntityS3 {
    private String name;
    private String path;
    private ZonedDateTime date;
}
