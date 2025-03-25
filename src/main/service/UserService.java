package main.service;

import main.model.User;
import main.util.CryptoUtils;
import main.util.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de gestion des utilisateurs (CRUD).
 */
public class UserService {
    
    /**
     * Crée un nouvel utilisateur.
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @param isAdmin Si l'utilisateur est administrateur
     * @return Vrai si la création a réussi
     */
    public static boolean createUser(String username, String password, boolean isAdmin) {
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
            System.err.println("Erreur de création: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère un utilisateur par son ID.
     * @param id ID de l'utilisateur
     * @return L'utilisateur trouvé ou null
     */
    public static User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("salt"),
                    rs.getBoolean("is_admin")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur de récupération: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Récupère tous les utilisateurs.
     * @return Liste de tous les utilisateurs
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("salt"),
                    rs.getBoolean("is_admin")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur de récupération: " + e.getMessage());
        }
        return users;
    }
    
    /**
     * Met à jour un utilisateur existant.
     * @param id ID de l'utilisateur
     * @param newUsername Nouveau nom d'utilisateur (null pour ne pas changer)
     * @param newPassword Nouveau mot de passe (null pour ne pas changer)
     * @param isAdmin Nouveau statut admin (null pour ne pas changer)
     * @return Vrai si la mise à jour a réussi
     */
    public static boolean updateUser(int id, String newUsername, String newPassword, Boolean isAdmin) {
        User user = getUserById(id);
        if (user == null) return false;
        
        try {
            String sql = "UPDATE users SET username = ?, password = ?, is_admin = ? WHERE id = ?";
            
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, newUsername != null ? newUsername : user.getUsername());
                
                if (newPassword != null && !newPassword.isEmpty()) {
                    String newHashedPassword = CryptoUtils.hashWithSalt(newPassword, user.getSalt());
                    pstmt.setString(2, newHashedPassword);
                } else {
                    pstmt.setString(2, user.getPasswordHash());
                }
                
                pstmt.setBoolean(3, isAdmin != null ? isAdmin : user.isAdmin());
                pstmt.setInt(4, id);
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("Erreur de mise à jour: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un utilisateur.
     * @param id ID de l'utilisateur à supprimer
     * @return Vrai si la suppression a réussi
     */
    public static boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur de suppression: " + e.getMessage());
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
