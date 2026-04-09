package com.compiladores.semantics.models;

import java.util.LinkedList;
import java.util.List;

public class Symbol {
    private final String name;//nombre del simbolo (lexema)
    private final Type type;//indica el tipo de dato representa el simbolo (p. ej. chakra)
    private final Category category;//indica si es variable, funcion, o parametro
    private final List<Type> parametersTypes;//lista del tipo de dato de los parametros, solo cuando category es FUNCTION
    private final int lineOfDeclaration;//numero de linea donde el simbolo es declarado
    private final List<Integer> linesOfUsage;//lista enlazada de los numeros de linea donde se invoca al simbolo

    private Symbol(Builder SymbolBuilder) {
        this.name = SymbolBuilder.name;
        this.type = SymbolBuilder.type;
        this.category = SymbolBuilder.category;
        this.parametersTypes = SymbolBuilder.parametersTypes;
        this.lineOfDeclaration = SymbolBuilder.lineOfDeclaration;
        this.linesOfUsage = new LinkedList<>();
    }

    public static class Builder {
        private final String name;
        private Type type;
        private Category category;
        private final List<Type> parametersTypes;
        private int lineOfDeclaration;

        public Builder(String name) {
            this.name = name;
            this.parametersTypes = new LinkedList<>();
        }

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setCategory(Category category) {
            this.category = category;
            return this;
        }


        public Builder setLineOfDeclaration(int lineOfDeclaration) {
            this.lineOfDeclaration = lineOfDeclaration;
            return this;
        }

        public Builder addParameter(Type type) {
            this.parametersTypes.add(type);
            return this;
        }

        public Symbol build() {
            return new Symbol(this);
        }
    }

    public void addUsage(int lineOfUsage) {
        this.linesOfUsage.add(lineOfUsage);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    public List<Type> getParametersTypes() {
        return parametersTypes;
    }

    public int getLineOfDeclaration() {
        return lineOfDeclaration;
    }

    public List<Integer> getLinesOfUsage() {
        return linesOfUsage;
    }
}
