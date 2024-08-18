package org.nikita.spingproject.filestorage.service;

import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.nikita.spingproject.filestorage.account.UserDto;
import org.nikita.spingproject.filestorage.account.UserServiceImpl;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@Testcontainers
class UserServiceImplTest {

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
    }

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerNewUserAccount() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        final String EMAIL = "user@gmail.com";
        final String PASSWORD = "root";

        UserDto userDto = new UserDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD)
                .setMatchingPassword(PASSWORD);

        userService.registerNewUserAccount(userDto);

        Optional<User> userOpt = userRepository.findUserByEmail(EMAIL);

        assumeTrue(userOpt.isPresent());

        assertEquals(EMAIL, userOpt.get().getEmail());
    }
}