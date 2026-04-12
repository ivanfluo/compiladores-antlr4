package com.compiladores.ui.launcher;

import com.compiladores.ui.views.MainView;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;

public class MainUI {
    public static void main(String[] args) {
        try {
            FlatMacLightLaf.setup();

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("ShinobiScript Analyzer");

                MainView mainView = new MainView();

                frame.setContentPane(mainView.getMainPanel());

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(900, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });

        } catch (Exception ex) {
            System.err.println("Error " + ex);
        }
    }
}
