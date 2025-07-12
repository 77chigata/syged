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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sndi.model.User;
import com.example.sndi.model.Utilisateur;
import com.example.sndi.service.UtilisteurService;

@RestController
@RequestMapping("/utilisateur")
public class UtilisateurController {

    @Autowired
    private UtilisteurService utilisateurService;

     @Autowired
    private CryptoService cryptoService;


// Créer un utilisateur (avec génération automatique des clés asymétriques)
    @PostMapping("/create")
    public ResponseEntity<User> createUtilisateur(@RequestBody User utilisateur) {
        try {
            User createdUtilisateur = utilisateurService.createUtilisateur(utilisateur);
            // Ne pas retourner la clé privée dans la réponse pour des raisons de sécurité
            createdUtilisateur.setPrivateKey(null);
            return new ResponseEntity<>(createdUtilisateur, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Mettre à jour un utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUtilisateur(@PathVariable("id") Long id, @RequestBody User utilisateurDetails) {
        User updatedUtilisateur = utilisateurService.updateUtilisateur(id, utilisateurDetails);
        if (updatedUtilisateur != null) {
            // Ne pas retourner la clé privée dans la réponse
            updatedUtilisateur.setPrivateKey(null);
            return new ResponseEntity<>(updatedUtilisateur, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Récupérer un utilisateur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUtilisateurById(@PathVariable("id") Long id) {
        Optional<User> utilisateur = utilisateurService.getUtilisateurById(id);
        if (utilisateur.isPresent()) {
            User user = utilisateur.get();
            // Ne pas retourner la clé privée dans la réponse
            user.setPrivateKey(null);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Récupérer tous les utilisateurs
    @GetMapping("")
    public List<User> getAllUtilisateurs() {
        List<User> users = utilisateurService.getAllUtilisateurs();
        // Ne pas retourner les clés privées dans la réponse
        users.forEach(user -> user.setPrivateKey(null));
        return users;
    }

    // Supprimer un utilisateur par son ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable("id") Long id) {
        utilisateurService.deleteUtilisateur(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Récupérer un utilisateur par son email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUtilisateurByEmail(@PathVariable("email") String email) {
        Optional<User> utilisateur = utilisateurService.getUtilisateurByEmail(email);
        if (utilisateur.isPresent()) {
            User user = utilisateur.get();
            // Ne pas retourner la clé privée dans la réponse
            user.setPrivateKey(null);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ============== ENDPOINTS POUR LES CLÉS ASYMÉTRIQUES ==============

    // Récupérer la clé publique d'un utilisateur
    @GetMapping("/{id}/public-key")
    public ResponseEntity<String> getPublicKey(@PathVariable("id") Long id) {
        String publicKey = utilisateurService.getPublicKey(id);
        if (publicKey != null) {
            return new ResponseEntity<>(publicKey, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Régénérer les clés d'un utilisateur
    @PostMapping("/{id}/regenerate-keys")
    public ResponseEntity<String> regenerateKeys(@PathVariable("id") Long id) {
        try {
            User user = utilisateurService.regenerateKeys(id);
            if (user != null) {
                return new ResponseEntity<>("Clés régénérées avec succès", HttpStatus.OK);
            }
            return new ResponseEntity<>("Utilisateur non trouvé", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la régénération des clés", HttpStatus.BAD_REQUEST);
        }
    }

    // ============== ENDPOINTS POUR LE CHIFFREMENT ==============

    // Chiffrer un message avec la clé publique d'un utilisateur
    @PostMapping("/{id}/encrypt")
    public ResponseEntity<String> encryptMessage(@PathVariable("id") Long id, @RequestBody String message) {
        try {
            String publicKey = utilisateurService.getPublicKey(id);
            if (publicKey != null) {
                String encryptedMessage = cryptoService.encrypt(message, publicKey);
                return new ResponseEntity<>(encryptedMessage, HttpStatus.OK);
            }
            return new ResponseEntity<>("Utilisateur non trouvé", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors du chiffrement", HttpStatus.BAD_REQUEST);
        }
    }

    // Déchiffrer un message avec la clé privée d'un utilisateur
    @PostMapping("/{id}/decrypt")
    public ResponseEntity<String> decryptMessage(@PathVariable("id") Long id, @RequestBody String encryptedMessage) {
        try {
            Optional<User> userOpt = utilisateurService.getUtilisateurById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String decryptedMessage = cryptoService.encrypt(encryptedMessage, user.getPrivateKey());
                return new ResponseEntity<>(decryptedMessage, HttpStatus.OK);
            }
            return new ResponseEntity<>("Utilisateur non trouvé", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors du déchiffrement", HttpStatus.BAD_REQUEST);
        }
    }

    // ============== ENDPOINTS POUR LA SIGNATURE ==============

    // Signer un message avec la clé privée d'un utilisateur
    @PostMapping("/{id}/sign")
    public ResponseEntity<String> signMessage(@PathVariable("id") Long id, @RequestBody String message) {
        try {
            Optional<User> userOpt = utilisateurService.getUtilisateurById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String signature = cryptoService.sign(message, user.getPrivateKey());
                return new ResponseEntity<>(signature, HttpStatus.OK);
            }
            return new ResponseEntity<>("Utilisateur non trouvé", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la signature", HttpStatus.BAD_REQUEST);
        }
    }

    // Vérifier une signature avec la clé publique d'un utilisateur
    @PostMapping("/{id}/verify")
    public ResponseEntity<Boolean> verifySignature(@PathVariable("id") Long id, 
                                                   @RequestParam String message,
                                                   @RequestParam String signature) {
        try {
            String publicKey = utilisateurService.getPublicKey(id);
            if (publicKey != null) {
                boolean isValid = cryptoService.verify(message, signature, publicKey);
                return new ResponseEntity<>(isValid, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
}


    /* // Créer un utilisateur
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
*/

