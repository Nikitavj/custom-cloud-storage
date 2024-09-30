package org.nikita.spingproject.filestorage.path;

import jakarta.persistence.EntityNotFoundException;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.nikita.spingproject.filestorage.utils.PathEncoderUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.UnsupportedEncodingException;


public abstract class S3PathBuilder {
    private static final String USER_ROOT_PATH_FORMAT = "user-%s-files";
    private static final String SEPARATOR = "/";

    protected final UserRepository userRepository;

    public S3PathBuilder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    abstract String buildPath(String path) throws UnsupportedEncodingException;

    public String userPath() throws UnsupportedEncodingException {
        String rootPath = PathEncoderUtil.encode(rootPathForUser());
        return String.join("",
                rootPath,
                SEPARATOR);
    }

    protected String rootPathForUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User " + auth.getName() + "not exist"));
        return String.format(USER_ROOT_PATH_FORMAT, user.getId());
    }
}
