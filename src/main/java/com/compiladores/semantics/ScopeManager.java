package com.compiladores.semantics;

import com.compiladores.models.ScopeModel;
import com.compiladores.semantics.models.Symbol;

import java.util.*;
import java.util.stream.Collectors;

public class ScopeManager {
    private SymbolTable currentScope;
    private final List<SymbolTable> scopes;
    private final Map<SymbolTable, Integer> childIndexTracker;
    private static ScopeManager instance;

    private final List<ScopeModel> scopeHistory = new ArrayList<>();

    private ScopeManager() {
        this.currentScope = new SymbolTable(null, "GLOBAL");
        this.scopes = new ArrayList<>();
        this.scopes.add(this.currentScope);
        this.childIndexTracker = new HashMap<>();
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
        currentScope.addChild(newScope);
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

    public Map<String,List<ScopeModel>> getScopes() {
        if(currentScope != null && currentScope.getParent() == null) {
            addScopeToHistory(currentScope);
        }
        return scopeHistory.stream()
                .collect(Collectors.groupingBy(
                        ScopeModel::getScope,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public void resetCurrentScopePointer() {
        while(currentScope.getParent() != null) {
            currentScope = currentScope.getParent();
        }
        childIndexTracker.clear();
    }

    public void visitNextScopeChild() {
        int nextChildIndex = childIndexTracker.getOrDefault(currentScope, 0);
        if(nextChildIndex < currentScope.getChildren().size()) {
            SymbolTable child = currentScope.getChildren().get(nextChildIndex);
            childIndexTracker.put(currentScope, nextChildIndex + 1);
            currentScope = child;
        }
    }

    public void exitScopeChild() {
        if(currentScope.getParent() != null) {
            currentScope = currentScope.getParent();
        }
    }

    public Symbol resolveSymbol(String name) {
        SymbolTable scope = currentScope;
        while(scope != null) {
            if(scope.getSymbols().containsKey(name)) {
                return scope.getSymbols().get(name);
            }
            scope = scope.getParent();
        }
        return null;
    }

    public void resetScopeManager() {
        this.currentScope = new SymbolTable(null, "GLOBAL");
        this.childIndexTracker.clear();
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
