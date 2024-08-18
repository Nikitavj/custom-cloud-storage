package org.nikita.spingproject.filestorage.file.service;

import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PathFileServiceImpl implements PathFileService{
    private UserRepository userRepository;

    @Autowired
    public PathFileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String renameAbsolutePath(String oldAsolurtePath, String newName) {
        String prefix = StringUtils.substringBeforeLast(oldAsolurtePath, "/");
        String newAsolutePath = prefix + "/" + newName;
        return newAsolutePath;
    }

    @Override
    public String renameLink(String oldAbsolutePath, String newNameFile) {
        String oldRelativePath = StringUtils.substringAfter(oldAbsolutePath, "/");
        if (oldRelativePath.contains("/")) {
            return StringUtils.substringBeforeLast(oldRelativePath, "/") + "/" + newNameFile;
        } else {
            return newNameFile;
        }
    }

    @Override
    public String createRelativePath(String path, String name) {
        if (path.isBlank()) {
            return name;
        } else {
            return String.format("%s/%s", path, name);
        }
    }

    @Override
    public String createAbsolutePath(String path, String nameUser) {
        return String.format("%s/%s",
                createRootPathForUser(nameUser),
                path);
    }

    @Override
    public String createAbsolutePathNewFile(String path, String nameFile, String nameUser) {
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
