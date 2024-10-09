package org.nikita.spingproject.filestorage.service;

import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.directory.dto.CreateDirRequest;
import org.nikita.spingproject.filestorage.directory.exception.DirectoryAlreadyExistsException;
import org.nikita.spingproject.filestorage.file.dto.UploadFileRequest;
import org.nikita.spingproject.filestorage.file.dto.UploadFilesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class UploadFilesServiceImpl implements UploadFilesService {
    private static final String SEPARATOR = "/";
    private final FileService fileService;
    private final DirectoryService directoryService;

    @Autowired
    public UploadFilesServiceImpl(FileService fileService, DirectoryService directoryService) {
        this.fileService = fileService;
        this.directoryService = directoryService;
    }

    @Override
    public void upload(UploadFilesRequest req) throws IOException {
        List<MultipartFile> files = new LinkedList<>(Arrays.asList(req.getMultipartFiles()));
        MultipartFile firstFile = files.getFirst();
        String fileName = firstFile.getOriginalFilename();

        if (fileName != null && isDirectory(fileName)) {
            String nameDir = StringUtils.substringBefore(
                    firstFile.getOriginalFilename(),
                    SEPARATOR);

            directoryService.create(new CreateDirRequest(
                    req.getPath(),
                    nameDir));

            uploadFileOfDir(firstFile, req.getPath());
            files.removeFirst();

            for (MultipartFile file : files) {
                String prefixPathFile = StringUtils
                        .substringBeforeLast(
                                file.getOriginalFilename(),
                                SEPARATOR);
                uploadFileOfDir(file, req.getPath());
                if (prefixPathFile != null) {
                    createDirectoriesForFile(req.getPath(), prefixPathFile);
                }
            }

        } else {
            for (MultipartFile file : files) {
                fileService.upload(new UploadFileRequest(
                        file.getInputStream(),
                        req.getPath(),
                        file.getOriginalFilename()));
            }
        }
    }

    private void createDirectoriesForFile(String currentPath, String prefixPathFile) {
        String[] namesDir = prefixPathFile.split(SEPARATOR);
        String pathNewDir = currentPath;
        for (String nameDir : namesDir) {
            try {
                directoryService.create(
                        new CreateDirRequest(pathNewDir, nameDir));
            } catch (DirectoryAlreadyExistsException ignored) {
            }

            if (pathNewDir.isBlank()) {
                pathNewDir = nameDir;
            } else {
                pathNewDir = String.join("",
                        pathNewDir,
                        SEPARATOR,
                        nameDir);
            }
        }
    }

    private void uploadFileOfDir(MultipartFile file, String path) throws IOException {
        String nameFile = StringUtils
                .substringAfterLast(
                        file.getOriginalFilename(),
                        SEPARATOR);
        String postfixPath = StringUtils
                .substringBeforeLast(
                        file.getOriginalFilename(),
                        SEPARATOR);

        String newCurrentPath = postfixPath;
        if (!path.isBlank()) {
            newCurrentPath = String.join("",
                    path,
                    SEPARATOR,
                    postfixPath);
        }
        fileService.upload(new UploadFileRequest(
                file.getInputStream(),
                newCurrentPath,
                nameFile));
    }

    private boolean isDirectory(String nameFile) {
        return nameFile.contains(SEPARATOR);
    }

}
