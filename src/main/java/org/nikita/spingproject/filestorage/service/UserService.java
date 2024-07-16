package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserDto registerNewUserAccount(UserDto userDto);
}
