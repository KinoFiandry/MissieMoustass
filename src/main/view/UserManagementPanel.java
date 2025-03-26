package main.view;

import main.model.User;
import main.service.UserService;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Panel de gestion des utilisateurs (admin seulement).
 */
public class UserManagementPanel extends JPanel {
    private JTable usersTable;
    private UserTableModel tableModel;
    
    /**
     * Constructeur du panel de gestion des utilisateurs.
     */
    public UserManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
    }
    
    /**
     * Initialise les composants de l'interface.
     */
    private void initializeComponents() {
        // Modèle de table
        tableModel = new UserTableModel();
        usersTable = new JTable(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Barre d'outils
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(this::showAddUserDialog);
        
        JButton editButton = new JButton("Modifier");
        editButton.addActionListener(this::showEditUserDialog);
        
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(this::deleteSelectedUser);
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        
        // Ajout des composants
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(usersTable), BorderLayout.CENTER);
    }
    
    /**
     * Affiche le dialogue d'ajout d'utilisateur.
     * @param e Événement d'action
     */
    private void showAddUserDialog(ActionEvent e) {
        UserDialog dialog = new UserDialog((JFrame)SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isUserSaved()) {
            tableModel.refreshData();
        }
    }
    
    /**
     * Affiche le dialogue de modification d'utilisateur.
     * @param e Événement d'action
     */
    private void showEditUserDialog(ActionEvent e) {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow >= 0) {
            User user = tableModel.getUserAt(selectedRow);
            UserDialog dialog = new UserDialog((JFrame)SwingUtilities.getWindowAncestor(this), user);
            dialog.setVisible(true);
            
            if (dialog.isUserSaved()) {
                tableModel.refreshData();
            }
        }
    }
    
    /**
     * Supprime l'utilisateur sélectionné.
     * @param e Événement d'action
     */
    private void deleteSelectedUser(ActionEvent e) {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow >= 0) {
            User user = tableModel.getUserAt(selectedRow);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Supprimer l'utilisateur '" + user.getUsername() + "' ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (UserService.deleteUser(user.getId())) {
                    tableModel.refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Échec de la suppression", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Modèle de table pour les utilisateurs.
     */
    private static class UserTableModel extends AbstractTableModel {
        private List<User> users;
        private final String[] columnNames = {"ID", "Nom d'utilisateur", "Email", "Nom", "Prénom", "Admin"};
        
        public UserTableModel() {
            this.users = UserService.getAllUsers();
        }
        
        /**
         * Rafraîchit les données du modèle.
         */
        public void refreshData() {
            this.users = UserService.getAllUsers();
            fireTableDataChanged();
        }
        
        /**
         * Récupère un utilisateur à une ligne spécifique.
         * @param row Index de la ligne
         * @return L'utilisateur correspondant
         */
        public User getUserAt(int row) {
            return users.get(row);
        }
        
        @Override
        public int getRowCount() {
            return users.size();
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
            User user = users.get(rowIndex);
            switch (columnIndex) {
            case 0: return user.getId();
            case 1: return user.getUsername();
            case 2: return user.getEmail();
            case 3: return user.getNom();
            case 4: return user.getPrenom();
            case 5: return user.isAdmin() ? "Oui" : "Non";
            default: return null;
            }
        }
    }
}