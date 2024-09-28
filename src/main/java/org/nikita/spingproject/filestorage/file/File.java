package org.nikita.spingproject.filestorage.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nikita.spingproject.filestorage.s3manager.EntityS3;

import java.io.InputStream;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File extends EntityS3 {
    private InputStream inputStream;
    private String name;
    private String path;
    private ZonedDateTime date;
    private long size;
}
