
package main;

import main.view.MainFrame;

/**
 * Classe principale de l'application de messages vocaux sécurisés.
 * Point d'entrée du programme.
 */
public class Main {
    
    /**
     * Méthode principale lançant l'application.
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        // Initialiser la base de données
        main.util.DatabaseHelper.initializeDB();
        
        // Créer et afficher la fenêtre principale
        javax.swing.SwingUtilities.invokeLater(() -> {
          main.view.MainFrame frame = new main.view.MainFrame();
            frame.setVisible(true);
        });
    }
}
