package org.nikita.spingproject.filestorage.directory;

import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DirPathService {
    @Autowired
    private UserRepository userRepository;

    private String createRootPathForUser(String userName) {
        User user = userRepository.findUserByEmail(userName)
                .orElseThrow(() -> new EntityNotFoundException("User " + userName + "not exist"));
        return String.format("user-%s-files", user.getId());
    }

    public String createAbsolutPath(String relativePath, String userName) {
        String rootPath = createRootPathForUser(userName);
        if (relativePath.equals("/")) {
            return String.format("%s/", rootPath);
        } else {
            return String.format("%s/%s/",
                    rootPath,
                    relativePath);
        }
    }

    public String createFullPathNewDir(String currentPath, String name, String userName) {
        String rootPath = createRootPathForUser(userName);
        if (currentPath.equals("/")) {
            return String.format("%s/%s", rootPath, name);
        } else {
            return String.format("%s/%s/%s", rootPath, currentPath, name);
        }
    }

    public String createRelativePath(String path, String name) {
        if (path.equals("/")) {
            return name;
        } else {
            return String.format("%s/%s", path, name);
        }
    }

    public String createRelativePathRenameDir(String previousPath, String newName) {
        if (previousPath.contains("/")) {
            String path = StringUtils.substringBeforeLast(previousPath, "/");
            return String.format("%s/%s", path, newName);
        } else {
            return newName;
        }
    }

    public String createPathMetaDataObjectForRenameDir(String relativePath, String newName, String nameUser) {
        String rootPath = createRootPathForUser(nameUser);
        String relPath = createRelativePathRenameDir(relativePath, newName);
        return String.format("%s/%s_", rootPath, relPath);
    }

    public String createPathMetaDataObject(String pathFolder, String nameUser) {
        if (pathFolder.equals("/")) {
            return String.format("%s_", createRootPathForUser(nameUser));
        } else {
            return String.format("%s/%s_",
                    createRootPathForUser(nameUser),
                    pathFolder);
        }
    }
}
