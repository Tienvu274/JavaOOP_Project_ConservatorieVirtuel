package org.app.gui;

import javax.swing.*;

public class GuiMain {
    public static void main(String[] args) {
        java.util.Locale.setDefault(java.util.Locale.ENGLISH);
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { /* Use the default Swing look and feel. */ }
            new MainFrame().setVisible(true);
        });
    }
}

