package com.jobquest.repository;

import com.jobquest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find a user by their unique username. */
    Optional<User> findByUsername(String username);

    /** Check whether a username is already taken. */
    boolean existsByUsername(String username);
}
