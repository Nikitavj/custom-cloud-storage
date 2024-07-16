package org.nikita.spingproject.filestorage.service;

import org.nikita.spingproject.filestorage.exception.UserAlreadyExistException;
import org.nikita.spingproject.filestorage.model.User;
import org.nikita.spingproject.filestorage.repository.UserRepository;
import org.nikita.spingproject.filestorage.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerNewUserAccount(UserDto userDto) {
        User user = buildUser(userDto);

        if(userExist(user)) {
            throw new UserAlreadyExistException("User " + userDto.getEmail() + " already exist");
        }

        user.setRole("ROLE_USER");
        return builUserDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    protected boolean userExist(User user) {
        return userRepository.existsUserByEmail(user.getEmail());
    }

    private User buildUser(UserDto userDto) {
        return new User()
                .setEmail(userDto.getEmail())
                .setPassword(passwordEncoder.encode(userDto.getPassword()));
    }

    private UserDto builUserDto(User user) {
        return new UserDto()
                .setEmail(user.getEmail());
    }
}
