package com.attendance;

import com.attendance.ui.LoginFrame;

import javax.swing.*;

/**
 * Application entry point.
 */
public class App {
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Launch on EDT
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
