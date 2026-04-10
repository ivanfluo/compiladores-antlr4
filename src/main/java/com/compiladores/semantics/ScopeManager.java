package com.compiladores.semantics;

import com.compiladores.models.ScopeModel;
import com.compiladores.semantics.models.Symbol;

import java.util.ArrayList;
import java.util.List;

public class ScopeManager {
    private SymbolTable currentScope;
    private final List<SymbolTable> scopes;
    private static ScopeManager instance;

    private final List<ScopeModel> scopeHistory = new ArrayList<>();

    private ScopeManager() {
        this.currentScope = new SymbolTable(null, "GLOBAL");
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
            addScopeToHistory(currentScope);
            currentScope = currentScope.getParent();
        }
    }

    public SymbolTable getCurrentScope() {
        return currentScope;
    }

    public List<ScopeModel> getScopes() {
        if(currentScope != null && currentScope.getParent() == null) {
            addScopeToHistory(currentScope);
        }
        return scopeHistory;
    }

    public void reset() {
        this.currentScope = new SymbolTable(null, "GLOBAL");
        this.scopes.clear();
        this.scopes.add(this.currentScope);
        this.scopeHistory.clear();
    }

    private void addScopeToHistory(SymbolTable currentScope) {
        for(Symbol s : currentScope.getSymbols().values()) {
            this.scopeHistory.add(new ScopeModel(currentScope.getScopeName(),s));
        }
    }
}
