package main.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Classe utilitaire pour l'enregistrement audio.
 */
public class AudioRecorder {
    private static final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;
    private final AudioFormat format;
    private final File audioFile;
    private TargetDataLine dataLine;
    private boolean isRecording = false;
    
    /**
     * Constructeur de l'enregistreur audio.
     * @param audioFile Fichier de destination
     */
    public AudioRecorder(File audioFile) {
        this.audioFile = audioFile;
        this.format = getAudioFormat();
    }
    
    /**
     * Démarre l'enregistrement audio.
     * @throws LineUnavailableException Si le matériel audio n'est pas disponible
     */
    public void start() throws LineUnavailableException {
        if (isRecording) return;
        
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Line not supported");
        }
        
        dataLine = (TargetDataLine) AudioSystem.getLine(info);
        dataLine.open(format);
        dataLine.start();
        
        isRecording = true;
        
        new Thread(() -> {
            try (AudioInputStream ais = new AudioInputStream(dataLine)) {
                AudioSystem.write(ais, FILE_TYPE, audioFile);
            } catch (IOException e) {
                System.err.println("Error during recording: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Arrête l'enregistrement audio.
     */
    public void stop() {
        if (!isRecording) return;
        
        isRecording = false;
        dataLine.stop();
        dataLine.close();
    }
    
    /**
     * @return Vrai si un enregistrement est en cours
     */
    public boolean isRecording() {
        return isRecording;
    }
    
    /**
     * Configure le format audio par défaut.
     * @return Le format audio configuré
     */
    private AudioFormat getAudioFormat() {
        float sampleRate = 44100; // 44.1 kHz
        int sampleSizeInBits = 16;
        int channels = 1; // Mono
        boolean signed = true;
        boolean bigEndian = false;
        
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}
