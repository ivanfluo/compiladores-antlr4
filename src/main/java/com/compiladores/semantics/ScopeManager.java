package com.compiladores.semantics;

import java.util.ArrayList;
import java.util.List;

public class ScopeManager {
    private SymbolTable currentScope;
    private final List<SymbolTable> scopes;
    private static ScopeManager instance;

    private ScopeManager() {
        this.currentScope = new SymbolTable(null, "Global");
        this.scopes = new ArrayList<>();
        this.scopes.add(this.currentScope);
    }

    public static ScopeManager getInstance() {
        if (instance == null) {
            instance = new ScopeManager();
        }
        return instance;
    }

    public void push(String name) {
        SymbolTable newScope = new SymbolTable(currentScope, name);
        scopes.add(newScope);
        currentScope = newScope;
    }

    public void pop() {
        if(currentScope.getParent() != null) {
            currentScope = currentScope.getParent();
        }
    }

    public SymbolTable getCurrentScope() {
        return currentScope;
    }

    public List<SymbolTable> getScopes() {
        return scopes;
    }

    public void reset() {
        this.currentScope = new SymbolTable(null, "Global");
        this.scopes.clear();
        this.scopes.add(this.currentScope);
    }
}
