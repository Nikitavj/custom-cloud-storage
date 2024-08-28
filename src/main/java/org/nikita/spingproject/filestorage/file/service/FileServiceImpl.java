package org.nikita.spingproject.filestorage.file.service;

import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.file.dao.FileDao;
import org.nikita.spingproject.filestorage.file.dto.FileDownloadDto;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.FileRenameDto;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {
    private PathFileService pathFileService;
    private FileDao fileDao;

    @Autowired
    public FileServiceImpl(PathFileService pathFileService, FileDao fileDao) {
        this.pathFileService = pathFileService;
        this.fileDao = fileDao;
    }

    @Override
    public void uploadFile(FileUploadDto dto) {
        String absolutePath = pathFileService
                .createAbsolutePathNewFile(
                        dto.getPath(),
                        dto.getName(),
                        dto.getUserName());
        String relativePath = pathFileService
                .createRelativePath(dto.getPath(), dto.getName());

        fileDao.add(File.builder()
                .absolutePath(absolutePath)
                .relativePath(relativePath)
                .name(dto.getName())
                .inputStream(dto.getInputStream())
                .build());
    }

    @Override
    public void deleteFile(FileDto dto) {
        String absolutePath = pathFileService.
                createAbsolutePath(
                        dto.getPath(),
                        dto.getUserName());

        fileDao.remove(absolutePath);
    }

    @Override
    public FileDownloadDto downloadFile(FileDto dto) {
        String absolutePath = pathFileService
                .createAbsolutePath(
                        dto.getPath(),
                        dto.getUserName());
        File file = fileDao.get(absolutePath);

        return new FileDownloadDto(file.getInputStream(), file.getName());
    }

    @Override
    public void renameFile(FileRenameDto dto) {
        String path = pathFileService
                .createAbsolutePath(
                        dto.getPath(),
                        dto.getUserName());
        String newPath = pathFileService
                .renameAbsolutePath(
                        path,
                        dto.getNewName());
        String relativeNewPath = pathFileService
                .renameLink(
                        path,
                        dto.getNewName());
        fileDao.rename(
                path,
                newPath,
                relativeNewPath,
                dto.getNewName());
        fileDao.remove(path);
    }
}
