package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.file.dto.FileDownloadDto;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.file.dto.RenameFileRequest;
import org.nikita.spingproject.filestorage.path.PathFileUtil;
import org.nikita.spingproject.filestorage.s3manager.S3FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {
    private final S3FileManager s3FileManager;

    @Autowired
    public FileServiceImpl(S3FileManager s3FileManager) {
        this.s3FileManager = s3FileManager;
    }

    @Override
    public void upload(FileUploadDto dto) {
            String path = PathFileUtil
                    .createPath(dto.getPath(), dto.getName());

            s3FileManager.add(File.builder()
                    .path(path)
                    .name(dto.getName())
                    .inputStream(dto.getInputStream())
                    .build());
    }

    @Override
    public void delete(FileDto dto) {
        s3FileManager.remove(dto.getPath());
    }

    @Override
    public FileDownloadDto download(FileDto dto) {
        File file = s3FileManager.get(dto.getPath());
        return new FileDownloadDto(file.getInputStream(), file.getName());
    }

    @Override
    public void rename(RenameFileRequest dto) {
            String path = PathFileUtil.renamePath(dto.getPath(), dto.getNewName());
            s3FileManager.copy(
                    dto.getPath(),
                    path,
                    dto.getNewName());
    }
}
