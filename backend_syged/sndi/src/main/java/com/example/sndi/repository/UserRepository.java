package com.example.sndi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sndi.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String usernamen);

    Optional<User> findByUsername(String username);
}
