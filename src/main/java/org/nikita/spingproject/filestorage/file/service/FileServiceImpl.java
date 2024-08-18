package org.nikita.spingproject.filestorage.file.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
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
    private UserRepository userRepository;
    private FileDao fileDao;

    @Autowired
    public FileServiceImpl(UserRepository userRepository, FileDao fileDao) {
        this.userRepository = userRepository;
        this.fileDao = fileDao;
    }

    @Override
    @SneakyThrows
    public void uploadFile(FileUploadDto dto) {
        String absolutePath = createAbsolutePathNewFile(
                dto.getPath(),
                dto.getName(),
                dto.getUserName());
        String relativePath = createRelativePath(dto.getPath(), dto.getName());

        fileDao.add(File.builder()
                .absolutePath(absolutePath)
                .relativePath(relativePath)
                .name(dto.getName())
                .inputStream(dto.getInputStream())
                .build());
    }

    @Override
    @SneakyThrows
    public void deleteFile(FileDto dto) {
        String absolutePath = createAbsolutePath(
                dto.getPath(),
                dto.getUserName());

        fileDao.remove(absolutePath);
    }

    @Override
    @SneakyThrows
    public FileDownloadDto downloadFile(FileDto dto) {
        String absolutePath = createAbsolutePath(
                dto.getPath(),
                dto.getUserName());

        return new FileDownloadDto(fileDao.get(absolutePath));
    }

    @Override
    public void renameFile(FileRenameDto dto) {
        String path = createAbsolutePath(dto.getPath(), dto.getUserName());
        String newPath = renameAbsolutePath(path, dto.getNewName());
        String relativeNewPath = renameLink(path, dto.getNewName());
        fileDao.rename(path,newPath, relativeNewPath, dto.getNewName());
        fileDao.remove(path);
    }


    private String renameAbsolutePath(String oldAsolurtePath, String newName) {
        String prefix = StringUtils.substringBeforeLast(oldAsolurtePath, "/");
        String newAsolutePath = prefix + "/" + newName;
        return newAsolutePath;
    }

    private String renameLink(String oldAbsolutePath, String newNameFile) {
        String oldRelativePath = StringUtils.substringAfter(oldAbsolutePath, "/");
        if (oldRelativePath.contains("/")) {
            return StringUtils.substringBeforeLast(oldRelativePath, "/") + "/" + newNameFile;
        } else {
            return newNameFile;
        }
    }

    private String createRelativePath(String path, String name) {
        if (path.isBlank()) {
            return name;
        } else {
            return String.format("%s/%s", path, name);
        }
    }

    private String createAbsolutePath(String path, String nameUser) {
        return String.format("%s/%s",
                createRootPathForUser(nameUser),
                path);
    }

    private String createAbsolutePathNewFile(String path, String nameFile, String nameUser) {
        if (path.isBlank()) {
            path = "";
        } else {
            path = path + "/";
        }

        return String.format("%s/%s%s",
                createRootPathForUser(nameUser),
                path,
                nameFile);
    }

    private String createRootPathForUser(String userName) {
        User user = userRepository.findUserByEmail(userName)
                .orElseThrow(() -> new EntityNotFoundException("User " + userName + "not exist"));
        return String.format("user-%s-files", user.getId());
    }
}
