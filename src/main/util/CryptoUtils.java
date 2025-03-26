package main.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Classe utilitaire pour les opérations cryptographiques.
 */
public class CryptoUtils {
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String IV = "1234567890123456"; // 16 bytes
    
    /**
     * Chiffre une chaîne avec AES-256.
     * @param strToEncrypt Chaîne à chiffrer
     * @param secret Clé secrète
     * @param salt Sel pour le hachage
     * @return Chaîne chiffrée en Base64
     */
    public static String encrypt(String strToEncrypt, String secret, String salt) {
        try {
            byte[] iv = IV.getBytes();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.err.println("Erreur lors du chiffrement: " + e.toString());
            return null;
        }
    }
    
    /**
     * Déchiffre une chaîne avec AES-256.
     * @param strToDecrypt Chaîne chiffrée en Base64
     * @param secret Clé secrète
     * @param salt Sel utilisé pour le hachage initial
     * @return Chaîne déchiffrée
     */
    public static String decrypt(String strToDecrypt, String secret, String salt) {
        try {
            byte[] iv = IV.getBytes();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.err.println("Erreur lors du déchiffrement: " + e.toString());
            return null;
        }
    }
    
    /**
     * Génère un hash SHA-256 d'une chaîne.
     * @param input Chaîne à hacher
     * @return Hash hexadécimal
     * @throws NoSuchAlgorithmException Si SHA-256 n'est pas disponible
     */
    public static String generateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
    
    /**
     * Génère un sel aléatoire sécurisé.
     * @return Sel encodé en Base64
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hache une chaîne avec un sel donné.
     * @param data Données à hacher
     * @param salt Sel à utiliser
     * @return Hash encodé en Base64
     */
    public static String hashWithSalt(String data, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes());
            byte[] hash = digest.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de hachage", e);
        }
    }
    
    /**
     * Génère un hash d'un fichier.
     * @param filePath Chemin du fichier
     * @return Hash hexadécimal
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    public static String generateFileHash(String filePath) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath));
            byte[] hash = digest.digest(fileBytes);
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithme de hachage non disponible", e);
        }
    }
}