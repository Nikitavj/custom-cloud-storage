package org.nikita.spingproject.filestorage.path;

import jakarta.persistence.EntityNotFoundException;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class S3FilePathBuilder {
    private final UserRepository userRepository;

    @Autowired
    public S3FilePathBuilder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String buildPath(String relativePath) {
        return String.format("%s/%s",
                rootPathForUser(),
                relativePath);
    }

    public String rootPathForUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("User " + auth.getName() + "not exist"));
        return String.format("user-%s-files", user.getId());
    }
}
