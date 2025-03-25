package main.service;


import main.model.VoiceMessage;
import main.util.AudioRecorder;
import main.util.CryptoUtils;
import main.util.DatabaseHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de gestion des enregistrements audio
 */
public class AudioService {
    private static final String AUDIO_DIR = "resources/audio/";
    
    /**
     * Enregistre un nouveau message vocal
     * @param messageName Nom du message
     * @return Le message vocal créé
     * @throws IOException En cas d'erreur d'écriture
     */
    public static VoiceMessage recordMessage(String messageName) throws IOException {
        if (!AuthService.isAuthenticated()) {
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        
        Files.createDirectories(Paths.get(AUDIO_DIR));
        
        // Générer un nom de fichier unique
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = sanitizeFilename(messageName) + "_" + timestamp + ".wav";
        String filePath = AUDIO_DIR + filename;
        
        // Enregistrement (sera géré par RecordDialog)
        File audioFile = new File(filePath);
        
        // Calculer le hash
        String hash = CryptoUtils.generateFileHash(filePath);
        int userId = AuthService.getCurrentUser().getId();
        
        // Sauvegarder dans la base de données
        return saveVoiceMessageToDB(userId, filename, filePath, hash, false);
    }
    
    /**
     * Sauvegarde un fichier audio existant
     * @param sourceFile Fichier source
     * @param messageName Nom du message
     * @return Le message vocal créé
     * @throws IOException En cas d'erreur de copie
     */
    public static VoiceMessage saveAudioFile(File sourceFile, String messageName) throws IOException {
        if (!AuthService.isAuthenticated()) {
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        
        Files.createDirectories(Paths.get(AUDIO_DIR));
        
        // Générer un nom de fichier unique
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = sanitizeFilename(messageName) + "_" + timestamp + ".wav";
        String filePath = AUDIO_DIR + filename;
        
        // Copier le fichier dans le dossier de l'application
        Files.copy(sourceFile.toPath(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        
        // Calculer le hash
        String hash = CryptoUtils.generateFileHash(filePath);
        int userId = AuthService.getCurrentUser().getId();
        
        // Sauvegarder dans la base de données
        return saveVoiceMessageToDB(userId, filename, filePath, hash, false);
    }
    
    private static String sanitizeFilename(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
    
    private static VoiceMessage saveVoiceMessageToDB(int userId, String filename, 
            String filePath, String hash, boolean encrypted) {
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
            System.err.println("Erreur de sauvegarde: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Récupère les messages vocaux de l'utilisateur courant.
     * @return Liste des messages vocaux
     */
    public static List<VoiceMessage> getUserMessages() {
        List<VoiceMessage> messages = new ArrayList<>();
        
        if (!AuthService.isAuthenticated()) {
            return messages;
        }
        
        String sql = "SELECT * FROM messages WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, AuthService.getCurrentUser().getId());
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
     * Supprime un message vocal.
     * @param messageId ID du message à supprimer
     * @return Vrai si la suppression a réussi
     */
    public static boolean deleteMessage(int messageId) {
        String sql = "DELETE FROM messages WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, messageId);
            pstmt.setInt(2, AuthService.getCurrentUser().getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur de suppression: " + e.getMessage());
            return false;
        }
    }
}
