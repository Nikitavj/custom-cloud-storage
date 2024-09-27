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


public abstract class S3PathBuilder {
    protected final UserRepository userRepository;

    public S3PathBuilder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    abstract String buildPath(String path) throws UnsupportedEncodingException;

    public String rootPath() throws UnsupportedEncodingException {
        return PathEncoderUtil.encode(rootPathForUser());
    }

    protected String rootPathForUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("User " + auth.getName() + "not exist"));
        return String.format("user-%s-files", user.getId());
    }
}
