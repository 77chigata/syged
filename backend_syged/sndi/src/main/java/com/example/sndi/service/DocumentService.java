package com.example.sndi.service;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sndi.model.Departement;
import com.example.sndi.model.Document;
import com.example.sndi.model.Projet;
import com.example.sndi.repository.DepartementRepository;
import com.example.sndi.repository.DocumentRepository;

@Service
public class DocumentService {
   
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ProjetService projetService;


    public List<Document> findAll() throws Exception{
       List<Document> documents = documentRepository.findAll();
    
    for (Document document : documents) {
        try {
            // Déchiffrer le nom du document s'il n'est pas null
            if (document.getNomDocument() != null) {
                document.setNomDocument(decryptText(document.getNomDocument(), document.getClesymetrique()));
            }
            
            // Déchiffrer le contenu du fichier s'il n'est pas null
            if (document.getContenuFichier() != null) {
                String decryptedContent = decryptText(document.getContenuFichier().toString(), document.getClesymetrique());
                document.setContenuFichier(Base64.getDecoder().decode(decryptedContent));
            }
            
        } catch (Exception e) {
            // Log l'erreur pour ce document spécifique
            System.err.println("Erreur lors du déchiffrement du document ID: " + 
                             document.getIdDocument() + " - " + e.getMessage());
            // Optionnel: continuer avec les autres documents ou relancer l'exception
            // throw e; // Décommentez si vous voulez arrêter sur la première erreur
        }
    }
    return documents;
}

    public Optional<Document>finById(Long Id){
        return documentRepository.findById(Id);
    }

    @Transactional
    public Document save( Document document) throws Exception{
             // Générer une clé symétrique AES
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // Taille de la clé : 256 bits
            SecretKey secretKey = keyGen.generateKey();
            // Convertir la clé en Base64 pour le stockage
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
          
            document.setNomDocument(encryptText(document.getNomDocument(), encodedKey));
            document.setContenuFichier((byte[]) Base64.getDecoder().decode(encryptText(document.getContenuFichier().toString(), encodedKey)));
            document.setClesymetrique(encodedKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de la génération de la clé symétrique", e);
        }

        getThis().beforeSave(document);
        Projet projet= projetService.findByNomProjet(document.getProjet().getNomProjet());
        document.getProjet().setIdProjet(projet.getIdProjet());
        return documentRepository.save(document);
    }

    @Transactional
    public Object process_document_request( Object payload){
        System.out.println("Document request: " + payload);
        return payload; // Placeholder for actual processing logic
    }

    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }
    
    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }

    @Transactional
    public Projet beforeSave(Document document ){
        return projetService.save(document.getProjet());
    }
        
    protected DocumentService getThis(){
        return this;
    }

    public  String encryptText(String plainText, String encodedKey) throws Exception {
        // Décoder la clé Base64
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey secretKey = new SecretKeySpec(decodedKey, "AES");
        
        // Créer le cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        // Chiffrer le texte
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        
        // Retourner le texte chiffré en Base64
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    // Méthode pour déchiffrer du texte avec une clé Base64
    public  String decryptText(String encryptedText, String encodedKey) throws Exception {
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
