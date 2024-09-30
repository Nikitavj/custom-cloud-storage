package org.nikita.spingproject.filestorage.account;

import org.nikita.spingproject.filestorage.account.exception.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerNewUserAccount(UserDto userDto) {
        User user = buildUser(userDto);

        if (userExist(user)) {
            throw new UserAlreadyExistException(
                    "User " + userDto.getEmail() + " already exist");
        }
        user.setRole("ROLE_USER");
        buildUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userExist(User user) {
        return userRepository.existsUserByEmail(user.getEmail());
    }

    private User buildUser(UserDto userDto) {
        return new User()
                .setEmail(userDto.getEmail())
                .setPassword(passwordEncoder.encode(userDto.getPassword()));
    }

    private void buildUserDto(User user) {
        new UserDto()
                .setId(user.getId())
                .setEmail(user.getEmail());
    }
}
