package org.nikita.spingproject.filestorage.service;

import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PathFileServiceImpl implements PathFileService {
    private UserRepository userRepository;

    @Autowired
    public PathFileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String renameAbsolutePath(String oldAbsolutePath, String newName) {
        String prefix = StringUtils.substringBeforeLast(oldAbsolutePath, "/");
        String newAsolutePath = prefix + "/" + newName;
        return newAsolutePath;
    }

    @Override
    public String renameRelPath(String oldRelativePath, String newName) {
        if (oldRelativePath.contains("/")) {
            return StringUtils.substringBeforeLast(oldRelativePath, "/") + "/" + newName;
        } else {
            return newName;
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
    public String createAbsolutePath(String path) {
        return String.format("%s/%s",
                rootPathForUser(),
                path);
    }

    @Override
    public String createAbsolutePathNewFile(String path, String nameFile) {
        if (path.isBlank()) {
            path = "";
        } else {
            path = path + "/";
        }

        return String.format("%s/%s%s",
                rootPathForUser(),
                path,
                nameFile);
    }

    public String rootPathForUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("User " + auth.getName() + "not exist"));
        return String.format("user-%s-files", user.getId());
    }
}
