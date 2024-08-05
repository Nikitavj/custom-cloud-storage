package org.nikita.spingproject.filestorage.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.nikita.spingproject.filestorage.file.dao.FileDao;
import org.nikita.spingproject.filestorage.file.dto.FileDownloadDto;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    FileDao fileDao;
    @Autowired
    UserRepository userRepository;

    @Override
    @SneakyThrows
    public void uploadFile(FileUploadDto dto) {
        String pathNewFile = createPathForSaveNewFile(
                dto.getPath(),
                dto.getName(),
                dto.getUserName());

        Map<String, String> metadata = new HashMap<>();
        metadata.put("name", dto.getName());
        metadata.put("link", createLink(dto.getPath(), dto.getName()));
        metadata.put("File", "");

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

        return new FileDownloadDto()
                .setInputStream(fileDao.downloadFile(path));
    }

    private String createLink(String path, String name) {
        if(path == null
                || path.equals("/")
                || path.isBlank()) {
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
        if (path == null || path.equals("/") || path.isBlank()) {
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
