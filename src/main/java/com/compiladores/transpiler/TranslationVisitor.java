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

        if(ctx.declaracionFuncion() != null) {
            for(ShinobiScriptParser.DeclaracionFuncionContext funcion : ctx.declaracionFuncion()) {
                code.append(
                        visit(funcion)
                ).append("\n");
            }
        }

        if(ctx.main() != null) {
            code.append(
                    visit(ctx.main())
            );
        }

        return code.toString();
    }

    @Override
    public String visitDeclaracionFuncion(ShinobiScriptParser.DeclaracionFuncionContext ctx) {
        StringBuilder functionCode = new StringBuilder();
        String returnType;
        if(ctx.MU() != null) {
            returnType = "void";
        }else {
            returnType = convertToCppType(ctx.tipo().getText());
        }

        String id = ctx.ID().getText();

        functionCode.append(returnType)
                .append(" ")
                .append(id)
                .append("(");

        ShinobiScriptParser.ParametrosContext paramCtx = ctx.parametros();

        if(paramCtx != null) {
            for(int i = 0; i < paramCtx.tipo().size(); i++) {
                String pType = convertToCppType(paramCtx.tipo(i).getText());
                String pId = paramCtx.ID(i).getText();

                functionCode.append(pType)
                        .append(" ")
                        .append(pId);
                if(i < paramCtx.tipo().size() - 1) {
                    functionCode.append(", ");
                }
            }
        }

        functionCode.append(") ");

        functionCode.append(
                visit(ctx.bloque())
        );

        return functionCode.toString();
    }

    @Override
    public String visitMain(ShinobiScriptParser.MainContext ctx) {
        StringBuilder mainCode = new StringBuilder();
        mainCode.append("int main() {\n");

        mainCode.append(visit(ctx.bloque()));

        mainCode.append("\n     return 0;\n");
        mainCode.append("}\n");
        return mainCode.toString();
    }




    private String convertToCppType(String type) {
        return switch (type) {
            case "CHAKRA" -> "int";
            case "RYO" -> "double";
            case "SHINRI" -> "boolean";
            case "KANA" -> "char";
            case "MOJI" -> "string";
            case "MU" -> "void";
            default -> "/* error_type */ void";
        };
    }
}
