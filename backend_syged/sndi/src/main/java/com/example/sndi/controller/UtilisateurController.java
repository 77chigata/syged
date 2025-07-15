package com.example.sndi.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sndi.model.User;

import com.example.sndi.service.UtilisteurService;

@RestController
@RequestMapping("/utilisateur")
public class UtilisateurController {

    @Autowired
    private UtilisteurService utilisateurService;

    // Créer un utilisateur
    @PostMapping("/create")
    public ResponseEntity<User> createUtilisateur(@RequestBody User utilisateur) {
        User createdUtilisateur = utilisateurService.createUtilisateur(utilisateur);
        return new ResponseEntity<>(createdUtilisateur, HttpStatus.CREATED);
    }

    // Mettre à jour un utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUtilisateur(@PathVariable("id") Long id, @RequestBody User utilisateurDetails) {
        User updatedUtilisateur = utilisateurService.updateUtilisateur(id, utilisateurDetails);
        if (updatedUtilisateur != null) {
            return new ResponseEntity<>(updatedUtilisateur, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Utilisateur non trouvé
        }
    }

    // Récupérer un utilisateur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUtilisateurById(@PathVariable("id") Long id) {
        Optional<User> utilisateur = utilisateurService.getUtilisateurById(id);
        return utilisateur.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));  // Utilisateur non trouvé
    }

    // Récupérer tous les utilisateurs
    @GetMapping("")
    public List<User> getAllUtilisateurs() {
        
        return utilisateurService.getAllUtilisateurs();
    }

    // Supprimer un utilisateur par son ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable("id") Long id) {
        utilisateurService.deleteUtilisateur(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Suppression réussie
    }

    // Récupérer un utilisateur par son email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUtilisateurByEmail(@PathVariable("email") String email) {
        Optional<User> utilisateur = utilisateurService.getUtilisateurByEmail(email);
        return utilisateur.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));  // Utilisateur non trouvé
    }
}


