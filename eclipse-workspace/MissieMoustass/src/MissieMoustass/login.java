package MissieMoustass;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.regex.*;

public class login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    login frame = new login();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public login() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("EMAIL :");
        lblNewLabel.setBounds(22, 93, 96, 37);
        contentPane.add(lblNewLabel);

        JLabel lblPassword = new JLabel("PASSWORD :");
        lblPassword.setBounds(22, 141, 96, 37);
        contentPane.add(lblPassword);

        textField = new JTextField();
        textField.setBounds(148, 93, 184, 28);
        contentPane.add(textField);
        textField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(148, 145, 184, 28);
        contentPane.add(passwordField);

        JButton btnNewButton = new JButton("CONNECTION");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = textField.getText();
                String password = new String(passwordField.getPassword());

                if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(null, "Email invalide. Il doit contenir @gmail.com");
                    return;
                }
                if (!isValidPassword(password)) {
                    JOptionPane.showMessageDialog(null, "Le mot de passe doit contenir au moins 12 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.");
                    return;
                }
                if (UserDAO.login(email, password)) {
                    JOptionPane.showMessageDialog(null, "Connexion réussie !");
                } else {
                    JOptionPane.showMessageDialog(null, "Identifiants incorrects.");
                }
            }
        });
        btnNewButton.setBounds(148, 211, 114, 41);
        contentPane.add(btnNewButton);

        JLabel lblNewLabel_1 = new JLabel("MissieMoustass");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblNewLabel_1.setBounds(116, 29, 212, 37);
        contentPane.add(lblNewLabel_1);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$");
    }
}
