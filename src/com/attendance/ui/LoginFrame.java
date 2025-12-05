package com.attendance.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Login screen for the application.
 * Demonstrates: Swing basics, event handling with lambdas.
 */
public class LoginFrame extends JFrame {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    public LoginFrame() {
        setTitle("Login - Attendance System");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Attendance System", SwingConstants.CENTER);
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);

        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);

        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(Theme.PRIMARY_COLOR);
        loginBtn.setForeground(Color.WHITE);
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        // Lambda for action listener
        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (USERNAME.equals(user) && PASSWORD.equals(pass)) {
                dispose();
                new MainFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panel);
    }
}
