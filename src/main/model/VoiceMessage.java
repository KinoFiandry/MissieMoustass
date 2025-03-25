package main.model;

import java.time.LocalDateTime;

/**
 * Classe représentant un message vocal enregistré dans l'application.
 */
public class VoiceMessage {
    private final int id;
    private final int userId;
    private final String filename;
    private final String filePath;
    private final LocalDateTime createdAt;
    private final String hash;
    private final boolean encrypted;
    
    /**
     * Constructeur d'un message vocal.
     * @param id Identifiant unique
     * @param userId ID de l'utilisateur propriétaire
     * @param filename Nom du fichier
     * @param filePath Chemin d'accès complet
     * @param createdAt Date de création
     * @param hash Hash de vérification
     * @param encrypted Si le message est chiffré
     */
    public VoiceMessage(int id, int userId, String filename, String filePath, 
                      LocalDateTime createdAt, String hash, boolean encrypted) {
        this.id = id;
        this.userId = userId;
        this.filename = filename;
        this.filePath = filePath;
        this.createdAt = createdAt;
        this.hash = hash;
        this.encrypted = encrypted;
    }
    
    // Getters avec documentation Javadoc
    
    /**
     * @return L'identifiant du message
     */
    public int getId() { return id; }
    
    /**
     * @return L'identifiant de l'utilisateur propriétaire
     */
    public int getUserId() { return userId; }
    
    /**
     * @return Le nom du fichier
     */
    public String getFilename() { return filename; }
    
    /**
     * @return Le chemin complet du fichier
     */
    public String getFilePath() { return filePath; }
    
    /**
     * @return La date de création du message
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    /**
     * @return Le hash de vérification d'intégrité
     */
    public String getHash() { return hash; }
    
    /**
     * @return Vrai si le message est chiffré
     */
    public boolean isEncrypted() { return encrypted; }
}
