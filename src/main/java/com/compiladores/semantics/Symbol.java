package com.compiladores.semantics;

import java.util.List;
import java.util.LinkedList;
import java.util.Objects;

public class Symbol {
    private String name;                    //nombre del simbolo (lexema)
    private Type type;                      //indica el tipo de dato representa el simbolo (p. ej. chakra)
    private Category category;              //indica si es variable, funcion, o parametro
    private List<Type> parametersTypes;     //lista del tipo de dato de los parametros, solo cuando category es FUNCTION
    private int lineOfDeclaration;          //numero de linea donde el simbolo es declarado
    private List<Integer> lineOfUsage;      //lista enlazada de los numeros de linea donde se invoca al simbolo

    public Symbol(String name, Type type, Category category, int lineOfDeclaration) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.lineOfDeclaration = lineOfDeclaration;
        this.lineOfUsage = new LinkedList<>();
        this.parametersTypes = new LinkedList<>();
    }

    public void addUsage(int line) {
        this.lineOfUsage.add(line);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getLineOfDeclaration() {
        return lineOfDeclaration;
    }

    public void setLineOfDeclaration(int lineOfDeclaration) {
        this.lineOfDeclaration = lineOfDeclaration;
    }
}
