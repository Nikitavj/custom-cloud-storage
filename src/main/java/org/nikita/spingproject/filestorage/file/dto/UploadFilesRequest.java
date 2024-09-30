package org.nikita.spingproject.filestorage.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadFilesRequest {
    private MultipartFile[] multipartFiles;
    private String path;
}
