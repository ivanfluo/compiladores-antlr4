package com.compiladores.errors;

// [Inicio][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Implementación de la clase para los errores]

/**
 * Entrada de errores en la bitacora.
 * @author Ivan Hernandez.
 */
public class ErrorEntry {
    private final ErrorType type;
    private final int line;
    private final int column;
    private final String message;

    /**
     * Constructor base de la clase ErrorEntry.
     * @param type tipo de error detectado.
     * @param line número de línea donde ocurrió el error.
     * @param column posicion de la columna donde ocurrió el erorr.
     * @param message descripción del mensaje de error.
     */
    public ErrorEntry(ErrorType type, int line, int column, String message) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public ErrorType getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }
}

// [Fin][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Implementación de la clase para los errores]
