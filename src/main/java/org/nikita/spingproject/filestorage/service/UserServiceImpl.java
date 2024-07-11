package org.nikita.spingproject.filestorage.service;

import lombok.RequiredArgsConstructor;
import org.nikita.spingproject.filestorage.User;
import org.nikita.spingproject.filestorage.UserRepository;
import org.nikita.spingproject.filestorage.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    @Override
    public UserDto registerNewUserAccount(UserDto userDto) {

        User user = userRepository.save(buildUser(userDto));
        return builUserDto(user);
    }

    private boolean userExist(UserDto user) {
        return userRepository.existsUserByEmail(user.getEmail());
    }

    private User buildUser(UserDto userDto) {
        return new User()
                .setEmail(userDto.getEmail())
                .setPassword(userDto.getPassword());
    }

    private UserDto builUserDto(User user) {
        return new UserDto()
                .setEmail(user.getEmail());
    }
}
