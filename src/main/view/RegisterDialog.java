package main.view;

import main.service.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialogue d'inscription d'un nouvel utilisateur.
 */
public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    
    /**
     * Constructeur du dialogue d'inscription.
     * @param loginDialog Fenêtre parente
     */
    public RegisterDialog(LoginDialog loginDialog) {
        super(loginDialog, "Inscription", true);
        setSize(400, 250);
        setLocationRelativeTo(loginDialog);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
    }
    
    /**
     * Initialise les composants de l'interface.
     */
    private void initializeComponents() {
        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Titre
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Création d'un nouveau compte", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(titleLabel, gbc);
        
        // Champs de formulaire
        gbc.gridwidth = 1;
        gbc.gridy++;
        mainPanel.add(new JLabel("Nom d'utilisateur:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Mot de passe:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Confirmer mot de passe:"), gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(15);
        mainPanel.add(confirmPasswordField, gbc);
        
        // Barre de statut
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        mainPanel.add(statusLabel, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton registerButton = new JButton("S'inscrire");
        registerButton.addActionListener(this::performRegistration);
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        // Ajout des composants
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Raccourci clavier pour Enter
        getRootPane().setDefaultButton(registerButton);
    }
    
    /**
     * Tente d'enregistrer un nouvel utilisateur.
     * @param e Événement d'action
     */
    private void performRegistration(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Les mots de passe ne correspondent pas");
            return;
        }
        
        if (password.length() < 8) {
            statusLabel.setText("Le mot de passe doit faire au moins 8 caractères");
            return;
        }
        
        if (AuthService.register(username, password, false)) {
            JOptionPane.showMessageDialog(this, 
                "Inscription réussie! Vous êtes maintenant connecté.", 
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            statusLabel.setText("Ce nom d'utilisateur est déjà pris");
        }
    }
}