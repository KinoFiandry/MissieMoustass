package main.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {

    @Test
    void testGenerateHash() throws Exception {
        String input = "testPassword";
        String hash1 = CryptoUtils.generateHash(input);
        String hash2 = CryptoUtils.generateHash(input);
        
        assertEquals(hash1.length(), 64, "La longueur du hash SHA-256 doit être 64 caractères");
        assertEquals(hash1, hash2, "Le même input doit produire le même hash");
    }

    @Test
    void testGenerateSalt() {
        String salt1 = CryptoUtils.generateSalt();
        String salt2 = CryptoUtils.generateSalt();
        
        assertEquals(salt1.length(), 24, "La longueur du sel en Base64 doit être 24 caractères");
        assertNotEquals(salt1, salt2, "Deux sels générés doivent être différents");
    }

    @Test
    void testHashWithSalt() {
        String data = "testData";
        String salt = "testSalt";
        String hash = CryptoUtils.hashWithSalt(data, salt);
        
        assertNotNull(hash, "Le hash ne doit pas être null");
        assertEquals(hash.length(), 44, "La longueur du hash en Base64 doit être 44 caractères");
    }

    @Test
    void testEncryptDecrypt() {
        String original = "secretMessage";
        String password = "strongPassword";
        String salt = "randomSalt";
        
        String encrypted = CryptoUtils.encrypt(original, password, salt);
        String decrypted = CryptoUtils.decrypt(encrypted, password, salt);
        
        assertNotNull(encrypted, "Le texte chiffré ne doit pas être null");
        assertEquals(original, decrypted, "Le texte déchiffré doit correspondre à l'original");
    }

    @Test
    void testEncryptWithDifferentSalts() {
        String original = "secretMessage";
        String password = "strongPassword";
        
        String encrypted1 = CryptoUtils.encrypt(original, password, "salt1");
        String encrypted2 = CryptoUtils.encrypt(original, password, "salt2");
        
        assertNotEquals(encrypted1, encrypted2, "Différents sels doivent produire des chiffrements différents");
    }
}