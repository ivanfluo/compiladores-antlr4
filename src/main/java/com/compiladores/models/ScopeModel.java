package com.compiladores.models;

import com.compiladores.io.IReportable;
import com.compiladores.semantics.models.Symbol;

import java.util.Arrays;
import java.util.List;

public class ScopeModel implements IReportable {
    private String scope;
    private String symbolName;
    private String type;
    private String externType;
    private String category;
    private int line;

    public ScopeModel(String scope, Symbol symbol) {
        this.scope = scope;
        this.symbolName = symbol.getName();
        this.type = symbol.getType().toString();
        this.externType = toExternalType(type);
        this.category = symbol.getCategory().toString();
        this.line = symbol.getLineOfDeclaration();
    }

    @Override
    public List<String> getHeaders() { return Arrays.asList("Contexto", "Simbolo", "Tipo", "Tipo externo", "Categoría", "linea de declaración"); }

    @Override
    public List<String> toRow() {
        return Arrays.asList(
                this.scope,
                this.symbolName,
                this.type,
                this.externType,
                this.category,
                Integer.toString(this.line)
        );
    }

    public String getScope() { return scope; }

    private String toExternalType(String value) {
        return switch(value) {
            case "CHAKRA" -> "INTEGER";
            case "RYO" -> "DOUBLE";
            case "KANA" -> "CHAR";
            case "SHINRI" -> "BOOLEAN";
            case "MOJI" -> "STRING";
            case "MU" -> "VOID";
            default -> "N/A";
        };
    }
}
