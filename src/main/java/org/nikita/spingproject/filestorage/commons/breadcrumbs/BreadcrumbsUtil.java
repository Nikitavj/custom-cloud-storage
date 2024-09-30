package org.nikita.spingproject.filestorage.commons.breadcrumbs;

import org.apache.commons.lang3.StringUtils;
import org.nikita.spingproject.filestorage.commons.exception.StorageException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BreadcrumbsUtil {
    private static final String SEPARATOR = "/";

    public static List<Breadcrumb> createBreadcrumbs(String relativePath) {
        List<Breadcrumb> breadCrumbs = new ArrayList<>();

        if (relativePath == null) {
            return breadCrumbs;
        }

        try {
            if (relativePath.contains(SEPARATOR)) {
                String[] names = relativePath.split(SEPARATOR);
                breadCrumbs.add(new Breadcrumb(decode(names[0]), names[0]));
                for (int i = 1; i < (names.length); i++) {
                    String link = StringUtils
                            .substringBefore(relativePath, names[i]) + names[i];
                    breadCrumbs.add(new Breadcrumb(decode(names[i]), link));
                }
            } else {
                breadCrumbs.add(new Breadcrumb(
                        decode(relativePath),
                        relativePath));
                return breadCrumbs;
            }
        } catch (UnsupportedEncodingException e) {
            throw new StorageException();
        }

        return breadCrumbs;
    }

    private static String decode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }
}
