package com.compiladores.semantics;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class GlobalTypeDictionary {
    private ParseTreeProperty<String> globalTypes;
    public static GlobalTypeDictionary instance;

    private GlobalTypeDictionary() {
        globalTypes = new ParseTreeProperty<>();
    }

    public static GlobalTypeDictionary getInstance() {
        if (instance == null) {
            instance = new GlobalTypeDictionary();
        }
        return instance;
    }

    public void addTypeToDictionary(ParseTree node, String type) {
        globalTypes.put(node, type);
    }

    public String getTypeFromDictionary(ParseTree node) {
        return globalTypes.get(node);
    }

    public void resetGlobalTypes() {
        globalTypes = new ParseTreeProperty<>();
    }

}
