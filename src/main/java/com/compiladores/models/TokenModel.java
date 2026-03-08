package com.compiladores.models;

import com.compiladores.io.IReportable;

import java.util.Arrays;
import java.util.List;

/**
 * Clase envoltura para tokens.
 * <p>
 *     Envuelve los tokens originales encontrados por el analizador lexico
 *     generado por ANTLR4 y los devuelve en forma de objetos con los cuales
 *     {@link com.compiladores.io.HTMLReportGenerator} pueda generar una tabla
 *     al implementar {@link  IReportable}.
 * </p>
 */
public class TokenModel implements IReportable {
    private String lexema;
    private String tipo;
    private int linea;
    private int columna;

    public TokenModel(String lexema, String tipo, int linea, int columna) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("Lexema", "Tipo", "Linea", "Columna");
    }

    @Override
    public List<String> toRow() {
        return Arrays.asList(lexema, tipo, String.valueOf(linea), String.valueOf(columna));
    }
}
