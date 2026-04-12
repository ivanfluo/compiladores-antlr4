package com.compiladores.ui.launcher;

import com.compiladores.ui.views.MainView;

import javax.swing.*;

public class MainUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ShinobiScript Analyzer");

        MainView mainView = new MainView();

        frame.setContentPane(mainView.getMainPanel());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
