package main.view;

import main.service.AudioService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialogue d'enregistrement audio.
 */
public class RecordDialog extends JDialog {
    private JTextField nameField;
    private JProgressBar progressBar;
    private JButton stopButton;
    private boolean recordingSaved = false;
    
    /**
     * Constructeur du dialogue d'enregistrement.
     * @param parent Fenêtre parente
     */
    public RecordDialog(JFrame parent) {
        super(parent, "Nouvel enregistrement", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
    }
    
    /**
     * Initialise les composants de l'interface.
     */
    private void initializeComponents() {
        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Champ de nom
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new JLabel("Nom:"));
        nameField = new JTextField(20);
        namePanel.add(nameField);
        mainPanel.add(namePanel);
        
        // Barre de progression
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        mainPanel.add(progressBar);
        
        // Bouton d'arrêt
        stopButton = new JButton("Commencer l'enregistrement");
        stopButton.addActionListener(this::toggleRecording);
        mainPanel.add(stopButton);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Démarre ou arrête l'enregistrement.
     * @param e Événement d'action
     */
    private void toggleRecording(ActionEvent e) {
        if (stopButton.getText().equals("Commencer l'enregistrement")) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez entrer un nom pour l'enregistrement", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            stopButton.setText("Arrêter et sauvegarder");
            nameField.setEnabled(false);
            
            // Simuler l'enregistrement
            new Thread(() -> {
                for (int i = 0; i <= 100; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    progressBar.setValue(i);
                }
            }).start();
        } else {
            // Sauvegarder l'enregistrement
            try {
                AudioService.recordAndSaveMessage(nameField.getText().trim());
                recordingSaved = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'enregistrement: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * @return Vrai si l'enregistrement a été sauvegardé
     */
    public boolean isRecordingSaved() {
        return recordingSaved;
    }
}