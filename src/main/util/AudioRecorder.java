package main.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe pour l'enregistrement audio au format WAV
 */
public class AudioRecorder {
    private static final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;
    private final AudioFormat format;
    private TargetDataLine microphone;
    private boolean isRecording = false;
    private File audioFile;
    
    /**
     * Initialise l'enregistreur avec le format audio par défaut
     */
    public AudioRecorder() {
        this.format = getAudioFormat();
    }
    
    /**
     * Démarre l'enregistrement audio
     * @return Le fichier audio créé
     * @throws LineUnavailableException Si le matériel audio n'est pas disponible
     */
    public File startRecording() throws LineUnavailableException {
        if (isRecording) {
            throw new IllegalStateException("L'enregistrement est déjà en cours");
        }
        
        // Créer un nom de fichier unique avec timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "recording_" + timestamp + ".wav";
        this.audioFile = new File("resources/audio/" + filename);
        
        // Créer le répertoire s'il n'existe pas
        audioFile.getParentFile().mkdirs();
        
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();
        
        isRecording = true;
        
        // Démarrer un nouveau thread pour l'enregistrement
        new Thread(() -> {
            try (AudioInputStream ais = new AudioInputStream(microphone)) {
                AudioSystem.write(ais, FILE_TYPE, audioFile);
            } catch (IOException e) {
                System.err.println("Erreur lors de l'enregistrement: " + e.getMessage());
            }
        }).start();
        
        return audioFile;
    }
    
    /**
     * Arrête l'enregistrement audio
     */
    public void stopRecording() {
        if (!isRecording) return;
        
        isRecording = false;
        microphone.stop();
        microphone.close();
    }
    
    /**
     * @return Vrai si un enregistrement est en cours
     */
    public boolean isRecording() {
        return isRecording;
    }
    
    /**
     * @return Le fichier audio en cours d'enregistrement
     */
    public File getAudioFile() {
        return audioFile;
    }
    
    /**
     * Configure le format audio
     * @return Format audio configuré
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 44100; // 44.1 kHz
        int sampleSizeInBits = 16;
        int channels = 1; // Mono
        boolean signed = true;
        boolean bigEndian = false;
        
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}