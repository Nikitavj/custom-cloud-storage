package org.nikita.spingproject.filestorage.service;

import lombok.RequiredArgsConstructor;
import org.nikita.spingproject.filestorage.User;
import org.nikita.spingproject.filestorage.UserRepository;
import org.nikita.spingproject.filestorage.dto.UserDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    @Override
    public UserDto registerNewUserAccount(UserDto userDto) {

        User user = userRepository.save(buildUser(userDto));
        return builUserDto(user);
    }

    @Transactional(readOnly = true)
    protected boolean userExist(UserDto user) {
        return userRepository.existsUserByEmail(user.getEmail());
    }

    private User buildUser(UserDto userDto) {
        return new User()
                .setEmail(userDto.getEmail())
                .setPassword(passwordEncoder(userDto.getPassword()));
    }

    private UserDto builUserDto(User user) {
        return new UserDto()
                .setEmail(user.getEmail());
    }

    private String passwordEncoder(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
