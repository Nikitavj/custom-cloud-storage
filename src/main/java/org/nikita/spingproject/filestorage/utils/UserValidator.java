package org.nikita.spingproject.filestorage.utils;

import lombok.RequiredArgsConstructor;
import org.nikita.spingproject.filestorage.UserRepository;
import org.nikita.spingproject.filestorage.dto.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {
    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDto user = (UserDto) target;
        if(userRepository.existsUserByEmail(user.getEmail())) {
            errors.rejectValue(
                    "email",
                    "",
                    "User " + user.getEmail() + " already exist");
        }
    }
}
