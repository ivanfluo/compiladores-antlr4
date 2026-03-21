package com.compiladores.models;

import com.compiladores.io.IReportable;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;

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

    public TokenModel(Token t, Vocabulary v) {
        String symbolicName = v.getSymbolicName(t.getType());
        this.lexema = (symbolicName != null) ? symbolicName : "LITERAL/OTRO";
        this.lexema = t.getText();
        this.linea = t.getLine();
        this.columna = t.getCharPositionInLine();
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
