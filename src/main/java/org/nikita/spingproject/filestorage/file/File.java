package org.nikita.spingproject.filestorage.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File{
    private InputStream inputStream;
    private String name;
    private String path;
    private ZonedDateTime date;
    private long size;
}
