package main;

import view.LoginFrame;
import javax.swing.*;

/**
 * Main - Punto de entrada de la aplicación.
 * Solo arranca el LoginFrame, nada más.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}