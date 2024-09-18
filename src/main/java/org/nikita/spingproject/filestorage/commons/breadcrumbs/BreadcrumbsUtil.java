package org.nikita.spingproject.filestorage.commons.breadcrumbs;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class BreadcrumbsUtil {

    public static List<Breadcrumbs> createBreadcrumbs(String relativePath) {
        List<Breadcrumbs> breadCrumbs = new ArrayList<>();

        if (relativePath == null) {
            return breadCrumbs;
        }

        try {
            if (!relativePath.contains("/")) {
                breadCrumbs.add(new Breadcrumbs(encode(relativePath), relativePath));
                return breadCrumbs;
            }
            if (relativePath.contains("/")) {
                String[] names = relativePath.split("/");
                breadCrumbs.add(new Breadcrumbs(encode(names[0]), names[0]));
                for (int i = 1; i < (names.length); i++) {
                    String link = StringUtils.substringBefore(relativePath, names[i]) + names[i];
                    breadCrumbs.add(new Breadcrumbs(encode(names[i]), link));
                }
            }
        } catch (UnsupportedEncodingException e) {}

        return breadCrumbs;
    }

    private static String encode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, "UTF-8");
    }
}
