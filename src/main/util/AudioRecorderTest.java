package main.util;

import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AudioRecorderTest {

    @Test
    void testStartStopRecording() throws LineUnavailableException, IOException {
        AudioRecorder recorder = new AudioRecorder();
        
        // Test d'enregistrement très court
        File audioFile = recorder.startRecording();
        assertTrue(recorder.isRecording(), "L'enregistrement doit être en cours");
        
        // Attendre brièvement pour la capture audio
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        recorder.stopRecording();
        assertFalse(recorder.isRecording(), "L'enregistrement doit être arrêté");
        
        // Vérifier que le fichier a été créé
        assertTrue(audioFile.exists(), "Le fichier audio doit exister");
        assertTrue(audioFile.length() > 0, "Le fichier audio ne doit pas être vide");
        
        // Nettoyage
        audioFile.delete();
    }

    @Test
    void testGetAudioFormat() {
        AudioRecorder recorder = new AudioRecorder();
        AudioFormat format = recorder.getAudioFormat();
        
        assertEquals(44100.0f, format.getSampleRate(), "Le sample rate doit être 44.1kHz");
        assertEquals(16, format.getSampleSizeInBits(), "La taille d'échantillon doit être 16 bits");
        assertEquals(1, format.getChannels(), "Le nombre de canaux doit être 1 (mono)");
    }
}