package main.model;

import java.util.Objects;

/**
 * Classe représentant un utilisateur de l'application.
 */
public class User {
    private final int id;
    private final String username;
    private final String passwordHash;
    private final String salt;
    private final String email;
    private final String nom;
    private final String prenom;
    private final boolean isAdmin;
    
    /**
     * Constructeur d'un utilisateur.
     * @param id Identifiant unique
     * @param username Nom d'utilisateur
     * @param passwordHash Mot de passe hashé
     * @param salt Sel utilisé pour le hachage
     * @param isAdmin Si l'utilisateur est administrateur
     */
    public User(int id, String username, String passwordHash, String salt, 
            String email, String nom, String prenom, boolean isAdmin) {
     this.id = id;
     this.username = username;
     this.passwordHash = passwordHash;
     this.salt = salt;
     this.email = email;
     this.nom = nom;
     this.prenom = prenom;
     this.isAdmin = isAdmin;
 }
    
    // Getters avec documentation Javadoc
    
    /**
     * @return L'identifiant de l'utilisateur
     */
    public int getId() { return id; }
    
    /**
     * @return Le nom d'utilisateur
     */
    public String getUsername() { return username; }
    
    public String getEmail() {
        return email;
    }
    
    public String getNom() {
        return nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    /**
     * @return Le hash du mot de passe
     */
   
    public String getPasswordHash() { return passwordHash; }
    
    /**
     * @return Le sel utilisé pour le hachage
     */
    public String getSalt() { return salt; }
    
    /**
     * @return Vrai si l'utilisateur est administrateur
     */
    public boolean isAdmin() { return isAdmin; }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}