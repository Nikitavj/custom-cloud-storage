package org.nikita.spingproject.filestorage.file;

import lombok.*;
import org.nikita.spingproject.filestorage.s3manager.EntityS3;

import java.io.InputStream;
import java.time.ZonedDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class File extends EntityS3 {
    private InputStream inputStream;
    private String name;
    private String path;
    private ZonedDateTime date;
    private long size;
}
