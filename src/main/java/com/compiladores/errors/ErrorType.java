package com.compiladores.errors;

// [Inicio][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Implementación de la clase para los tipos de errores]

/**
 * Enumeración para los tipos de errores que pueden ocurrir durante el proceso de análisis.
 * @author Ivan Hernandez
 */
public enum ErrorType {
    /**
     * Error léxico detectado por el lexer.
     */
    LEXICAL,

    /**
     * Error sintáctico detectado por el parser.
     */
    SYNTAX
}

// [Fin][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Implementación de la clase para los tipos de errores]