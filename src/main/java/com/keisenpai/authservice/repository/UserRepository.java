package com.keisenpai.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keisenpai.authservice.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    boolean existsByLogin(String login);
    Optional<User> findByLogin(String login);

    Optional<User> findByVerificationToken(String verificationToken);
}
