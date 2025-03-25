package main.service;

import main.model.VoiceMessage;
import main.util.CryptoUtils;
import main.util.DatabaseHelper;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de gestion des messages vocaux (CRUD).
 */
public class MessageService {
    
    /**
     * Crée un nouveau message vocal.
     * @param userId ID de l'utilisateur
     * @param filename Nom du fichier
     * @param filePath Chemin du fichier
     * @param encrypted Si le message est chiffré
     * @return Le message créé
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    public static VoiceMessage createMessage(int userId, String filename, String filePath, 
                                           boolean encrypted) throws IOException {
        String hash = CryptoUtils.generateFileHash(filePath);
        
        String sql = "INSERT INTO messages(user_id, filename, file_path, hash, encrypted) " +
                     "VALUES(?,?,?,?,?)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, filename);
            pstmt.setString(3, filePath);
            pstmt.setString(4, hash);
            pstmt.setBoolean(5, encrypted);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return new VoiceMessage(
                            rs.getInt(1),
                            userId,
                            filename,
                            filePath,
                            LocalDateTime.now(),
                            hash,
                            encrypted
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de création: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Récupère un message par son ID.
     * @param messageId ID du message
     * @return Le message trouvé ou null
     */
    public static VoiceMessage getMessageById(int messageId) {
        String sql = "SELECT * FROM messages WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, messageId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new VoiceMessage(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("filename"),
                    rs.getString("file_path"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("hash"),
                    rs.getBoolean("encrypted")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur de récupération: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Récupère tous les messages d'un utilisateur.
     * @param userId ID de l'utilisateur
     * @return Liste des messages
     */
    public static List<VoiceMessage> getUserMessages(int userId) {
        List<VoiceMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                messages.add(new VoiceMessage(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("filename"),
                    rs.getString("file_path"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("hash"),
                    rs.getBoolean("encrypted")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur de récupération: " + e.getMessage());
        }
        return messages;
    }
    
    /**
     * Récupère tous les messages (admin seulement).
     * @return Liste de tous les messages
     */
    public static List<VoiceMessage> getAllMessages() {
        List<VoiceMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                messages.add(new VoiceMessage(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("filename"),
                    rs.getString("file_path"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("hash"),
                    rs.getBoolean("encrypted")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur de récupération: " + e.getMessage());
        }
        return messages;
    }
    
    /**
     * Met à jour un message existant.
     * @param messageId ID du message
     * @param newFilename Nouveau nom de fichier (null pour ne pas changer)
     * @param encrypted Nouveau statut de chiffrement (null pour ne pas changer)
     * @return Vrai si la mise à jour a réussi
     */
    public static boolean updateMessage(int messageId, String newFilename, Boolean encrypted) {
        VoiceMessage message = getMessageById(messageId);
        if (message == null) return false;
        
        String sql = "UPDATE messages SET filename = ?, encrypted = ? WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newFilename != null ? newFilename : message.getFilename());
            pstmt.setBoolean(2, encrypted != null ? encrypted : message.isEncrypted());
            pstmt.setInt(3, messageId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur de mise à jour: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un message.
     * @param messageId ID du message à supprimer
     * @return Vrai si la suppression a réussi
     */
    public static boolean deleteMessage(int messageId) {
        VoiceMessage message = getMessageById(messageId);
        if (message == null) return false;
        
        String sql = "DELETE FROM messages WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, messageId);
            boolean dbSuccess = pstmt.executeUpdate() > 0;
            
            if (dbSuccess) {
                try {
                    java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(message.getFilePath()));
                    return true;
                } catch (IOException e) {
                    System.err.println("Erreur de suppression du fichier: " + e.getMessage());
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Erreur de suppression: " + e.getMessage());
            return false;
        }
    }
}