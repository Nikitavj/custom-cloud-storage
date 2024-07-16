package org.nikita.spingproject.filestorage.repository;

import org.nikita.spingproject.filestorage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsUserByEmail(String email);
    Optional<User> findUserByEmail(String email);
}
