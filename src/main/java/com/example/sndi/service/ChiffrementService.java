package com.example.sndi.service;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import ch.qos.logback.classic.Logger;
import java.util.Arrays;


public class ChiffrementService {
    
private static final Logger logger = (Logger) LoggerFactory.getLogger(ChiffrementService.class);
    
    // ============== CONSTANTES DE CHIFFREMENT ==============
    
    // Algorithmes de chiffrement
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final String RSA_ALGORITHM = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
    private static final String AES_KEY_ALGORITHM = "AES";
    private static final String RSA_KEY_ALGORITHM = "RSA";
    private static final String HASH_ALGORITHM = "SHA-256";
    
    // Tailles et paramètres de sécurité
    private static final int AES_KEY_SIZE = 256;          // Taille de clé AES en bits
    private static final int RSA_KEY_SIZE = 2048;         // Taille de clé RSA en bits
    private static final int GCM_IV_LENGTH = 12;          // Longueur IV pour GCM (96 bits)
    private static final int GCM_TAG_LENGTH = 16;         // Longueur tag d'authentification GCM (128 bits)
    
    // Limites de sécurité
    private static final int MAX_DOCUMENT_SIZE = 100 * 1024 * 1024; // 100MB max
    private static final int MIN_DOCUMENT_SIZE = 1;                 // 1 byte min
    
    // ============== GÉNÉRATION DE CLÉS ==============
    
    /**
     * Génère une clé AES-256 cryptographiquement sécurisée
     * 
     * @return SecretKey - Nouvelle clé AES-256
     * @throws ChiffrementException si la génération échoue
     */
    public SecretKey generateAESKey() throws ChiffrementException {
        try {
            logger.debug("Génération d'une nouvelle clé AES-{}", AES_KEY_SIZE);
            
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_KEY_ALGORITHM);
            keyGenerator.init(AES_KEY_SIZE);
            SecretKey key = keyGenerator.generateKey();
            
            logger.info("Clé AES générée avec succès");
            return key;
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("Algorithme AES non disponible", e);
            throw new ChiffrementException("Erreur lors de la génération de clé AES", e);
        }
    }
    
    /**
     * Génère une paire de clés RSA-2048 pour un nouvel utilisateur
     * 
     * @return KeyPair - Nouvelle paire de clés RSA (publique et privée)
     * @throws ChiffrementException si la génération échoue
     */
    public KeyPair genererPaireClesRSA() throws ChiffrementException {
        try {
            logger.debug("Génération d'une nouvelle paire de clés RSA-{}", RSA_KEY_SIZE);
            
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
            keyPairGenerator.initialize(RSA_KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            logger.info("Paire de clés RSA générée avec succès");
            return keyPair;
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("Algorithme RSA non disponible", e);
            throw new ChiffrementException("Erreur lors de la génération de paire de clés RSA", e);
        }
    }
    
    // ============== CHIFFREMENT/DÉCHIFFREMENT DOCUMENTS ==============
    
    /**
     * Chiffre un document avec AES-256-GCM
     * 
     * @param documentData - Données du document à chiffrer
     * @param aesKey - Clé AES pour le chiffrement
     * @return DonneeChiffree - Objet contenant les données chiffrées, l'IV et le tag d'authentification
     * @throws ChiffrementException si le chiffrement échoue
     */
    public DonneeChiffree chiffrerDocument(byte[] documentData, SecretKey aesKey) throws ChiffrementException {
        // Validation des paramètres
        validerDonneesDocument(documentData);
        validerCleAES(aesKey);
        
        try {
            logger.debug("Chiffrement d'un document de {} bytes", documentData.length);
            
            // Initialisation du cipher AES-GCM
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            
            // Génération d'un IV unique et cryptographiquement sécurisé
            byte[] iv = genererIVSecurise();
            
            // Configuration du mode GCM avec IV et longueur de tag
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
            
            // Chiffrement des données avec authentification intégrée
            byte[] donneesChiffrees = cipher.doFinal(documentData);
            
            logger.info("Document chiffré avec succès ({} bytes -> {} bytes)", 
                       documentData.length, donneesChiffrees.length);
            
            return new DonneeChiffree(donneesChiffrees, iv);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chiffrement du document", e);
            throw new ChiffrementException("Échec du chiffrement du document", e);
        }
    }
    
    /**
     * Déchiffre un document avec AES-256-GCM et vérifie l'authentification
     * 
     * @param donneeChiffree - Objet contenant les données chiffrées et l'IV
     * @param aesKey - Clé AES pour le déchiffrement
     * @return byte[] - Données déchiffrées et authentifiées
     * @throws ChiffrementException si le déchiffrement échoue ou l'authentification est invalide
     */
    public byte[] dechiffrerDocument(DonneeChiffree donneeChiffree, SecretKey aesKey) throws ChiffrementException {
        // Validation des paramètres
        validerDonneeChiffree(donneeChiffree);
        validerCleAES(aesKey);
        
        try {
            logger.debug("Déchiffrement d'un document de {} bytes", 
                        donneeChiffree.getDonneesChiffrees().length);
            
            // Initialisation du cipher AES-GCM
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            
            // Configuration du mode GCM avec l'IV original
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, donneeChiffree.getIv());
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);
            
            // Déchiffrement avec vérification d'authentification automatique
            byte[] donneesDechiffrees = cipher.doFinal(donneeChiffree.getDonneesChiffrees());
            
            logger.info("Document déchiffré avec succès ({} bytes -> {} bytes)", 
                       donneeChiffree.getDonneesChiffrees().length, donneesDechiffrees.length);
            
            return donneesDechiffrees;
            
        } catch (AEADBadTagException e) {
            logger.error("Échec de l'authentification GCM - données corrompues ou clé incorrecte", e);
            throw new ChiffrementException("Authentification échouée - données corrompues ou clé incorrecte", e);
        } catch (Exception e) {
            logger.error("Erreur lors du déchiffrement du document", e);
            throw new ChiffrementException("Échec du déchiffrement du document", e);
        }
    }
    
    // ============== CHIFFREMENT/DÉCHIFFREMENT CLÉS AES ==============
    
    /**
     * Chiffre une clé AES avec la clé publique RSA d'un utilisateur
     * Permet le partage sécurisé de clés AES
     * 
     * @param aesKey - Clé AES à chiffrer
     * @param clePubliqueRSA - Clé publique RSA du destinataire
     * @return byte[] - Clé AES chiffrée
     * @throws ChiffrementException si le chiffrement échoue
     */
    public byte[] chiffrerCleAES(SecretKey aesKey, PublicKey clePubliqueRSA) throws ChiffrementException {
        // Validation des paramètres
        validerCleAES(aesKey);
        validerClePubliqueRSA(clePubliqueRSA);
        
        try {
            logger.debug("Chiffrement d'une clé AES avec RSA");
            
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, clePubliqueRSA);
            
            // Chiffrement de la clé AES (encodée en bytes)
            byte[] cleAESChiffree = cipher.doFinal(aesKey.getEncoded());
            
            logger.info("Clé AES chiffrée avec succès avec RSA");
            return cleAESChiffree;
            
        } catch (Exception e) {
            logger.error("Erreur lors du chiffrement de la clé AES", e);
            throw new ChiffrementException("Échec du chiffrement de la clé AES", e);
        }
    }
    
    /**
     * Déchiffre une clé AES avec la clé privée RSA d'un utilisateur
     * 
     * @param cleAESChiffree - Clé AES chiffrée
     * @param clePriveeRSA - Clé privée RSA de l'utilisateur
     * @return SecretKey - Clé AES déchiffrée
     * @throws ChiffrementException si le déchiffrement échoue
     */
    public SecretKey dechiffrerCleAES(byte[] cleAESChiffree, PrivateKey clePriveeRSA) throws ChiffrementException {
        // Validation des paramètres
        validerDonneesChiffrees(cleAESChiffree);
        validerClePriveeRSA(clePriveeRSA);
        
        try {
            logger.debug("Déchiffrement d'une clé AES avec RSA");
            
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, clePriveeRSA);
            
            // Déchiffrement de la clé AES
            byte[] cleBytes = cipher.doFinal(cleAESChiffree);
            
            // Reconstruction de la clé AES à partir des bytes
            SecretKey cleAES = new SecretKeySpec(cleBytes, AES_KEY_ALGORITHM);
            
            logger.info("Clé AES déchiffrée avec succès");
            return cleAES;
            
        } catch (Exception e) {
            logger.error("Erreur lors du déchiffrement de la clé AES", e);
            throw new ChiffrementException("Échec du déchiffrement de la clé AES", e);
        }
    }
    
    // ============== CONVERSION BASE64 ==============
    
    /**
     * Convertit une clé publique RSA en format Base64 pour stockage en base de données
     * 
     * @param clePublique - Clé publique RSA
     * @return String - Clé publique encodée en Base64
     * @throws ChiffrementException si la conversion échoue
     */
    public String clePubliqueVersBase64(PublicKey clePublique) throws ChiffrementException {
        validerClePubliqueRSA(clePublique);
        
        try {
            byte[] cleBytes = clePublique.getEncoded();
            String base64 = Base64.getEncoder().encodeToString(cleBytes);
            logger.debug("Clé publique RSA convertie en Base64 ({} bytes)", cleBytes.length);
            return base64;
        } catch (Exception e) {
            logger.error("Erreur lors de la conversion de la clé publique en Base64", e);
            throw new ChiffrementException("Échec de la conversion de la clé publique", e);
        }
    }
    
    /**
     * Convertit une clé privée RSA en format Base64 pour stockage sécurisé
     * 
     * @param clePrivee - Clé privée RSA
     * @return String - Clé privée encodée en Base64
     * @throws ChiffrementException si la conversion échoue
     */
    public String clePriveeVersBase64(PrivateKey clePrivee) throws ChiffrementException {
        validerClePriveeRSA(clePrivee);
        
        try {
            byte[] cleBytes = clePrivee.getEncoded();
            String base64 = Base64.getEncoder().encodeToString(cleBytes);
            logger.debug("Clé privée RSA convertie en Base64 ({} bytes)", cleBytes.length);
            return base64;
        } catch (Exception e) {
            logger.error("Erreur lors de la conversion de la clé privée en Base64", e);
            throw new ChiffrementException("Échec de la conversion de la clé privée", e);
        }
    }
    
    /**
     * Convertit une clé AES en format Base64 pour stockage temporaire
     * 
     * @param cleAES - Clé AES
     * @return String - Clé AES encodée en Base64
     * @throws ChiffrementException si la conversion échoue
     */
    public String cleAESVersBase64(SecretKey cleAES) throws ChiffrementException {
        validerCleAES(cleAES);
        
        try {
            byte[] cleBytes = cleAES.getEncoded();
            String base64 = Base64.getEncoder().encodeToString(cleBytes);
            logger.debug("Clé AES convertie en Base64 ({} bytes)", cleBytes.length);
            return base64;
        } catch (Exception e) {
            logger.error("Erreur lors de la conversion de la clé AES en Base64", e);
            throw new ChiffrementException("Échec de la conversion de la clé AES", e);
        }
    }
    
    // ============== RECONSTRUCTION DEPUIS BASE64 ==============
    
    /**
     * Reconstruit une clé publique RSA depuis sa représentation Base64
     * 
     * @param clePubliqueBase64 - Clé publique en Base64
     * @return PublicKey - Clé publique RSA reconstruite
     * @throws ChiffrementException si la reconstruction échoue
     */
    public PublicKey base64VersClePublique(String clePubliqueBase64) throws ChiffrementException {
        validerBase64(clePubliqueBase64, "clé publique");
        
        try {
            byte[] cleBytes = Base64.getDecoder().decode(clePubliqueBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(cleBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
            PublicKey clePublique = keyFactory.generatePublic(keySpec);
            
            logger.debug("Clé publique RSA reconstruite depuis Base64");
            return clePublique;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la reconstruction de la clé publique depuis Base64", e);
            throw new ChiffrementException("Échec de la reconstruction de la clé publique", e);
        }
    }
    
    /**
     * Reconstruit une clé privée RSA depuis sa représentation Base64
     * 
     * @param clePriveeBase64 - Clé privée en Base64
     * @return PrivateKey - Clé privée RSA reconstruite
     * @throws ChiffrementException si la reconstruction échoue
     */
    public PrivateKey base64VersClePrivee(String clePriveeBase64) throws ChiffrementException {
        validerBase64(clePriveeBase64, "clé privée");
        
        try {
            byte[] cleBytes = Base64.getDecoder().decode(clePriveeBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(cleBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
            PrivateKey clePrivee = keyFactory.generatePrivate(keySpec);
            
            logger.debug("Clé privée RSA reconstruite depuis Base64");
            return clePrivee;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la reconstruction de la clé privée depuis Base64", e);
            throw new ChiffrementException("Échec de la reconstruction de la clé privée", e);
        }
    }
    
    /**
     * Reconstruit une clé AES depuis sa représentation Base64
     * 
     * @param cleAESBase64 - Clé AES en Base64
     * @return SecretKey - Clé AES reconstruite
     * @throws ChiffrementException si la reconstruction échoue
     */
    public SecretKey base64VersCleAES(String cleAESBase64) throws ChiffrementException {
        validerBase64(cleAESBase64, "clé AES");
        
        try {
            byte[] cleBytes = Base64.getDecoder().decode(cleAESBase64);
            SecretKey cleAES = new SecretKeySpec(cleBytes, AES_KEY_ALGORITHM);
            
            logger.debug("Clé AES reconstruite depuis Base64");
            return cleAES;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la reconstruction de la clé AES depuis Base64", e);
            throw new ChiffrementException("Échec de la reconstruction de la clé AES", e);
        }
    }
    
    // ============== VÉRIFICATION D'INTÉGRITÉ ==============
    
    /**
     * Génère un hash SHA-256 d'un document pour vérification d'intégrité
     * 
     * @param donnees - Données à hasher
     * @return String - Hash SHA-256 en Base64
     * @throws ChiffrementException si la génération échoue
     */
    public String genererHashSHA256(byte[] donnees) throws ChiffrementException {
        validerDonneesDocument(donnees);
        
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(donnees);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);
            
            logger.debug("Hash SHA-256 généré pour {} bytes de données", donnees.length);
            return hashBase64;
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("Algorithme SHA-256 non disponible", e);
            throw new ChiffrementException("Erreur lors de la génération du hash SHA-256", e);
        }
    }
    
    /**
     * Vérifie l'intégrité d'un document en comparant son hash
     * 
     * @param donnees - Données à vérifier
     * @param hashAttendu - Hash attendu en Base64
     * @return boolean - true si l'intégrité est vérifiée
     * @throws ChiffrementException si la vérification échoue
     */
    public boolean verifierIntegrite(byte[] donnees, String hashAttendu) throws ChiffrementException {
        validerDonneesDocument(donnees);
        validerBase64(hashAttendu, "hash");
        
        try {
            String hashCalcule = genererHashSHA256(donnees);
            boolean integrite = hashCalcule.equals(hashAttendu);
            
            logger.debug("Vérification d'intégrité : {}", integrite ? "SUCCÈS" : "ÉCHEC");
            return integrite;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification d'intégrité", e);
            throw new ChiffrementException("Échec de la vérification d'intégrité", e);
        }
    }
    
    // ============== MÉTHODES UTILITAIRES PRIVÉES ==============
    
    /**
     * Génère un IV (Initialization Vector) cryptographiquement sécurisé
     * 
     * @return byte[] - IV aléatoire de 96 bits
     * @throws ChiffrementException si la génération échoue
     */
    private byte[] genererIVSecurise() throws ChiffrementException {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);
            return iv;
        } catch (NoSuchAlgorithmException e) {
            logger.error("SecureRandom non disponible", e);
            throw new ChiffrementException("Erreur lors de la génération de l'IV", e);
        }
    }
    
    // ============== MÉTHODES DE VALIDATION ==============
    
    private void validerDonneesDocument(byte[] donnees) throws ChiffrementException {
        if (donnees == null) {
            throw new ChiffrementException("Les données du document ne peuvent pas être nulles");
        }
        if (donnees.length < MIN_DOCUMENT_SIZE) {
            throw new ChiffrementException("Le document est trop petit (minimum " + MIN_DOCUMENT_SIZE + " byte)");
        }
        if (donnees.length > MAX_DOCUMENT_SIZE) {
            throw new ChiffrementException("Le document est trop volumineux (maximum " + MAX_DOCUMENT_SIZE + " bytes)");
        }
    }
    
    private void validerDonneeChiffree(DonneeChiffree donneeChiffree) throws ChiffrementException {
        if (donneeChiffree == null) {
            throw new ChiffrementException("Les données chiffrées ne peuvent pas être nulles");
        }
        if (donneeChiffree.getDonneesChiffrees() == null || donneeChiffree.getDonneesChiffrees().length == 0) {
            throw new ChiffrementException("Les données chiffrées sont vides");
        }
        if (donneeChiffree.getIv() == null || donneeChiffree.getIv().length != GCM_IV_LENGTH) {
            throw new ChiffrementException("L'IV est invalide (doit faire " + GCM_IV_LENGTH + " bytes)");
        }
    }
    
    private void validerDonneesChiffrees(byte[] donnees) throws ChiffrementException {
        if (donnees == null || donnees.length == 0) {
            throw new ChiffrementException("Les données chiffrées ne peuvent pas être nulles ou vides");
        }
    }
    
    private void validerCleAES(SecretKey cle) throws ChiffrementException {
        if (cle == null) {
            throw new ChiffrementException("La clé AES ne peut pas être nulle");
        }
        if (!AES_KEY_ALGORITHM.equals(cle.getAlgorithm())) {
            throw new ChiffrementException("La clé doit être de type AES");
        }
        if (cle.getEncoded().length * 8 != AES_KEY_SIZE) {
            throw new ChiffrementException("La clé AES doit faire " + AES_KEY_SIZE + " bits");
        }
    }
    
    private void validerClePubliqueRSA(PublicKey cle) throws ChiffrementException {
        if (cle == null) {
            throw new ChiffrementException("La clé publique RSA ne peut pas être nulle");
        }
        if (!RSA_KEY_ALGORITHM.equals(cle.getAlgorithm())) {
            throw new ChiffrementException("La clé doit être de type RSA");
        }
    }
    
    private void validerClePriveeRSA(PrivateKey cle) throws ChiffrementException {
        if (cle == null) {
            throw new ChiffrementException("La clé privée RSA ne peut pas être nulle");
        }
        if (!RSA_KEY_ALGORITHM.equals(cle.getAlgorithm())) {
            throw new ChiffrementException("La clé doit être de type RSA");
        }
    }
    
    private void validerBase64(String base64, String typeCle) throws ChiffrementException {
        if (!StringUtils.hasText(base64)) {
            throw new ChiffrementException("La " + typeCle + " en Base64 ne peut pas être vide");
        }
        try {
            Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            throw new ChiffrementException("Format Base64 invalide pour la " + typeCle, e);
        }
    }
    
    // ============== CLASSES INTERNES ==============
    
    /**
     * Classe pour encapsuler les données chiffrées avec leur IV
     * Garantit que l'IV est toujours associé aux données correspondantes
     */
    public static class DonneeChiffree {
        private final byte[] donneesChiffrees;
        private final byte[] iv;
        private final long timestamp;
        
        public DonneeChiffree(byte[] donneesChiffrees, byte[] iv) {
            this.donneesChiffrees = Arrays.copyOf(donneesChiffrees, donneesChiffrees.length);
            this.iv = Arrays.copyOf(iv, iv.length);
            this.timestamp = System.currentTimeMillis();
        }
        
        public byte[] getDonneesChiffrees() {
            return Arrays.copyOf(donneesChiffrees, donneesChiffrees.length);
        }
        
        public byte[] getIv() {
            return Arrays.copyOf(iv, iv.length);
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        /**
         * Convertit les données chiffrées en Base64 pour stockage
         * @return String - Données chiffrées en Base64
         */
        public String toBase64() {
            return Base64.getEncoder().encodeToString(donneesChiffrees);
        }
        
        /**
         * Convertit l'IV en Base64 pour stockage
         * @return String - IV en Base64
         */
        public String getIvBase64() {
            return Base64.getEncoder().encodeToString(iv);
        }
        
        /**
         * Méthode factory pour reconstruire depuis des données Base64
         * @param donneesBase64 - Données chiffrées en Base64
         * @param ivBase64 - IV en Base64
         * @return DonneeChiffree - Objet reconstruit
         */
        public static DonneeChiffree fromBase64(String donneesBase64, String ivBase64) {
            byte[] donnees = Base64.getDecoder().decode(donneesBase64);
            byte[] iv = Base64.getDecoder().decode(ivBase64);
            return new DonneeChiffree(donnees, iv);
        }
    }
    
    /**
     * Exception personnalisée pour les erreurs de chiffrement
     */
    public static class ChiffrementException extends Exception {
        public ChiffrementException(String message) {
            super(message);
        }
        
        public ChiffrementException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
}
