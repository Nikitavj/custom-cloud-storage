package org.nikita.spingproject.filestorage.service;

import io.minio.errors.*;
import org.nikita.spingproject.filestorage.dto.UserDto;
import org.nikita.spingproject.filestorage.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public interface UserService {
    UserDto registerNewUserAccount(UserDto userDto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    @Transactional(readOnly = true)
    boolean userExist(User user);

    UserDto findUserByEmail(String email);
}
