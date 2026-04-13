package com.compiladores.ui.views;

import com.compiladores.core.CompilerService;
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
        btnRun.addActionListener(e -> runAnalysis());
        btnClear.addActionListener(e -> clean());
        btnBrowser.addActionListener(e -> openReports());
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

    private void runAnalysis() {
        String codigo = editor.getText();

        if (codigo == null || codigo.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    mainPanel,
                    "No hay código para analizar.\n\n" +
                            "Puedes:\n" +
                            "• Escribir código en el editor \n" +
                            "• O cargar un archivo desde el botón Abrir archivo",
                    "ShinobiScript Analyzer",
                    JOptionPane.WARNING_MESSAGE
            );

            editor.requestFocus();
            return;
        }


        int result = CompilerService.analyze(codigo);

        if (result == 0) {
            JOptionPane.showMessageDialog(mainPanel, "Análisis exitoso");
        } else {
            JOptionPane.showMessageDialog(mainPanel, "Se produjo un error durante el análisis.");
        }
    }

    private void clean() {
        try {
            editor.setText("");
            editor.setCaretPosition(0);
            editor.requestFocus();

            String outputDir = System.getProperty("user.dir") + "/output/";
            File file = new File(outputDir);

            int eliminated = 0;

            if (file.exists() && file.isDirectory()) {

                File[] files = file.listFiles((dir, name) -> name.endsWith(".html"));

                if (files != null) {
                    for (File itemFile : files) {
                        if (itemFile.delete()) {
                            eliminated++;
                        }
                    }
                }
            }

            if (eliminated > 0) {
                JOptionPane.showMessageDialog(
                        mainPanel,
                        "Se eliminaron " + eliminated + " reporte(s)",
                        "ShinobiScript Analyzer",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "No fue posible limpiar completamente.\n\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openReports() {

        try {
            String outputDir = System.getProperty("user.dir") + "/output/";
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

            String[] reportes = {
                    "btc_tokens.html",
                    "btc_err_lexicos.html",
                    "btc_err_sintacticos.html",
                    "btc_err_semanticos.html",
                    "btc_scopes.html",
                    "btc_calls.html"
            };

            int open = 0;

            for (String nombre : reportes) {
                File file = new File(outputDir + nombre);

                if (file.exists()) {
                    desktop.browse(file.toURI());
                    open++;
                }
            }

            if (open == 0) {
                JOptionPane.showMessageDialog(
                        mainPanel,
                        "No hay reportes disponibles.\n\n" +
                                "Ejecuta un análisis primero para generar resultados.",
                        "ShinobiScript Analyzer",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Ocurrió un error al intentar abrir los reportes.\n\n" +
                            ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
