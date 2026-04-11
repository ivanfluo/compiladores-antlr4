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
    private String tipoExterno;
    private int linea;
    private int columna;

    public TokenModel(Token t, Vocabulary v) {
        String symbolicName = v.getSymbolicName(t.getType());
        this.lexema = t.getText();
        this.tipo = (symbolicName != null) ? symbolicName : "LITERAL/OTRO";
        this.tipoExterno = toExternalType((symbolicName != null) ? symbolicName : "N/A");
        this.linea = t.getLine();
        this.columna = t.getCharPositionInLine();
    }

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("Lexema", "Tipo interno", "Tipo externo", "Linea", "Columna");
    }

    @Override
    public List<String> toRow() {
        return Arrays.asList(lexema, tipo, tipoExterno, String.valueOf(linea), String.valueOf(columna));
    }

    private String toExternalType(String value) {
        return switch(value) {
            case "CHAKRA" -> "INTEGER";
            case "RYO" -> "DOUBLE";
            case "KANA" -> "CHAR";
            case "SHINRI" -> "BOOLEAN";
            case "MOJI" -> "STRING";
            case "MU" -> "VOID";

            case "JUTSU", "MOSHI", "SORE", "KUCHIYOSE", "KAKEMONO", "KURIKAE", "KAI"
                -> "KEYWORD";
            case "LPAREN", "RPAREN", "LBRACE", "RBRACE", "SEMI", "COMMA"
                -> "DELIMETER";
            case "ASSIGN", "GT", "LT", "LEQ", "GEQ", "EQ", "NEQ", "PLUS", "MINUS", "MULT", "DIV"
                -> "OPERATOR";
            case "ID" -> "IDENTIFIER";
            case "INT", "DOUBLE", "STRING", "CHAR", "MARU", "BATSU"
                -> "LITERAL";
            default -> "N/A";
        };
    }
}
