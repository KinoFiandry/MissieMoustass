package main.view;

import main.util.AudioRecorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Dialogue pour l'enregistrement audio
 */
public class RecordDialog extends JDialog {
    private JButton recordButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private Timer recordingTimer;
    private int recordingSeconds = 0;
    private AudioRecorder audioRecorder;
    private File recordedFile;
    private boolean recordingSaved = false;
    
    public RecordDialog(JFrame parent) {
        super(parent, "Enregistrement Audio", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        
        audioRecorder = new AudioRecorder();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel de contrôle
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        recordButton = new JButton("Démarrer l'enregistrement");
        recordButton.addActionListener(this::toggleRecording);
        controlPanel.add(recordButton);
        
        // Barre de progression
        progressBar = new JProgressBar(0, 60); // Max 60 secondes
        progressBar.setStringPainted(true);
        progressBar.setString("00:00");
        
        // Barre de statut
        statusLabel = new JLabel("Prêt à enregistrer");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Ajout des composants
        add(controlPanel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        // Timer pour afficher la durée
        recordingTimer = new Timer(1000, e -> {
            recordingSeconds++;
            progressBar.setValue(recordingSeconds);
            progressBar.setString(String.format("%02d:%02d", 
                recordingSeconds / 60, 
                recordingSeconds % 60));
        });
    }
    
    private void toggleRecording(ActionEvent e) {
        if (!audioRecorder.isRecording()) {
            startRecording();
        } else {
            stopRecording();
        }
    }
    
    private void startRecording() {
        try {
            recordedFile = audioRecorder.startRecording();
            statusLabel.setText("Enregistrement en cours...");
            recordButton.setText("Arrêter l'enregistrement");
            
            // Réinitialiser et démarrer le timer
            recordingSeconds = 0;
            progressBar.setValue(0);
            recordingTimer.start();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du démarrage de l'enregistrement: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Indique si l'enregistrement a été sauvegardé
     * @return true si l'enregistrement a été sauvegardé, false sinon
     */
    public boolean isRecordingSaved() {
        return recordingSaved;
    }
    private void stopRecording() {
        audioRecorder.stopRecording();
        recordingTimer.stop();
        statusLabel.setText("Enregistrement terminé: " + recordedFile.getName());
        recordButton.setText("Démarrer un nouvel enregistrement");
        recordingSaved = true;
    }
    
    /**
     * @return Le fichier audio enregistré
     */
    public File getRecordedFile() {
        return recordedFile;
    }
    
    /**
     * @return Vrai si un enregistrement a été effectué
     */
    public boolean hasRecording() {
        return recordedFile != null;
    }
}