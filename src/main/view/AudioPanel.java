package main.view;

import main.model.VoiceMessage;
import main.service.AudioService;
import main.service.AuthService;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Panel de gestion des enregistrements audio.
 */
public class AudioPanel extends JPanel {
    private JTable messagesTable;
    private VoiceMessageTableModel tableModel;
    private JButton recordButton;
    private JButton playButton;
    private JButton deleteButton;
    
    /**
     * Constructeur du panel audio.
     */
    public AudioPanel() {
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
        refreshMessagesList();
    }
    
    /**
     * Initialise les composants de l'interface.
     */
    private void initializeComponents() {
        // Modèle de table
        tableModel = new VoiceMessageTableModel();
        messagesTable = new JTable(tableModel);
        messagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Barre d'outils
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        recordButton = new JButton("Nouvel enregistrement");
        recordButton.addActionListener(this::showRecordDialog);
        
        playButton = new JButton("Jouer");
        playButton.setEnabled(false);
        playButton.addActionListener(this::playSelectedMessage);
        
        deleteButton = new JButton("Supprimer");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(this::deleteSelectedMessage);
        
        toolBar.add(recordButton);
        toolBar.add(playButton);
        toolBar.add(deleteButton);
        
        // Gestion de la sélection
        messagesTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = messagesTable.getSelectedRow() != -1;
            playButton.setEnabled(rowSelected);
            deleteButton.setEnabled(rowSelected);
        });
        
        // Ajout des composants
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(messagesTable), BorderLayout.CENTER);
    }
    
    /**
     * Affiche le dialogue d'enregistrement.
     * @param e Événement d'action
     */
    private void showRecordDialog(ActionEvent e) {
        RecordDialog dialog = new RecordDialog((JFrame)SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        
        if (dialog.isRecordingSaved()) {
            refreshMessagesList();
        }
    }
    
    /**
     * Joue le message sélectionné.
     * @param e Événement d'action
     */
    private void playSelectedMessage(ActionEvent e) {
        int selectedRow = messagesTable.getSelectedRow();
        if (selectedRow >= 0) {
            VoiceMessage message = tableModel.getMessageAt(selectedRow);
            // Implémenter la lecture audio ici
            JOptionPane.showMessageDialog(this, 
                "Lecture de: " + message.getFilename(), 
                "Lecture", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Supprime le message sélectionné.
     * @param e Événement d'action
     */
    private void deleteSelectedMessage(ActionEvent e) {
        int selectedRow = messagesTable.getSelectedRow();
        if (selectedRow >= 0) {
            VoiceMessage message = tableModel.getMessageAt(selectedRow);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Supprimer le message '" + message.getFilename() + "' ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (AudioService.deleteMessage(message.getId())) {
                    refreshMessagesList();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Échec de la suppression", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Rafraîchit la liste des messages.
     */
    public void refreshMessagesList() {
        tableModel.refreshData();
    }
    
    /**
     * Modèle de table pour les messages vocaux.
     */
    private static class VoiceMessageTableModel extends AbstractTableModel {
        private List<VoiceMessage> messages;
        private final String[] columnNames = {"Nom", "Date", "Statut"};
        
        public VoiceMessageTableModel() {
            this.messages = AudioService.getUserMessages();
        }
        
        /**
         * Rafraîchit les données du modèle.
         */
        public void refreshData() {
            this.messages = AudioService.getUserMessages();
            fireTableDataChanged();
        }
        
        /**
         * Récupère un message à une ligne spécifique.
         * @param row Index de la ligne
         * @return Le message correspondant
         */
        public VoiceMessage getMessageAt(int row) {
            return messages.get(row);
        }
        
        @Override
        public int getRowCount() {
            return messages.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            VoiceMessage message = messages.get(rowIndex);
            switch (columnIndex) {
                case 0: return message.getFilename();
                case 1: return message.getCreatedAt().toString();
                case 2: return message.isEncrypted() ? "Chiffré" : "Non chiffré";
                default: return null;
            }
        }
    }
}