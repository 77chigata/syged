package com.example.sndi.service;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CleService {
    

private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    /**
     * Chiffre un texte avec une clé symétrique
     * @param plainText Le texte à chiffrer
     * @param base64Key La clé symétrique en Base64
     * @return Le texte chiffré avec IV, encodé en Base64
     * @throws Exception
     */
    public String encrypt(String plainText, String base64Key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(
            Base64.getDecoder().decode(base64Key), 
            ALGORITHM
        );

        // Générer un IV aléatoire
        byte[] iv = new byte[16];
        SecureRandom.getInstanceStrong().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Chiffrer
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedData = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Combiner IV + données chiffrées
        byte[] encryptedWithIv = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    /**
     * Déchiffre un texte avec une clé symétrique
     * @param encryptedText Le texte chiffré en Base64
     * @param base64Key La clé symétrique en Base64
     * @return Le texte déchiffré
     * @throws Exception
     */
    public String decrypt(String encryptedText, String base64Key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(
            Base64.getDecoder().decode(base64Key), 
            ALGORITHM
        );

        // Décoder les données chiffrées
        byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);

        // Extraire l'IV
        byte[] iv = new byte[16];
        System.arraycopy(encryptedWithIv, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extraire les données chiffrées
        byte[] encryptedData = new byte[encryptedWithIv.length - 16];
        System.arraycopy(encryptedWithIv, 16, encryptedData, 0, encryptedData.length);

        // Déchiffrer
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedData = cipher.doFinal(encryptedData);

        return new String(decryptedData, "UTF-8");
    }

    /**
     * Vérifie si une clé est valide
     * @param base64Key La clé en Base64
     * @return true si la clé est valide
     */
    public boolean isValidKey(String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            return keyBytes.length == 32; // AES-256 = 32 bytes
        } catch (Exception e) {
            return false;
        }
    }


}
