package com.example.sndi.service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sndi.model.Document;
import com.example.sndi.model.Partage;
import com.example.sndi.model.User;
import com.example.sndi.repository.PartageRepository;
import com.example.sndi.repository.UtilisateurRepository;

@Service
public class PartageService {
    @Autowired
    private PartageRepository partageRepository;

    @Autowired
    private UtilisateurRepository utlisateurRepository;

    public Partage save(Partage partage) throws Exception {

        Partage docPartage = partage;
        User userEmett = docPartage.getEmetteur();
        User emetteur = utlisateurRepository.findById(userEmett.getId())
                .orElseThrow(() -> new Exception("User not found with ID: " + userEmett.getId()));

        String clePublic = emetteur.getPublicKey();
        Document document = docPartage.getDocument();
        docPartage.setDocumentPartage( encryptId(document.getIdDocument(), clePublic));

        return partageRepository.save(docPartage);

    }

    // Chiffrer l'ID avec la clé publique
    public String encryptId(Long documentId, String publicKeyString) throws Exception {
        // Convertir l'ID en string
        String idString = documentId.toString();

        // Décoder la clé publique
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        // Chiffrer
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(idString.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Déchiffrer l'ID avec la clé privée
    public Long decryptId(String encryptedId, String privateKeyString) throws Exception {
        // Décoder la clé privée
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        // Déchiffrer
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedId);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        String decryptedIdString = new String(decryptedBytes, "UTF-8");
        return Long.parseLong(decryptedIdString);
    }
}
