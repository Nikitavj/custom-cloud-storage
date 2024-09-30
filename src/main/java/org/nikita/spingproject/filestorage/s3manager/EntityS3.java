package org.nikita.spingproject.filestorage.s3manager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
