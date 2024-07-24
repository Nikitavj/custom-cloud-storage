package org.nikita.spingproject.filestorage.service;

import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.persistence.EntityNotFoundException;
import org.nikita.spingproject.filestorage.dao.FileDao;
import org.nikita.spingproject.filestorage.dto.FileUploadDto;
import org.nikita.spingproject.filestorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileService {

    @Autowired
    private FileDao fileDao;
    @Autowired
    private UserRepository userRepository;

    public void uploadFile(FileUploadDto uplDto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InputStream stream = uplDto.getMultipartFile().getInputStream();

        fileDao.putObject(
                stream,
                stream.available(),
                createPathFile(
                        uplDto.getUserName(),
                        uplDto.getPathFile(),
                        uplDto.getMultipartFile().getOriginalFilename()));
    }

    public void downloadFile(String userName, String filePath, String targetPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        fileDao.downloadObject(createPathDir(userName, filePath), targetPath);
    }



    public Map<String, String> findFilesOfDirectory(String userName, String pathDir) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> iterable = fileDao.getObjectsOfPath(
                createPathDir(userName, pathDir));

        Map<String, String> files = new HashMap<>();
        for (Result<Item> result : iterable) {
            Item item = result.get();

            if (item.isDir()) {
                String name = getNameFileOrDir(item.objectName());
                String link = getLinkFileOrDir(item.objectName());
                files.put(name + "/", link);
            } else {
                String name = getNameFileOrDir(item.objectName());
                String link = getLinkFileOrDir(item.objectName());
                files.put(name, link);
            }
        }
        return files;
    }

    private String getNameFileOrDir(String path) {
        String[] names = path.split("/");
        return names[names.length - 1];
    }

    private String getLinkFileOrDir(String path) {
        String[] links = path.split("/", 2);
        return links[links.length - 1];
    }

    private String createPathDir(String userName, String pathFile) {
        int userId = userRepository.findUserByEmail(userName)
                .orElseThrow(() -> new EntityNotFoundException("User " + userName + " not found"))
                .getId();

        return "user-" +
                userId +
                "-files/" +
                pathFile;
    }

    private String createPathFile(String userName, String pathFile, String fileName) {
        return createPathDir(userName, pathFile) + fileName;
    }
}
