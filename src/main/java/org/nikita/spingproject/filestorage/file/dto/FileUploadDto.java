package org.nikita.spingproject.filestorage.file.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@Accessors(chain = true)
public class FileUploadDto {
    private MultipartFile multipartFile;
    private String pathFile;
    private String userName;
}
