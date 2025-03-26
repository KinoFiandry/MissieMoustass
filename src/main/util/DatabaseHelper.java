package main.util;

import java.io.File;
import java.sql.*;

/**
 * Classe utilitaire pour la gestion de la base de données SQLite.
 */
public class DatabaseHelper {
    private static final String DB_DIR = "resources/db/";
    private static final String DB_FILE = "voiceMessages.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_DIR + DB_FILE;
    
    static {
        // Créer les répertoires au chargement de la classe
        ensureDatabaseDirectoryExists();
    }
    
    /**
     * Vérifie et crée le répertoire de la base de données si nécessaire.
     */
    private static void ensureDatabaseDirectoryExists() {
        File dbDir = new File(DB_DIR);
        if (!dbDir.exists()) {
            boolean dirsCreated = dbDir.mkdirs();
            if (!dirsCreated) {
                System.err.println("Échec de la création du répertoire: " + dbDir.getAbsolutePath());
            }
        }
    }
    
    /**
     * Initialise la base de données et crée les tables si elles n'existent pas.
     * @throws RuntimeException Si l'initialisation échoue
     */
    public static void initializeDB() {
    	
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            createUserTable(stmt);
            createMessageTable(stmt);
            
        } catch (SQLException e) {
            throw new RuntimeException("Échec de l'initialisation de la base de données", e);
        }
    }
    
    private static void createUserTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
        		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "salt TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "nom TEXT NOT NULL," +
                "prenom TEXT NOT NULL," +
                "is_admin BOOLEAN NOT NULL DEFAULT 0)";
        stmt.execute(sql);
    }
    
    private static void createMessageTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS messages (" +
                      "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                      "user_id INTEGER NOT NULL," +
                      "filename TEXT NOT NULL," +
                      "file_path TEXT NOT NULL," +
                      "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                      "hash TEXT NOT NULL," +
                      "encrypted BOOLEAN NOT NULL DEFAULT 0," +
                      "FOREIGN KEY(user_id) REFERENCES users(id))";
        stmt.execute(sql);
    }
    
    /**
     * Obtient une connexion à la base de données.
     * @return Une connexion SQLite
     * @throws SQLException En cas d'erreur de connexion
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    /**
     * Vérifie si la base de données existe.
     * @return true si la base existe, false sinon
     */
    public static boolean databaseExists() {
        File dbFile = new File(DB_DIR + DB_FILE);
        return dbFile.exists();
    }
}