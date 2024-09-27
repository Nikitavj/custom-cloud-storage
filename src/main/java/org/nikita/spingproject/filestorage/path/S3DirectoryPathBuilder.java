package org.nikita.spingproject.filestorage.path;

import jakarta.persistence.EntityNotFoundException;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.nikita.spingproject.filestorage.utils.PathEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class S3DirectoryPathBuilder extends S3PathBuilder {
    private static final String POSTFIX = "_meta";

    @Autowired
    public S3DirectoryPathBuilder(UserRepository userRepository) {
        super(userRepository);
    }

    public String buildPathMeta(String path) throws UnsupportedEncodingException {
        String pathS3 =  String.format("%s/%s%s",
                rootPathForUser(),
                path,
                POSTFIX);
        return PathEncoderUtil.encode(pathS3);
    }

    @Override
    public String buildPath(String path) throws UnsupportedEncodingException {
        if(path == null || path.isBlank()) {
            return PathEncoderUtil.encode(rootPathForUser());
        } else {
            String pathS3 =  String.format("%s/%s",
                    rootPathForUser(),
                    path);
            return PathEncoderUtil.encode(pathS3);
        }
    }
}
