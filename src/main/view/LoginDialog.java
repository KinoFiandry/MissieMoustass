package main.view;


import main.service.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialogue d'authentification.
 */
public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    
    /**
     * Constructeur du dialogue de connexion.
     * @param parent Fenêtre parente
     */
    public LoginDialog(JFrame parent) {
        super(parent, "Connexion", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
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
        JLabel titleLabel = new JLabel("Connexion à l'application", JLabel.CENTER);
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
        
        // Barre de statut
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        mainPanel.add(statusLabel, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton loginButton = new JButton("Connexion");
        loginButton.addActionListener(this::performLogin);
        
        JButton registerButton = new JButton("S'inscrire");
        registerButton.addActionListener(this::showRegisterDialog);
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        // Ajout des composants
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Raccourci clavier pour Enter
        getRootPane().setDefaultButton(loginButton);
    }
    
    /**
     * Tente d'authentifier l'utilisateur.
     * @param e Événement d'action
     */
    private void performLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs");
            return;
        }
        
        if (AuthService.login(username, password)) {
            dispose();
        } else {
            statusLabel.setText("Identifiants incorrects");
            passwordField.setText("");
        }
    }
    
    /**
     * Affiche le dialogue d'inscription.
     * @param e Événement d'action
     */
    private void showRegisterDialog(ActionEvent e) {
        RegisterDialog registerDialog = new RegisterDialog(this);
        registerDialog.setVisible(true);
        
        if (AuthService.isAuthenticated()) {
            dispose();
        }
    }
}
