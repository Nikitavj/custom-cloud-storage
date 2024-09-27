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
public class S3FilePathBuilder extends S3PathBuilder{
    @Autowired
    public S3FilePathBuilder(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public String buildPath(String path) throws UnsupportedEncodingException {
        String pathS3 =  String.format("%s/%s",
                rootPathForUser(),
                path);
        return PathEncoderUtil.encode(pathS3);
    }
}
