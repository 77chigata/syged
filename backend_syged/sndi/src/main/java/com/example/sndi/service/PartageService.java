package com.example.sndi.service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sndi.model.Document;
import com.example.sndi.model.Partage;
import com.example.sndi.model.User;
import com.example.sndi.repository.DocumentRepository;
import com.example.sndi.repository.PartageRepository;
import com.example.sndi.repository.UtilisateurRepository;

@Service
public class PartageService {
    @Autowired
    private PartageRepository partageRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UtilisateurRepository utlisateurRepository;

    public Partage save(Partage partage) throws Exception {

        Partage docPartage = partage;
        User userEmett = docPartage.getDestinataire();
        User emetteur = utlisateurRepository.findById(userEmett.getId())
                .orElseThrow(() -> new Exception("User not found with ID: " + userEmett.getId()));

        String clePublic = emetteur.getPublicKey();
        Document document = docPartage.getDocument();
        docPartage.setDocumentPartage(encryptId(document.getIdDocument(), clePublic));
        docPartage.setDatePartage(LocalDate.now());
        return partageRepository.save(docPartage);

    }

    public List<Partage> findByDestinataire(Long idDestinataire) throws Exception {
        List<Partage> partages = partageRepository.findByDestinataireId(idDestinataire);
        Map<Long, Document> cacheDocumentsDecryptes = new HashMap<>();

        for (Partage partage : partages) {
            User destinataire = partage.getDestinataire();
            String privateKey = destinataire.getPrivateKey();
            Long idDocument = decryptId(partage.getDocumentPartage(), privateKey);

            Document document;

            // Vérifie si le document a déjà été déchiffré
            if (cacheDocumentsDecryptes.containsKey(idDocument)) {
                document = cacheDocumentsDecryptes.get(idDocument);
            } else {
                document = documentRepository.findById(idDocument)
                        .orElseThrow(() -> new Exception("Document not found with ID: " + idDocument));

                // Déchiffrer le nom du document
                document.setNomDocument(decryptText(document.getNomDocument(), document.getClesymetrique()));

                // Déchiffrer le contenu du fichier
                String encryptedBase64 = Base64.getEncoder().encodeToString(document.getContenuFichier());
                String decryptedBase64 = decryptText(encryptedBase64, document.getClesymetrique());
                byte[] originalData = Base64.getDecoder().decode(decryptedBase64);
                document.setContenuFichier(originalData);

                // Stocke dans le cache
                cacheDocumentsDecryptes.put(idDocument, document);
            }

            partage.setDocument(document);
        }

        return partages;

    }

    public List<Partage> findByEmetteur(Long idEmetteur) throws Exception {
        List<Partage> partages = partageRepository.findByEmetteurId(idEmetteur);
        Map<Long, Document> cacheDocumentsDecryptes = new HashMap<>();

        for (Partage partage : partages) {
            User destinataire = partage.getDestinataire();
            String privateKey = destinataire.getPrivateKey();
            Long idDocument = decryptId(partage.getDocumentPartage(), privateKey);

            Document document;

            // Vérifie si le document a déjà été déchiffré
            if (cacheDocumentsDecryptes.containsKey(idDocument)) {
                document = cacheDocumentsDecryptes.get(idDocument);
            } else {
                document = documentRepository.findById(idDocument)
                        .orElseThrow(() -> new Exception("Document not found with ID: " + idDocument));

                // Déchiffrer le nom du document
                document.setNomDocument(decryptText(document.getNomDocument(), document.getClesymetrique()));

                // Déchiffrer le contenu du fichier
                String encryptedBase64 = Base64.getEncoder().encodeToString(document.getContenuFichier());
                String decryptedBase64 = decryptText(encryptedBase64, document.getClesymetrique());
                byte[] originalData = Base64.getDecoder().decode(decryptedBase64);
                document.setContenuFichier(originalData);

                // Stocke dans le cache
                cacheDocumentsDecryptes.put(idDocument, document);
            }

            partage.setDocument(document);
        }

        return partages;
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

    public String decryptText(String encryptedText, String encodedKey) throws Exception {
        // Décoder la clé Base64
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey secretKey = new SecretKeySpec(decodedKey, "AES");

        // Créer le cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Décoder le texte chiffré depuis Base64
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);

        // Déchiffrer
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // Retourner le texte en clair
        return new String(decryptedBytes, "UTF-8");
    }
}
