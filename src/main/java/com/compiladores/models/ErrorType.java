package com.compiladores.models;

/**
 * Define las categorías de errores dentro del compilador
 * @author Daniel Aldana / DaS6T
 * @version 1.0
 */
public enum ErrorType {
    /**
     * Errores detectados durante la generacion de tokens.
     */
    LEXICO,
    /**
     * Errores de gramática y estructuras detectadas por el Parser.
     */
    SINTACTICO,
    /**
     * Errores de lógica, como variables no declaradas o tipos incompatibles.
     */
    SEMANTICO
}
