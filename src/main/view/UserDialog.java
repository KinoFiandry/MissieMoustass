package main.view;

import main.model.User;
import main.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialogue de gestion d'un utilisateur (ajout/modification).
 */
public class UserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox adminCheckBox;
    private boolean userSaved = false;
    private final User existingUser;
    
    /**
     * Constructeur du dialogue utilisateur.
     * @param parent Fenêtre parente
     * @param user Utilisateur existant (null pour création)
     */
    public UserDialog(JFrame parent, User user) {
        super(parent, user == null ? "Nouvel utilisateur" : "Modifier utilisateur", true);
        this.existingUser = user;
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
    }
    
    /**
     * Initialise les composants de l'interface.
     */
    private void initializeComponents() {
        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Champs de formulaire
        mainPanel.add(new JLabel("Nom d'utilisateur:"));
        usernameField = new JTextField();
        mainPanel.add(usernameField);
        
        mainPanel.add(new JLabel("Mot de passe:"));
        passwordField = new JPasswordField();
        mainPanel.add(passwordField);
        
        mainPanel.add(new JLabel("Confirmer mot de passe:"));
        confirmPasswordField = new JPasswordField();
        mainPanel.add(confirmPasswordField);
        
        mainPanel.add(new JLabel("Administrateur:"));
        adminCheckBox = new JCheckBox();
        mainPanel.add(adminCheckBox);
        
        // Pré-remplir si modification
        if (existingUser != null) {
            usernameField.setText(existingUser.getUsername());
            adminCheckBox.setSelected(existingUser.isAdmin());
        }
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(this::saveUser);
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Ajout des composants
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Tente de sauvegarder l'utilisateur.
     * @param e Événement d'action
     */
    private void saveUser(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        boolean isAdmin = adminCheckBox.isSelected();
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Le nom d'utilisateur est requis", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (existingUser == null || !password.isEmpty()) {
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Le mot de passe est requis", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "Les mots de passe ne correspondent pas", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.length() < 8) {
                JOptionPane.showMessageDialog(this, 
                    "Le mot de passe doit contenir au moins 8 caractères", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        boolean success;
        if (existingUser == null) {
            success = UserService.createUser(username, password, isAdmin);
        } else {
            success = UserService.updateUser(
                existingUser.getId(), 
                username, 
                password.isEmpty() ? null : password, 
                isAdmin
            );
        }
        
        if (success) {
            userSaved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la sauvegarde", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * @return Vrai si l'utilisateur a été sauvegardé
     */
    public boolean isUserSaved() {
        return userSaved;
    }
}