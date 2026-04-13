package com.compiladores.ui.views;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class MainView {
    private JPanel mainPanel;
    private JPanel editorContainer;
    private JButton btnOpen;
    private JButton btnRun;
    private JButton btnClear;
    private JToolBar toolBar;
    private JButton btnBrowser;
    private RSyntaxTextArea editor;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public MainView() {
        createUIComponents();
        initToolbar();
        styleToolbarButtons();

        btnOpen.addActionListener(e -> openFile());
        btnClear.addActionListener(e -> clean());
    }

    private void createUIComponents() {
        editor = new RSyntaxTextArea(25, 80);
        editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        editor.setCodeFoldingEnabled(true);
        editor.setFont(new Font("Consolas", Font.PLAIN, 14));

        RTextScrollPane scrollPane = new RTextScrollPane(editor);

        editorContainer = new JPanel(new BorderLayout());
        editorContainer.add(scrollPane, BorderLayout.CENTER);

        mainPanel = new JPanel(new BorderLayout());

        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(editorContainer, BorderLayout.CENTER);
    }

    private void initToolbar() {
        btnOpen.setIcon(new FlatSVGIcon("icons/folder-open.svg", 16, 16));
        btnOpen.setToolTipText("Abrir archivo");

        btnRun.setIcon(new FlatSVGIcon("icons/play.svg", 16, 16));
        btnRun.setToolTipText("Analizar");

        btnClear.setIcon(new FlatSVGIcon("icons/trash-2.svg", 16, 16));
        btnClear.setToolTipText("Limpiar");

        btnBrowser.setIcon(new FlatSVGIcon("icons/earth.svg", 16, 16));
        btnBrowser.setToolTipText("Abrir resultados en el navegador");

    }

    private void styleToolbarButtons() {
        for (JButton btn : new JButton[]{btnOpen, btnRun, btnClear, btnBrowser}) {
            btn.setText(null);
            btn.setMargin(new Insets(4, 4, 4, 4));
        }
    }

    private void openFile() {
        try {
            JFileChooser chooser = new JFileChooser();

            int result = chooser.showOpenDialog(mainPanel);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String content = Files.readString(file.toPath());

                editor.setText(content);
                editor.setCaretPosition(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error al abrrir el archivo");
        }
    }

    private void clean() {
        try {
            editor.setText("");
            editor.setCaretPosition(0);
            editor.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error no fue posible limpiar el editor");
        }
    }
}
