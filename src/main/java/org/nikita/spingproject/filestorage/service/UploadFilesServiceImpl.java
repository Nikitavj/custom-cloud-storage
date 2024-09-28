package org.nikita.spingproject.filestorage.service;

import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.directory.dto.NewDirRequest;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryAlreadyExistsException;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.file.dto.FilesUploadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class UploadFilesServiceImpl implements UploadFilesService {
    private final FileService fileService;
    private final DirectoryService directoryService;

    @Autowired
    public UploadFilesServiceImpl(FileService fileService, DirectoryService directoryService) {
        this.fileService = fileService;
        this.directoryService = directoryService;
    }

    @Override
    public void upload(FilesUploadDto dto) throws IOException {
        List<MultipartFile> multFiles = new LinkedList<>(Arrays.asList(dto.getMultipartFiles()));
        MultipartFile firstFile = multFiles.getFirst();

        if (isDirectory(firstFile.getOriginalFilename())) {
            String nameRootDir = StringUtils.substringBefore(firstFile.getOriginalFilename(), "/");
            directoryService.create(
                    new NewDirRequest(dto.getPath(), nameRootDir));
            uploadFileOfDir(firstFile, dto.getPath());
            multFiles.removeFirst();

            for (MultipartFile mFile : multFiles) {
                String prefixPathFile = StringUtils.substringBeforeLast(mFile.getOriginalFilename(), "/");
                uploadFileOfDir(mFile, dto.getPath());
                createDirectoriesForFile(dto.getPath(), prefixPathFile);
            }

        } else {
            for (MultipartFile mFile : multFiles) {
                fileService.upload(
                        new FileUploadDto(
                                mFile.getInputStream(),
                                dto.getPath(),
                                mFile.getOriginalFilename()));
            }
        }
    }

    private void createDirectoriesForFile(String currentPath, String prefixPathFile) {
        String[] namesDir = prefixPathFile.split("/");
        String pathNewDir = currentPath;
        for (String nameDir : namesDir) {
            try {
                directoryService.create(
                        new NewDirRequest(pathNewDir, nameDir));
            } catch (DirectoryAlreadyExistsException e) {
            }

            if (pathNewDir.isBlank()) {
                pathNewDir = nameDir;
            } else {
                pathNewDir = pathNewDir + "/" + nameDir;
            }
        }
    }

    private void uploadFileOfDir(MultipartFile mFile, String path) throws IOException {
        String nameFile = StringUtils.substringAfterLast(mFile.getOriginalFilename(), "/");
        String postfixPath = StringUtils.substringBeforeLast(mFile.getOriginalFilename(), "/");

        String newCurrentPath = postfixPath;
        if (!path.isBlank()) {
            newCurrentPath = path + "/" + postfixPath;
        }
        fileService.upload(
                new FileUploadDto(
                        mFile.getInputStream(),
                        newCurrentPath,
                        nameFile));
    }

    private boolean isDirectory(String nameFile) {
        return nameFile.contains("/");
    }

}
