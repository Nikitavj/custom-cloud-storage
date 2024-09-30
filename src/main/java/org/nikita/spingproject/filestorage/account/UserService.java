package org.nikita.spingproject.filestorage.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UserService {
    void registerNewUserAccount(UserDto userDto);

    @Transactional(readOnly = true)
    boolean userExist(User user);
}
