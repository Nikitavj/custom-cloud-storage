package org.nikita.spingproject.filestorage.path;

import org.apache.commons.lang3.StringUtils;

public class PathFileUtil {
    private static final String SEPARATOR = "/";
    public static String renamePath(String oldPath, String newName) {
        if (oldPath.contains(SEPARATOR)) {
            return StringUtils.substringBeforeLast(oldPath, SEPARATOR) + SEPARATOR + newName;
        } else {
            return newName;
        }
    }

    public static String createPath(String path, String name) {
        if (path == null || path.isBlank()) {
            return name;
        } else {
            return String.format("%s/%s", path, name);
        }
    }
}
