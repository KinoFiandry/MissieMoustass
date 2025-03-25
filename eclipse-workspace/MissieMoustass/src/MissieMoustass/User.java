package MissieMoustass;

import java.sql.*;

// Classe User pour représenter un utilisateur
public class User {
    private int id;
    private String nom;
    private String motDePasse;

    // Constructeur
    public User(int id, String nom, String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.motDePasse = motDePasse;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}

// Classe UserDAO pour gérer les opérations CRUD
class UserDAO {
    private static final String URL = "jdbc:sqlite:missie_moustass.db";

    // Méthode pour ajouter un utilisateur
    public static void ajouterUtilisateur(String nom, String motDePasse) {
        String sql = "INSERT INTO Utilisateurs (nom, mot_de_passe) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, motDePasse);
            pstmt.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer un utilisateur par son nom
    public static User getUtilisateur(String nom) {
        String sql = "SELECT * FROM Utilisateurs WHERE nom = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("nom"), rs.getString("mot_de_passe"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Méthode pour mettre à jour le mot de passe d'un utilisateur
    public static void mettreAJourMotDePasse(int id, String nouveauMotDePasse) {
        String sql = "UPDATE Utilisateurs SET mot_de_passe = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nouveauMotDePasse);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Mot de passe mis à jour avec succès");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour supprimer un utilisateur
    public static void supprimerUtilisateur(int id) {
        String sql = "DELETE FROM Utilisateurs WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Utilisateur supprimé avec succès");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
