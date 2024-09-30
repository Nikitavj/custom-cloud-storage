package org.nikita.spingproject.filestorage.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PathEncoderUtil {
    private static final String SEPARATOR = "/";
    public static String encode(String path) throws UnsupportedEncodingException {
        if (path.contains(SEPARATOR)) {
            String[] parts = path.split(SEPARATOR);
            List<String> partsUTF = Arrays.stream(parts)
                    .map(part -> URLEncoder.encode(part, StandardCharsets.UTF_8))
                    .collect(Collectors.toList());

            return String.join(SEPARATOR, partsUTF);
        } else {
            return URLEncoder.encode(path, StandardCharsets.UTF_8);
        }
    }
}
