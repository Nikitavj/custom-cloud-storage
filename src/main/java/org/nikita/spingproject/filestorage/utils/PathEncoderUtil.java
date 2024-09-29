package org.nikita.spingproject.filestorage.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PathEncoderUtil {
    private static final String SEPARATOR = "/";
    public static String encode(String path) throws UnsupportedEncodingException {
        if (path.contains(SEPARATOR)) {
            String[] parts = path.split(SEPARATOR);
            List<String> partsUTF = Arrays.stream(parts)
                    .map(part -> {
                        try {
                            return URLEncoder.encode(part, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());

            return String.join(SEPARATOR, partsUTF);
        } else {
            return URLEncoder.encode(path, "UTF-8");
        }
    }
}
