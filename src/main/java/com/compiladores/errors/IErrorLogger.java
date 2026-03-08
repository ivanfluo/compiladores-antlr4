package com.compiladores.errors;
// [Inicio][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Interfaz que permite desacoplar la lógica para el registro de errores.]

/**
 * Define el contrato para el registro de errores.
 * @author Ivan Hernandez
 */
public interface IErrorLogger {

    /**
     * Registra el error léxico.
     * @param line línea donde ocurrió el error
     * @param column columna donde ocurrió el error
     * @param message descripción del error
     */
    void logLexicalError(int line, int column, String message);

    /**
     * Registra el error sintactico.
     * @param line línea donde ocurrió el error
     * @param column columna donde ocurrió el error
     * @param message descripción del error
     */
    void logSyntaxError(int line, int column, String message);

    /**
     * Genera el reporte de errores.
     * @param path ruta donde se generará el archivo de reporte
     */
    void generateReport(String path);
}
// [Fin][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Interfaz que permite desacoplar la lógica para el registro de errores.]
