package com.compiladores.errors;

// [Inicio][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Implementación de la clase que genera la bitacora de errores en html.]

import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que genera la bitacora en formato html.
 *
 * @author Ivan Hernandez
 */
public class HtmlErrorLogger implements IErrorLogger {
    private final List<ErrorEntry> errors = new ArrayList<>();

    @Override
    public void logLexicalError(int line, int column, String message) {
        errors.add(new ErrorEntry(ErrorType.LEXICAL, line, column, message));
    }

    @Override
    public void logSyntaxError(int line, int column, String message) {
        errors.add(new ErrorEntry(ErrorType.LEXICAL, line, column, message));
    }

    @Override
    public void generateReport(String path) {
        StringBuilder html = new StringBuilder();

        try(FileWriter writer = new FileWriter(path)) {

            html.append("<html><head><title>Bitácora de Errores</title></head><body>");
            html.append("<h1>Bitácora de Errores</h1>");
            html.append("<table border='1'>");
            html.append("<tr><th>Tipo</th><th>Línea</th><th>Columna</th><th>Mensaje</th></tr>");

            for (ErrorEntry error: errors){
                html.append("<tr>")
                        .append("<td>").append(error.getType()).append("</td>")
                        .append("<td>").append(error.getLine()).append("</td>")
                        .append("<td>").append(error.getColumn()).append("</td>")
                        .append("<td>").append(error.getMessage()).append("</td>")
                        .append("</tr>");
            }

            html.append("</table></body></html>");

            writer.write(html.toString());
        }catch (IOException exception){
            throw new RuntimeException("Error al generar el reporte html ", exception);
        }
    }
}
// [Fin][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Implementación de la clase que genera la bitacora de errores en html.]

