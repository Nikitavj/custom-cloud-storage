package org.nikita.spingproject.filestorage.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nikita.spingproject.filestorage.account.User;
import org.nikita.spingproject.filestorage.account.UserDto;
import org.nikita.spingproject.filestorage.account.UserRepository;
import org.nikita.spingproject.filestorage.account.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@Testcontainers
class UserServiceImplTest {

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"));

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerNewUserAccount() {
        final String EMAIL = "user@gmail.com";
        final String PASSWORD = "root";

        UserDto userDto = new UserDto();
        userDto.setEmail(EMAIL);
        userDto.setPassword(PASSWORD);
        userDto.setMatchingPassword(PASSWORD);

        userService.registerNewUserAccount(userDto);
        Optional<User> userOpt = userRepository.findUserByEmail(EMAIL);
        assumeTrue(userOpt.isPresent());
        assertEquals(EMAIL, userOpt.get().getEmail());
    }
}