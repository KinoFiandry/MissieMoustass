package main.view;

import main.util.AudioPlayer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Panel pour la lecture des messages audio
 */
public class PlaybackPanel extends JPanel {
    private AudioPlayer audioPlayer;
    private JButton playButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private Timer playbackTimer;
    private int playbackSeconds = 0;
    private File currentAudioFile;
    
    public PlaybackPanel() {
        audioPlayer = new AudioPlayer();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel de contrôle
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        playButton = new JButton("Lire");
        playButton.addActionListener(this::playAudio);
        playButton.setEnabled(false);
        
        stopButton = new JButton("Arrêter");
        stopButton.addActionListener(this::stopAudio);
        stopButton.setEnabled(false);
        
        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        
        // Barre de progression
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("00:00");
        
        // Barre de statut
        statusLabel = new JLabel("Aucun fichier sélectionné");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Ajout des composants
        add(controlPanel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        // Timer pour la progression de la lecture
        playbackTimer = new Timer(1000, e -> {
            if (audioPlayer.isPlaying()) {
                playbackSeconds++;
                progressBar.setString(String.format("%02d:%02d", 
                    playbackSeconds / 60, 
                    playbackSeconds % 60));
            }
        });
    }
    
    /**
     * Définit le fichier audio à lire
     * @param audioFile Fichier audio
     */
    public void setAudioFile(File audioFile) {
        this.currentAudioFile = audioFile;
        playButton.setEnabled(audioFile != null);
        statusLabel.setText(audioFile != null ? 
            "Prêt à lire: " + audioFile.getName() : 
            "Aucun fichier sélectionné");
        playbackSeconds = 0;
        progressBar.setValue(0);
        progressBar.setString("00:00");
    }
    
    private void playAudio(ActionEvent e) {
        if (currentAudioFile == null) return;
        
        try {
            audioPlayer.play(currentAudioFile);
            playButton.setEnabled(false);
            stopButton.setEnabled(true);
            statusLabel.setText("Lecture en cours...");
            playbackSeconds = 0;
            playbackTimer.start();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la lecture: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void stopAudio(ActionEvent e) {
        audioPlayer.stop();
        playButton.setEnabled(true);
        stopButton.setEnabled(false);
        statusLabel.setText("Lecture arrêtée");
        playbackTimer.stop();
    }
}