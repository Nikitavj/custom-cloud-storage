package org.nikita.spingproject.filestorage.path;

import org.nikita.spingproject.filestorage.account.UserRepository;
import org.nikita.spingproject.filestorage.utils.PathEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class S3DirectoryPathBuilder extends S3PathBuilder {
    private static final String POSTFIX = "_meta";
    private static final String SEPARATOR = "/";

    @Autowired
    public S3DirectoryPathBuilder(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public String buildPath(String path) throws UnsupportedEncodingException {
        if(path == null || path.isBlank()) {
            return PathEncoderUtil.encode(rootPathForUser());
        } else {
            String pathS3 =  String.join("",
                    rootPathForUser(),
                    SEPARATOR,
                    path);
            return PathEncoderUtil.encode(pathS3);
        }
    }

    public String buildPathMeta(String path) throws UnsupportedEncodingException {
        String pathS3 =  String.join("",
                rootPathForUser(),
                SEPARATOR,
                path,
                POSTFIX);
        return PathEncoderUtil.encode(pathS3);
    }

    public String buildPathDirObjects(String path) throws UnsupportedEncodingException {
        return String.join("",
                buildPath(path),
                SEPARATOR);
    }
}
