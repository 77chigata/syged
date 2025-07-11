package com.example.sndi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sndi.model.User;
import com.example.sndi.model.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<User, Long> 
{
       boolean existsByUsername(String usernamen);
       // Méthode pour récupérer un utilisateur par son email
       Optional<User> findByUsername(String username);
}
