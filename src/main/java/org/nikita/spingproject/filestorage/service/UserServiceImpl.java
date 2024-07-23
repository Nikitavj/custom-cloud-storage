package org.nikita.spingproject.filestorage.service;

import io.minio.errors.*;
import jakarta.persistence.EntityNotFoundException;
import org.nikita.spingproject.filestorage.exception.UserAlreadyExistException;
import org.nikita.spingproject.filestorage.model.User;
import org.nikita.spingproject.filestorage.repository.UserRepository;
import org.nikita.spingproject.filestorage.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerNewUserAccount(UserDto userDto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        User user = buildUser(userDto);

        if (userExist(user)) {
            throw new UserAlreadyExistException("User " + userDto.getEmail() + " already exist");
        }
        user.setRole("ROLE_USER");
        return builUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userExist(User user) {
        return userRepository.existsUserByEmail(user.getEmail());
    }

    @Override
    public UserDto findUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User " + email + " not found."));
        return builUserDto(user);
    }

    private User buildUser(UserDto userDto) {
        return new User()
                .setEmail(userDto.getEmail())
                .setPassword(passwordEncoder.encode(userDto.getPassword()));
    }

    private UserDto builUserDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setEmail(user.getEmail());
    }

}
