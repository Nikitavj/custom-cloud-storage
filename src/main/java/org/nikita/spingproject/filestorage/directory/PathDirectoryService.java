package org.nikita.spingproject.filestorage.directory;

import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PathDirectoryService {
    @Autowired
    private UserRepository userRepository;

    public String rootPathForUser(String userName) {
        User user = userRepository.findUserByEmail(userName)
                .orElseThrow(() -> new EntityNotFoundException("User " + userName + "not exist"));
        return String.format("user-%s-files", user.getId());
    }

    public String absolutPath(String relativePath, String userName) {
        String rootPath = rootPathForUser(userName);
        if (relativePath == null) {
            return String.format("%s", rootPath);
        } else {
            return String.format("%s/%s",
                    rootPath,
                    relativePath);
        }
    }

    public String absolutePathNewDir(String currentPath, String name, String userName) {
        String rootPath = rootPathForUser(userName);
        if (currentPath.isBlank()) {
            return String.format("%s/%s", rootPath, name);
        } else {
            return String.format("%s/%s/%s", rootPath, currentPath, name);
        }
    }

    public String relativePath(String path, String name) {
        if (path.isBlank()) {
            return name;
        } else {
            return String.format("%s/%s", path, name);
        }
    }

    public String renameAbsolutePath(String previousAbsolutePath, String newName) {
        String path = StringUtils.removeEnd(previousAbsolutePath,"/");
        return StringUtils.substringBeforeLast(path, "/") + "/" + newName;
    }

    public String renameRelativePath(String previousPath, String newName) {
        if(previousPath.contains("/")) {
            return StringUtils.substringBeforeLast(previousPath, "/") + "/" + newName;
        } else {
            return newName;
        }
    }
}
