package com.example.sndi.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.sndi.model.User;
import com.example.sndi.model.Utilisateur;
import com.example.sndi.repository.UtilisateurRepository;

@Service
public class UtilisteurService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

        @Autowired
    private PasswordEncoder passwordEncoder;

    // Créer un utilisateur
    public User createUtilisateur(User utilisateur) {
       
        String hasPassword = passwordEncoder.encode(utilisateur.getPassword());
        utilisateur.setPassword(hasPassword);
        return utilisateurRepository.save(utilisateur);
    }
    // Mettre à jour un utilisateur
    public User updateUtilisateur(Long id, User utilisateurDetails) {
        Optional<User> optionalUtilisateur = utilisateurRepository.findById(id);
        if (optionalUtilisateur.isPresent()) {
            User utilisateur = optionalUtilisateur.get();
            utilisateur.setName(utilisateurDetails.getName());
            utilisateur.setPrenom(utilisateurDetails.getPrenom());
            utilisateur.setContact(utilisateurDetails.getContact());
            utilisateur.setUsername(utilisateurDetails.getUsername());

            // Hacher le mot de passe avant de le sauvegarder
            if (utilisateurDetails.getPassword() != null && !utilisateurDetails.getPassword().isEmpty()) {
                utilisateur.setPassword(passwordEncoder.encode(utilisateurDetails.getPassword()));
            }

            utilisateur.setTypeUtilisateur(utilisateurDetails.getTypeUtilisateur());
            utilisateur.setDepartement(utilisateurDetails.getDepartement());
            utilisateur.setRoles(utilisateurDetails.getRoles());

            return utilisateurRepository.save(utilisateur);
        } else {
            return null; // Lancer une exception ou retourner une valeur appropriée selon votre besoin
        }
    }
    // Récupérer un utilisateur par son ID
    public Optional<User> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id);
    }

    // Récupérer tous les utilisateurs
    public List<User> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // Supprimer un utilisateur par son ID
    public void deleteUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    // Récupérer un utilisateur par son email
    public Optional<User> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByUsername(email);
    }

}

