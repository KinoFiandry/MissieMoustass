package main.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EncryptionDialog extends JDialog {
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private boolean confirmed = false;
    
    public EncryptionDialog(JFrame parent, boolean isEncryption) {
        super(parent, isEncryption ? "Chiffrer le message" : "Déchiffrer le message", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        
        initializeUI(isEncryption);
    }
    
    private void initializeUI(boolean isEncryption) {
        setLayout(new GridLayout(4, 1, 10, 10));
        
        JLabel instructionLabel = new JLabel(
            isEncryption ? 
            "Entrez un mot de passe pour le chiffrement:" : 
            "Entrez le mot de passe de déchiffrement:");
        add(instructionLabel);
        
        passwordField = new JPasswordField();
        add(passwordField);
        
        if (isEncryption) {
            confirmPasswordField = new JPasswordField();
            add(new JLabel("Confirmez le mot de passe:"));
            add(confirmPasswordField);
        }
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            if (validateInput(isEncryption)) {
                confirmed = true;
                dispose();
            }
        });
        add(okButton);
    }
    
    private boolean validateInput(boolean isEncryption) {
        String password = new String(passwordField.getPassword());
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Le mot de passe ne peut pas être vide", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (isEncryption) {
            String confirmPassword = new String(confirmPasswordField.getPassword());
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "Les mots de passe ne correspondent pas", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        return true;
    }
    
    public String getPassword() {
        return confirmed ? new String(passwordField.getPassword()) : null;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}