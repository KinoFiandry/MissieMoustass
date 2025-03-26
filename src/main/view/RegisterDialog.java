package main.view;

import main.service.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField nomField;
    private JTextField prenomField;
    private JLabel statusLabel;
    
    public RegisterDialog(LoginDialog loginDialog) {
        super(loginDialog, "Inscription", true);
        setSize(450, 350); // Augmentation de la taille
        setLocationRelativeTo(loginDialog);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Panel principal avec GridBagLayout pour plus de flexibilité
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
        
        // Réinitialiser gridwidth
        gbc.gridwidth = 1;
        
        // Champs de formulaire
        addFormField(mainPanel, gbc, "Nom d'utilisateur:", usernameField = new JTextField(15), 1);
        addFormField(mainPanel, gbc, "Email:", emailField = new JTextField(15), 2);
        addFormField(mainPanel, gbc, "Nom:", nomField = new JTextField(15), 3);
        addFormField(mainPanel, gbc, "Prénom:", prenomField = new JTextField(15), 4);
        addFormField(mainPanel, gbc, "Mot de passe:", passwordField = new JPasswordField(15), 5);
        addFormField(mainPanel, gbc, "Confirmer mot de passe:", confirmPasswordField = new JPasswordField(15), 6);
        
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
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
    
    private void performRegistration(ActionEvent e) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation des champs
        if (username.isEmpty() || email.isEmpty() || nom.isEmpty() || prenom.isEmpty() || password.isEmpty()) {
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
        
        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            statusLabel.setText("Veuillez entrer un email valide");
            return;
        }
        
        if (AuthService.register(username, password, email, nom, prenom, false)) {
            JOptionPane.showMessageDialog(this, 
                "Inscription réussie! Vous êtes maintenant connecté.", 
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            statusLabel.setText("Ce nom d'utilisateur ou email est déjà utilisé");
        }
    }
}