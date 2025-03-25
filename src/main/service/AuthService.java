package main.service;

import main.model.User;
import main.util.CryptoUtils;
import main.util.DatabaseHelper;
import java.sql.*;

/**
 * Service d'authentification et de gestion des utilisateurs.
 */
public class AuthService {
    private static User currentUser;
    
    /**
     * Authentifie un utilisateur.
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @return Vrai si l'authentification a réussi
     */
    public static boolean login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                String salt = rs.getString("salt");
                String inputHash = CryptoUtils.hashWithSalt(password, salt);
                
                if (inputHash.equals(storedHash)) {
                    currentUser = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        storedHash,
                        salt,
                        rs.getBoolean("is_admin")
                    );
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur d'authentification: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Déconnecte l'utilisateur actuel.
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * @return L'utilisateur actuellement connecté
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * @return Vrai si un utilisateur est authentifié
     */
    public static boolean isAuthenticated() {
        return currentUser != null;
    }
    
    /**
     * @return Vrai si l'utilisateur actuel est administrateur
     */
    public static boolean isAdminUser() {
        return isAuthenticated() && currentUser.isAdmin();
    }
    
    /**
     * Enregistre un nouvel utilisateur.
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @param isAdmin Si l'utilisateur est administrateur
     * @return Vrai si l'enregistrement a réussi
     */
    public static boolean register(String username, String password, boolean isAdmin) {
        if (usernameExists(username)) {
            return false;
        }

        try {
            String salt = CryptoUtils.generateSalt();
            String hashedPassword = CryptoUtils.hashWithSalt(password, salt);
            
            String sql = "INSERT INTO users(username, password, salt, is_admin) VALUES(?,?,?,?)";
            
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);
                pstmt.setString(3, salt);
                pstmt.setBoolean(4, isAdmin);
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("Erreur d'enregistrement: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean usernameExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Erreur de vérification: " + e.getMessage());
            return false;
        }
    }
}
