package com.compiladores.semantics;

import java.util.HashMap;
import java.util.Map;

/**
 * Representacion de tabla de simbolos, encargada de manejar el contexto (ctx) de los identificadores.
 * <p>
 *     Clase encargada de gestionar los simbolos (identificadores) de un contexto de forma tabular
 *     almacenando toda la inforamción en objetos de tipo {@link Symbol} en un {@link HashMap} con llave
 *     en el nombre del simbolo, y valor con el objeto.
 * </p>
 */
public class SymbolTable {
    private final Map<String, Symbol> symbols = new HashMap<>();
    private final SymbolTable parent;
    private final String scopeName;

    public SymbolTable(SymbolTable parent, String scopeName) {
        this.scopeName = scopeName;
        this.parent = parent;
    }

    /**
     * Busca un simbolo dentro del contexto que lo invoca. En caso de
     * no encontrarse, siempre que el padre no sea nulo, se llama recursivamente
     * a la función para localizar el simbolo en los contextos padres al actual.
     * @param name nombre del simbolo que busca.
     * @return devuelve el {@code Symbol} encontrado o {@code null} si no existe.
     */
    public Symbol lookup(String name) {
        Symbol s = symbols.get(name);
        if(s != null) return s;
        if(parent != null) return parent.lookup(name);
        return null;
    }

    /**
     * Intenta insertar un simbolo nuevo dentro del contexto que lo invoca.
     * @param symbol el simbolo nuevo a insertar en el contexto.
     * @return {@code true} si el simbolo no existe en el contexto actual y se puede insertar,
     * y {@code false} si existe y por ende no se puede insertar
     */
    public boolean insert(Symbol symbol) {
        if(symbols.containsKey(symbol.getName())) return false;
        symbols.put(symbol.getName(), symbol);
        return true;
    }

    /**
     * Obtiene el nombre del scope en el que se llama.
     * @return {@code String} el nombre del scope.
     */
    public String getScopeName() {
        return scopeName;
    }

    /**
     * Obtiene la referencia al contexto padre del scope en el que se llama.
     * @return Referencia al {@code parent} si el contexto padre no es {@code null}.
     */
    public SymbolTable getParent() {
        return parent;
    }

    /**
     * Obtiene la tabla de simbolos del contexto en el que se llama.
     * @return {@link HashMap} de objetos {@link Symbol}
     */
    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

}
