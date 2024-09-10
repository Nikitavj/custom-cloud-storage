package org.nikita.spingproject.filestorage.commons;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PathEncoder {
    public static String encode(String path) throws UnsupportedEncodingException {
        if (path.contains("/")) {
            String[] parts = path.split("/");
            List<String> partsUTF = Arrays.stream(parts)
                    .map(part -> {
                        try {
                            return URLEncoder.encode(part, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());

            return String.join("/", partsUTF);
        } else {
            return URLEncoder.encode(path, "UTF-8");
        }
    }
}
