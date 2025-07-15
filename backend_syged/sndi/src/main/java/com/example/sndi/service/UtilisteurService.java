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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.sndi.model.User;

import com.example.sndi.repository.UtilisateurRepository;


@Service
public class UtilisteurService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
/* 
    @PostConstruct
    public void initializeUserKeys() {
        attribuerClesATousLesUtilisateurs();
    } */

    // Créer un utilisateur
    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    // Convertir une clé en chaîne Base64
    private String encodeKeyToBase64(byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }

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

    public void attribuerClesATousLesUtilisateurs() {
        try {
            // Récupération de tous les utilisateurs
            List<User> utilisateurs = utilisateurRepository.findAll();

            System.out.println("Début de l'attribution des clés pour " + utilisateurs.size() + " utilisateurs");

            int compteur = 0;
            int erreurs = 0;

            for (User utilisateur : utilisateurs) {
                try {
                    // Vérifier si l'utilisateur a déjà des clés
                    if (utilisateur.getPublicKey() == null || utilisateur.getPrivateKey() == null) {

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
                        utilisateurRepository.save(utilisateur);

                        compteur++;
                        System.out.println("Clés attribuées à l'utilisateur: " + utilisateur.getUsername() + " ("
                                + compteur + "/" + utilisateurs.size() + ")");
                    } else {
                        System.out.println("L'utilisateur " + utilisateur.getUsername() + " possède déjà des clés");
                    }

                } catch (Exception e) {
                    erreurs++;
                    System.err.println("Erreur lors de l'attribution des clés pour l'utilisateur "
                            + utilisateur.getUsername() + ": " + e.getMessage());
                }
            }

            System.out
                    .println("Attribution terminée. " + compteur + " utilisateurs mis à jour, " + erreurs + " erreurs");

        } catch (Exception e) {
            System.err.println("Erreur globale lors de l'attribution des clés: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'attribution des clés à tous les utilisateurs", e);
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
            utilisateur.setPassword(utilisateurDetails.getPassword());
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
