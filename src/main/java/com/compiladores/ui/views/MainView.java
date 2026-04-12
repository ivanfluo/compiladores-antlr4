package com.compiladores.ui.views;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class MainView {
    private JPanel mainPanel;
    private JPanel editorContainer;
    private RSyntaxTextArea editor;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public MainView() {
        createUIComponents();
    }

    private void createUIComponents(){
        editor = new RSyntaxTextArea(25, 80);
        editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        editor.setCodeFoldingEnabled(true);
        editor.setFont(new Font("Consolas", Font.PLAIN, 14));

        RTextScrollPane scrollPane = new RTextScrollPane(editor);
        editorContainer = new JPanel(new BorderLayout());
        editorContainer.add(scrollPane, BorderLayout.CENTER);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(editorContainer, BorderLayout.CENTER);
    }
}
