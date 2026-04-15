package com.compiladores.transpiler;

import com.compiladores.ShinobiScriptBaseVisitor;
import com.compiladores.ShinobiScriptParser;
import com.compiladores.semantics.models.Type;

public class TranslationVisitor extends ShinobiScriptBaseVisitor<String> {

    @Override
    public String visitInit(ShinobiScriptParser.InitContext ctx) {
        StringBuilder code = new StringBuilder();
        code.append("#include <iostream>\n");
        code.append("#include <string>\n\n");
        code.append("using namespace std;\n\n");

        for(var child : ctx.children) {
            code.append(visit(child));
        }

        return code.toString();
    }


    private String convertToCppType(Type type) {
        return switch (type) {
            case CHAKRA -> "int";
            case RYO -> "double";
            case SHINRI -> "boolean";
            case KANA -> "char";
            case MOJI -> "string";
            case MU -> "void";
            default -> "/* error_type */ void";
        };
    }
}
