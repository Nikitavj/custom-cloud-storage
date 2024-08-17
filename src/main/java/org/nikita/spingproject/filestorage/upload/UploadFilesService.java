package org.nikita.spingproject.filestorage.upload;

import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.directory.dto.NewDirDto;
import org.nikita.spingproject.filestorage.directory.service.DirectoryService;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFilesService {
    @Autowired
    private FileService fileService;
    @Autowired
    private DirectoryService directoryService;

    public void upload(FileUploadDto dto) {
        if (isDirectory(dto.getName())) {
            String prefixPathFile = StringUtils.substringBeforeLast(dto.getName(), "/");
            createDirectories(dto.getPath(), prefixPathFile, dto.getUserName());
            uploadFileOfDir(dto);
        } else {
            fileService.uploadFile(dto);
        }
    }

    private void createDirectories(String currentPath, String prefixPathFile, String userName) {
        String[] namesDir = prefixPathFile.split("/");
        String pathNewDir = currentPath;
        for (String nameDir: namesDir) {
            directoryService.createNewDirectory(new NewDirDto(pathNewDir ,nameDir, userName));

            if (pathNewDir.isBlank()) {
                pathNewDir = nameDir;
            }else {
                pathNewDir = pathNewDir + "/" + nameDir;
            }
        }
    }

    private void uploadFileOfDir(FileUploadDto dto) {
        String nameFile = StringUtils.substringAfterLast(dto.getName(), "/");
        String postfixPath = StringUtils.substringBeforeLast(dto.getName(),"/");

        String newCurrentPath = postfixPath;
        if (!dto.getPath().isBlank()) {
            newCurrentPath = dto.getPath() + "/" + postfixPath;
        }

        dto.setPath(newCurrentPath);
        dto.setName(nameFile);
        fileService.uploadFile(dto);
    }

    private boolean isDirectory(String nameFile) {
        return nameFile.contains("/");
    }

}
