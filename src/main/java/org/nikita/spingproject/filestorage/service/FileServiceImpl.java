package org.nikita.spingproject.filestorage.service;

import io.minio.errors.*;
import jakarta.persistence.EntityNotFoundException;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.nikita.spingproject.filestorage.file.File;
import org.nikita.spingproject.filestorage.file.dao.FileDao;
import org.nikita.spingproject.filestorage.file.dto.FileDto;
import org.nikita.spingproject.filestorage.file.dto.FileUploadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    FileDao fileDao;
    @Autowired
    UserRepository userRepository;

    @Override
    public void uploadFile(FileUploadDto dto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File file = new File();
        String pathNewFile = createPathForNewFile(
                dto.getPathFile(),
                dto.getMultipartFile().getOriginalFilename(),
                dto.getUserName());
        file.setPath(pathNewFile);
        file.setInputStream(dto.getMultipartFile().getInputStream());

        fileDao.putFile(file);
    }

    @Override
    public void deleteFile(FileDto dto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File file = new File();
        file.setPath(createPathForFile(dto.getPath(), dto.getNameUser()));
        fileDao.deleteFile(file);
    }

    private String createPathForFile(String path, String nameUser) {
        return String.format("%s/%s",
                createRootPathForUser(nameUser),
                path);
    }

    private String createPathForNewFile(String path, String nameFile, String nameUser) {
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
