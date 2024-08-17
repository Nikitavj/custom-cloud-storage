package org.nikita.spingproject.filestorage.file.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.nikita.spingproject.filestorage.file.dao.FileDao;
import org.nikita.spingproject.filestorage.file.dto.FileDownloadDto;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.FileRenameDto;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {
    FileDao fileDao;
    UserRepository userRepository;

    @Autowired
    public FileServiceImpl(FileDao fileDao, UserRepository userRepository) {
        this.fileDao = fileDao;
        this.userRepository = userRepository;
    }

    @Override
    @SneakyThrows
    public void uploadFile(FileUploadDto dto) {
        String pathNewFile = createPathForSaveNewFile(
                dto.getPath(),
                dto.getName(),
                dto.getUserName());

        Map<String, String> metadata = new HashMap<>();
        metadata.put("name", dto.getName());
        metadata.put("rel_path", createLink(dto.getPath(), dto.getName()));
        metadata.put("file", "");

        fileDao.putFile(
                metadata,
                pathNewFile,
                dto.getInputStream());
    }

    @Override
    @SneakyThrows
    public void deleteFile(FileDto dto) {
        String path = createPathForFile(
                dto.getPath(),
                dto.getUserName());

        fileDao.deleteFile(path);
    }

    @Override
    @SneakyThrows
    public FileDownloadDto downloadFile(FileDto dto) {
        String path = createPathForFile(
                dto.getPath(),
                dto.getUserName());

        return new FileDownloadDto(fileDao.downloadFile(path));
    }

    @Override
    public void renameFile(FileRenameDto dto) {
        String path = createPathForFile(dto.getPath(), dto.getUserName());
        String newPath = renameAbsolutePath(path, dto.getNewName());

        Map<String, String> metadata = new HashMap<>();
        metadata.put("name", dto.getNewName());
        metadata.put("rel_path", renameLink(path, dto.getNewName()));
        metadata.put("file", "");

        fileDao.copyFile(
                path,
                newPath,
                metadata);

        fileDao.deleteFile(path);
    }


    private String renameAbsolutePath(String oldAsolurtePath, String newName) {
        String prefix = StringUtils.substringBeforeLast(oldAsolurtePath, "/");
        String newAsolutePath = prefix + "/" + newName;
        return newAsolutePath;
    }

    private String renameLink(String oldAbsolutePath, String newNameFile) {
        String oldRelativePath = StringUtils.substringAfter(oldAbsolutePath, "/");
        String prefix = StringUtils.substringBeforeLast(oldRelativePath, "/");
        if (prefix.contains("/")) {
            return prefix + "/" + newNameFile;
        } else {
            return newNameFile;
        }

    }

    private String createLink(String path, String name) {
        if (path.isBlank()) {
            return name;
        } else {
            return String.format("%s/%s", path, name);
        }
    }

    private String createPathForFile(String path, String nameUser) {
        return String.format("%s/%s",
                createRootPathForUser(nameUser),
                path);
    }

    private String createPathForSaveNewFile(String path, String nameFile, String nameUser) {
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
