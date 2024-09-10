package org.nikita.spingproject.filestorage.directory.service;

import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathDirectoryServiceImpl implements PathDirectoryService {
    private UserRepository userRepository;

    @Autowired
    public PathDirectoryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String rootPathForUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("User " + auth.getName() + "not exist"));
        return String.format("user-%s-files", user.getId());
    }

    @Override
    public String absolutPath(String relativePath) {
        String rootPath = rootPathForUser();
        if (relativePath == null) {
            return rootPath;
        } else {
            return String.format("%s/%s",
                    rootPath,
                    relativePath);
        }
    }

    @Override
    public String absolutePathNewDir(String currentPath, String name)  {
        String rootPath = rootPathForUser();
        if (currentPath.isBlank()) {
            return String.format("%s/%s", rootPath, name);
        } else {
            return String.format("%s/%s/%s", rootPath, currentPath, name);
        }
    }

    @Override
    public String relativePath(String path, String name)  {
        if (path.isBlank()) {
            return name;
        } else {
            return String.format("%s/%s", path, name);
        }
    }

    @Override
    public String renameAbsolutePath(String previousAbsolutePath, String newName) {
        String path = StringUtils.removeEnd(previousAbsolutePath, "/");
        return StringUtils.substringBeforeLast(path, "/") + "/" + newName;
    }

    @Override
    public String renameRelativePath(String previousPath, String newName) {
        if (previousPath.contains("/")) {
            return StringUtils.substringBeforeLast(previousPath, "/") + "/" + newName;
        } else {
            return newName;
        }
    }
}
