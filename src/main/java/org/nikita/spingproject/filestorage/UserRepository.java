package org.nikita.spingproject.filestorage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsUserByEmail(String email);
    Optional<User> findUserByEmail(String email);
}
