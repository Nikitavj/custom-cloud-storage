package org.nikita.spingproject.filestorage.path;

import org.apache.commons.lang3.StringUtils;

public class PathUtil {
    private static final String SEPARATOR = "/";

    public static String createPath(String path, String name) {
        if (path == null || path.isBlank()) {
            return name;
        } else {
            return String.join("",
                    path,
                    SEPARATOR,
                    name);
        }
    }

    public static String renamePath(String oldPath, String newName) {
        if (oldPath.contains(SEPARATOR)) {
            return String.join("",
                    StringUtils.substringBeforeLast(oldPath, SEPARATOR),
                    SEPARATOR,
                    newName);
        } else {
            return newName;
        }
    }

    public static boolean isRoot(String path) {
        return (path == null || path.isBlank());
    }

    public static String extractDirectoryPath(String relativePath) {
        if(relativePath.contains(SEPARATOR)) {
            return StringUtils.substringBeforeLast(relativePath, SEPARATOR);
        } else {
            return "";
        }
    }


}
