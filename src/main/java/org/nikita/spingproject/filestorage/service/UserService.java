package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.dto.UserDto;

public interface UserService {
    UserDto registerNewUserAccount(UserDto userDto);
}
