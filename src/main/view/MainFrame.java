package main.view;

import main.model.VoiceMessage;
import main.service.AudioService;
import main.service.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Fenêtre principale de l'application.
 */
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private AudioPanel audioPanel;
    private UserManagementPanel userManagementPanel;
    
    /**
     * Constructeur de la fenêtre principale.
     */
    public MainFrame() {
        super("Application de Messages Vocaux Sécurisés");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        initializeUI();
        checkAuthentication();
    }
    
    /**
     * Initialise l'interface utilisateur.
     */
    private void initializeUI() {
        tabbedPane = new JTabbedPane();
        
        // Onglet Audio
        audioPanel = new AudioPanel();
        tabbedPane.addTab("Messages Vocaux", audioPanel);
        
        // Onglet Administration (si admin)
        if (AuthService.isAdminUser()) {
            userManagementPanel = new UserManagementPanel();
            tabbedPane.addTab("Gestion Utilisateurs", userManagementPanel);
        }
        
        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Fichier");
        
        JMenuItem logoutItem = new JMenuItem("Déconnexion");
        logoutItem.addActionListener(this::logout);
        fileMenu.add(logoutItem);
        
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        add(tabbedPane);
    }
    
    /**
     * Vérifie l'authentification de l'utilisateur.
     */
    private void checkAuthentication() {
        if (!AuthService.isAuthenticated()) {
            LoginDialog loginDialog = new LoginDialog(this);
            loginDialog.setVisible(true);
            
            if (!AuthService.isAuthenticated()) {
                System.exit(0);
            } else {
                // Reconstruire l'interface après authentification
                getContentPane().removeAll();
                initializeUI();
                revalidate();
                repaint();
            }
        }
    }
    
    /**
     * Gère la déconnexion.
     * @param e Événement d'action
     */
    private void logout(ActionEvent e) {
        AuthService.logout();
        checkAuthentication();
    }
    
    /**
     * Rafraîchit la liste des messages audio.
     */
    public void refreshAudioMessages() {
        audioPanel.refreshMessagesList();
    }
}
