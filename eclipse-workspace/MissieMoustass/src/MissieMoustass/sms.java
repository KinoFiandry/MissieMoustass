package MissieMoustass;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import javax.sound.sampled.*;

public class MessagesAudio extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnRecord, btnPlay;
    private String selectedFilePath;

    public MessagesAudio() {
        setTitle("Gestion des Messages Audio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "Utilisateur", "Nom du fichier"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        btnRecord = new JButton("Enregistrer");
        btnPlay = new JButton("Lire");

        panel.add(btnRecord);
        panel.add(btnPlay);
        add(panel, BorderLayout.SOUTH);

        btnRecord.addActionListener(e -> enregistrerAudio());
        btnPlay.addActionListener(e -> lireAudio());

        chargerMessages();
    }

    private void chargerMessages() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, utilisateur_id, fichier_audio FROM Messages")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("id"), rs.getInt("utilisateur_id"), rs.getString("fichier_audio")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void enregistrerAudio() {
        try {
            File file = new File("message.wav");
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            Thread thread = new Thread(() -> {
                try (AudioInputStream ais = new AudioInputStream(microphone)) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
                    JOptionPane.showMessageDialog(null, "Enregistrement terminé");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lireAudio() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Veuillez sélectionner un message");
            return;
        }
        selectedFilePath = table.getValueAt(selectedRow, 2).toString();
        try {
            File file = new File(selectedFilePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MessagesAudio().setVisible(true));
    }
}
