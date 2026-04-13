package com.compiladores.semantics.models;

public enum Type {
    CHAKRA,     //INT
    RYO,        //DOUBLE
    KANA,       //CHAR
    SHINRI,     //BOOLEAN
    MOJI,       //STRING
    MU,         //VOID
    ERROR;      //ERROR SEMANTICO

    public static Type toType(String type) {
        String value = type.toUpperCase().trim();
        return switch (value) {
            case "CHAKRA" -> CHAKRA;
            case "RYO" -> RYO;
            case "KANA" -> KANA;
            case "SHINRI" -> SHINRI;
            case "MOJI" -> MOJI;
            case "MU" -> MU;
            default -> ERROR;
        };
    }
}
