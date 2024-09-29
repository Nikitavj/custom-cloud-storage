package org.nikita.spingproject.filestorage.commons.breadcrumbs;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class BreadcrumbsUtil {
    private static final String SEPARATOR = "/";

    public static List<Breadcrumbs> createBreadcrumbs(String relativePath) {
        List<Breadcrumbs> breadCrumbs = new ArrayList<>();

        if (relativePath == null) {
            return breadCrumbs;
        }

        try {
            if (relativePath.contains(SEPARATOR)) {
                String[] names = relativePath.split(SEPARATOR);
                breadCrumbs.add(new Breadcrumbs(decode(names[0]), names[0]));
                for (int i = 1; i < (names.length); i++) {
                    String link = StringUtils.substringBefore(relativePath, names[i]) + names[i];
                    breadCrumbs.add(new Breadcrumbs(decode(names[i]), link));
                }
            } else {
                breadCrumbs.add(new Breadcrumbs(decode(relativePath), relativePath));
                return breadCrumbs;
            }
        } catch (UnsupportedEncodingException e) {
        }

        return breadCrumbs;
    }

    private static String decode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, "UTF-8");
    }
}
