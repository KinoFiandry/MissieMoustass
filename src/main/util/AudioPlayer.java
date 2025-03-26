package main.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Classe pour la lecture de fichiers audio
 */
public class AudioPlayer {
    private Clip audioClip;
    private boolean isPlaying = false;
    
    /**
     * Joue un fichier audio
     * @param audioFile Fichier audio à jouer
     * @throws UnsupportedAudioFileException Si le format n'est pas supporté
     * @throws IOException En cas d'erreur de lecture
     * @throws LineUnavailableException Si le matériel audio n'est pas disponible
     */
    public void play(File audioFile) throws UnsupportedAudioFileException, 
                                          IOException, 
                                          LineUnavailableException {
        if (isPlaying) {
            stop();
        }
        
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        audioClip = AudioSystem.getClip();
        audioClip.open(audioStream);
        audioClip.start();
        
        isPlaying = true;
        
        // Écouter la fin de la lecture
        audioClip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                isPlaying = false;
                audioClip.close();
            }
        });
    }
    
    /**
     * Arrête la lecture
     */
    public void stop() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
            audioClip.close();
            isPlaying = false;
        }
    }
    
    /**
     * @return Vrai si une lecture est en cours
     */
    public boolean isPlaying() {
        return isPlaying;
    }
}