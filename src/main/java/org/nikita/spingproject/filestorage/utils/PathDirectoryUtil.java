package org.nikita.spingproject.filestorage.utils;

import org.apache.commons.lang3.StringUtils;

public class PathDirectoryUtil {
    public static String createPath(String path, String name)  {
        if (path.isBlank()) {
            return name;
        } else {
            return String.format("%s/%s", path, name);
        }
    }

    public static String renamePath(String previousPath, String newName) {
        if (previousPath.contains("/")) {
            return StringUtils.substringBeforeLast(previousPath, "/") + "/" + newName;
        } else {
            return newName;
        }
    }

}
