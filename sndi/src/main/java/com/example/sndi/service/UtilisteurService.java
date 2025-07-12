package com.example.sndi.service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
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

    


    // Générer une paire de clés asymétriques (RSA 2048 bits)
    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

 // Convertir une clé en chaîne Base64
    private String encodeKeyToBase64(byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }



 // Créer un utilisateur avec génération de clés asymétriques
    public User createUtilisateur(User utilisateur) {
        try {
            // Hachage du mot de passe
            String hashedPassword = passwordEncoder.encode(utilisateur.getPassword());
            utilisateur.setPassword(hashedPassword);
            
            // Génération de la paire de clés asymétriques
            KeyPair keyPair = generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            
            // Encodage des clés en Base64 pour stockage
            String publicKeyBase64 = encodeKeyToBase64(publicKey.getEncoded());
            String privateKeyBase64 = encodeKeyToBase64(privateKey.getEncoded());
            
            // Stockage des clés dans l'entité utilisateur
            utilisateur.setPublicKey(publicKeyBase64);
            utilisateur.setPrivateKey(privateKeyBase64);
            
            // Sauvegarde de l'utilisateur
            return utilisateurRepository.save(utilisateur);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de la génération des clés asymétriques", e);
        }
    }



    // Mettre à jour un utilisateur
    public User updateUtilisateur(Long id, User utilisateurDetails) {
        Optional<User> optionalUtilisateur = utilisateurRepository.findById(id);
        if (optionalUtilisateur.isPresent()) {
            User utilisateur = optionalUtilisateur.get();
            utilisateur.setName(utilisateurDetails.getName());
            utilisateur.setContact(utilisateurDetails.getContact());
            utilisateur.setUsername(utilisateurDetails.getUsername());
            
            // Hachage du nouveau mot de passe si fourni
            if (utilisateurDetails.getPassword() != null && !utilisateurDetails.getPassword().isEmpty()) {
                String hashedPassword = passwordEncoder.encode(utilisateurDetails.getPassword());
                utilisateur.setPassword(hashedPassword);
            }
            
            utilisateur.setTypeUtilisateur(utilisateurDetails.getTypeUtilisateur());
            utilisateur.setDepartement(utilisateurDetails.getDepartement());
            utilisateur.setRoles(utilisateurDetails.getRoles());
            
            // Ne pas mettre à jour les clés asymétriques lors de la mise à jour
            // Elles restent les mêmes pour l'utilisateur
            
            return utilisateurRepository.save(utilisateur);
        } else {
            return null;
        }
    }

    // Régénérer les clés asymétriques pour un utilisateur existant
    public User regenerateKeys(Long id) {
        Optional<User> optionalUtilisateur = utilisateurRepository.findById(id);
        if (optionalUtilisateur.isPresent()) {
            User utilisateur = optionalUtilisateur.get();
            
            try {
                // Génération de nouvelles clés
                KeyPair keyPair = generateKeyPair();
                PublicKey publicKey = keyPair.getPublic();
                PrivateKey privateKey = keyPair.getPrivate();
                
                // Encodage des nouvelles clés
                String publicKeyBase64 = encodeKeyToBase64(publicKey.getEncoded());
                String privateKeyBase64 = encodeKeyToBase64(privateKey.getEncoded());
                
                // Mise à jour des clés
                utilisateur.setPublicKey(publicKeyBase64);
                utilisateur.setPrivateKey(privateKeyBase64);
                
                return utilisateurRepository.save(utilisateur);
                
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Erreur lors de la régénération des clés asymétriques", e);
            }
        }
        return null;
    }

    // Récupérer la clé publique d'un utilisateur
    public String getPublicKey(Long id) {
        Optional<User> optionalUtilisateur = utilisateurRepository.findById(id);
        return optionalUtilisateur.map(User::getPublicKey).orElse(null);
    }



    // Créer un utilisateur
   /*  public User creatUtilisateur(User utilisateur) {
       
        String hasPassword = passwordEncoder.encode(utilisateur.getPassword());
        utilisateur.setPassword(hasPassword);
        return utilisateurRepository.save(utilisateur);
    }

    // Mettre à jour un utilisateur
    public User updatUtilisateur(Long id, User utilisateurDetails) {
        Optional<User> optionalUtilisateur = utilisateurRepository.findById(id);
        if (optionalUtilisateur.isPresent()) {
            User utilisateur = optionalUtilisateur.get();
            utilisateur.setName(utilisateurDetails.getName());
          
            utilisateur.setContact(utilisateurDetails.getContact());
            utilisateur.setUsername(utilisateurDetails.getUsername());
            utilisateur.setPassword(utilisateurDetails.getPassword());
            utilisateur.setTypeUtilisateur(utilisateurDetails.getTypeUtilisateur());
            utilisateur.setDepartement(utilisateurDetails.getDepartement());
            utilisateur.setRoles(utilisateurDetails.getRoles());
            return utilisateurRepository.save(utilisateur);
        } else {
            return null;  // Lancer une exception ou retourner une valeur appropriée selon votre besoin
        }
    }*/

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

