package com.compiladores.transpiler;

import com.compiladores.ShinobiScriptBaseVisitor;
import com.compiladores.ShinobiScriptParser;

public class TranslationVisitor extends ShinobiScriptBaseVisitor<String> {

    private String sourceCode = "";

    public String getSourceCode() {
        return this.sourceCode;
    }

    @Override
    public String visitLInt(ShinobiScriptParser.LIntContext ctx) {
        return ctx.INT().getText();
    }

    @Override
    public String visitLDouble(ShinobiScriptParser.LDoubleContext ctx) {
        return ctx.DOUBLE().getText();
    }

    @Override
    public String visitLChar(ShinobiScriptParser.LCharContext ctx) {
        return ctx.CHAR().getText();
    }

    @Override
    public String visitLString(ShinobiScriptParser.LStringContext ctx) {
        return ctx.STRING().getText();
    }

    @Override
    public String visitLTrue(ShinobiScriptParser.LTrueContext ctx) {
        return "true";
    }

    @Override
    public String visitLFalse(ShinobiScriptParser.LFalseContext ctx) {
        return "false";
    }

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

        this.sourceCode = code.toString();
        return "";
    }

    @Override
    public String visitSAsignacion(ShinobiScriptParser.SAsignacionContext ctx) {
        return visit(ctx.asignacion()) + ";\n";
    }

    @Override
    public String visitSImpresion(ShinobiScriptParser.SImpresionContext ctx) {
        return visit(ctx.impresion()) + ";\n";
    }

    @Override
    public String visitSLectura(ShinobiScriptParser.SLecturaContext ctx) {
        return visit(ctx.lectura()) + ";\n";
    }

    @Override
    public String visitSDeclaracion(ShinobiScriptParser.SDeclaracionContext ctx) {
        return visit(ctx.declaracion())  + ";\n";
    }

    @Override
    public String visitSRetorno(ShinobiScriptParser.SRetornoContext ctx) {
        return visit(ctx.retorno())  + ";\n";
    }

    @Override
    public String visitSControl(ShinobiScriptParser.SControlContext ctx) {
        return visit(ctx.sentenciaControl()) + "\n";
    }

    @Override
    public String visitSIterativa(ShinobiScriptParser.SIterativaContext ctx) {
        return visit(ctx.sentenciaIterativa()) + "\n";
    }

    @Override
    public String visitSLlamada(ShinobiScriptParser.SLlamadaContext ctx) {
        return visit(ctx.llamadaFuncion()) + ";\n";
    }

    @Override
    public String visitSBloque(ShinobiScriptParser.SBloqueContext ctx) {
        return visit(ctx.bloque()) + "\n";
    }

    @Override
    public String visitImpresion(ShinobiScriptParser.ImpresionContext ctx) {
        return "cout <<" + visit(ctx.expresion()) + " << endl";
    }

    @Override
    public String visitLectura(ShinobiScriptParser.LecturaContext ctx) {
        return "cin >>" + ctx.ID().getText();
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
    public String visitRetorno(ShinobiScriptParser.RetornoContext ctx) {
        return (ctx.expresion() != null) ? "return " + visit(ctx.expresion()) : "return";
    }

    @Override
    public String visitMain(ShinobiScriptParser.MainContext ctx) {
        StringBuilder mainCode = new StringBuilder();
        mainCode.append("int main() {\n");

        if(ctx.bloque() != null) {
            for(ShinobiScriptParser.SentenciaContext stnCtx : ctx.bloque().sentencia()) {
                mainCode.append("    ").append(visit(stnCtx));
            }
        }

        mainCode.append("    return 0;\n");
        mainCode.append("}\n");
        return mainCode.toString();
    }

    @Override
    public String visitBloque(ShinobiScriptParser.BloqueContext ctx) {
        StringBuilder bloqueCode = new StringBuilder();
        bloqueCode.append("{\n");
        for(ShinobiScriptParser.SentenciaContext stnCtx : ctx.sentencia()) {
            bloqueCode.append("    ").append(visit(stnCtx));
        }
        bloqueCode.append("}");
        return bloqueCode.toString();
    }

    @Override
    public String visitDeclaracion(ShinobiScriptParser.DeclaracionContext ctx) {
        String localType = ctx.tipo().getText();
        String externalType = convertToCppType(localType);
        String id = ctx.ID().getText();
        if(ctx.expresion() != null) {
            return externalType + " " + id + " = " + visit(ctx.expresion());
        }
        return externalType + " " + id;
    }

    @Override
    public String visitAsignacion(ShinobiScriptParser.AsignacionContext ctx) {
        String id = ctx.ID().getText();
        return id + " = " + visit(ctx.expresion());
    }

    @Override
    public String visitEParentesis(ShinobiScriptParser.EParentesisContext ctx) {
        return "(" + visit(ctx.expresion()) + ")";
    }

    @Override
    public String visitENegacion(ShinobiScriptParser.ENegacionContext ctx) {
        return "!" + visit(ctx.expresion());
    }

    @Override
    public String visitEAditiva(ShinobiScriptParser.EAditivaContext ctx) {
        String left = visit(ctx.expresion(0));
        String right = visit(ctx.expresion(1));
        String op = (ctx.op.getText()).equals("ZOKA") ? " + " : " - ";
        return left + op + right;
    }

    @Override
    public String visitEMultiplicativa(ShinobiScriptParser.EMultiplicativaContext ctx) {
        String left = visit(ctx.expresion(0));
        String right = visit(ctx.expresion(1));
        String op = (ctx.op.getText()).equals("BAI") ? " * " : " / ";
        return left + op + right;
    }

    @Override
    public String visitERelacional(ShinobiScriptParser.ERelacionalContext ctx) {
        String left = visit(ctx.expresion(0));
        String right = visit(ctx.expresion(1));
        String localOp = ctx.op.getText();
        String externalOp;
        switch(localOp) {
            case "SAI" -> externalOp = " < ";
            case "DAI" -> externalOp = " > ";
            case "SAITO" -> externalOp = " <= ";
            case "DAITO" -> externalOp = " >= ";
            default -> externalOp = " /*error*/ ";
        }
        return left + externalOp + right;
    }

    @Override
    public String visitEIgualdad(ShinobiScriptParser.EIgualdadContext ctx) {
        String left = visit(ctx.expresion(0));
        String right = visit(ctx.expresion(1));
        String op = (ctx.op.getText().equals("ONAJI")) ? " == " : " != ";
        return left + op + right;
    }

    @Override
    public String visitELogica(ShinobiScriptParser.ELogicaContext ctx) {
        String left = visit(ctx.expresion(0));
        String right = visit(ctx.expresion(1));
        String op = (ctx.op.getText().equals("TO")) ? " && " : " || ";
        return left + op + right;
    }

    @Override
    public String visitELlamada(ShinobiScriptParser.ELlamadaContext ctx) {
        return visit(ctx.llamadaFuncion());
    }

    @Override
    public String visitLlamadaFuncion(ShinobiScriptParser.LlamadaFuncionContext ctx) {
        StringBuilder funcCode = new StringBuilder();
        String funcId = ctx.ID().getText();
        funcCode.append(funcId).append("(");
        if(ctx.expresion() != null && !ctx.expresion().isEmpty()) {
            for(int i = 0; i < ctx.expresion().size(); i++) {
                funcCode.append(visit(ctx.expresion(i)));
                if( i < ctx.expresion().size() - 1) {
                    funcCode.append(", ");
                }
            }
        }
        funcCode.append(")");
        return funcCode.toString();
    }

    @Override
    public String visitEVariable(ShinobiScriptParser.EVariableContext ctx) {
        return ctx.ID().getText();
    }

    @Override
    public String visitELiteral(ShinobiScriptParser.ELiteralContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public String visitDeclaracionFor(ShinobiScriptParser.DeclaracionForContext ctx) {
        StringBuilder forCode = new StringBuilder();

        forCode.append("for(");

        if(ctx.declaracion() != null) {
            forCode.append(visit(ctx.declaracion())).append("; ");
        }else {
            forCode.append(visit(ctx.asignacion(0))).append("; ");
        }

        forCode.append(visit(ctx.expresion())).append("; ");

        int asignIndex = (ctx.asignacion().size() > 1) ? 1 : 0;
        forCode.append(visit(ctx.asignacion(asignIndex)));

        forCode.append(") ");
        forCode.append(visit(ctx.bloque()));
        return forCode.toString();
    }

    @Override
    public String visitDeclaracionWhile(ShinobiScriptParser.DeclaracionWhileContext ctx) {
        StringBuilder whileCode = new StringBuilder();
        whileCode.append("while(");
        whileCode.append(visit(ctx.expresion()));
        whileCode.append(") ");
        whileCode.append(visit(ctx.bloque()));
        return whileCode.toString();
    }

    @Override
    public String visitDeclaracionDoWhile(ShinobiScriptParser.DeclaracionDoWhileContext ctx) {
        StringBuilder doCode = new StringBuilder();
        doCode.append("do ");
        doCode.append(visit(ctx.bloque()));
        doCode.append("while (").append(visit(ctx.expresion())).append(");\n");
        return doCode.toString();
    }

    @Override
    public String visitDeclaracionIf(ShinobiScriptParser.DeclaracionIfContext ctx) {
        StringBuilder firstIfCode = new StringBuilder();
        firstIfCode.append("if(").append(visit(ctx.expresion(0))).append(") ");
        firstIfCode.append(visit(ctx.bloque(0)));

        String firstIfCodeStr = firstIfCode.toString();
        firstIfCodeStr.replaceAll("[\n\r]"," ");
        int indexCounter = 1;

        StringBuilder elseIfCode = new StringBuilder();
        if(ctx.expresion().size() > 1) {
            for(int i = 1; i < ctx.expresion().size(); i++) {
                StringBuilder elseIfTempCode = new StringBuilder();
                elseIfTempCode.append("else if(").append(visit(ctx.expresion(i))).append(") ");
                elseIfTempCode.append(visit(ctx.bloque(i)));
                String elseIfTempCodeStr = elseIfTempCode.toString();
                elseIfTempCodeStr.replaceAll("[\n\r]"," ");
                elseIfCode.append(elseIfTempCode).append(" ");
                indexCounter++;
            }
        }
        StringBuilder ifCode = new StringBuilder();
        ifCode.append(firstIfCode);
        if(!elseIfCode.isEmpty()) {
            ifCode.append(elseIfCode);
        }
        if(ctx.SORE() != null) {
            ifCode.append("else ").append(visit(ctx.bloque(indexCounter)));
        }

        return ifCode.toString();
    }

    @Override
    public String visitDeclaracionSwitch(ShinobiScriptParser.DeclaracionSwitchContext ctx) {
        StringBuilder switchCode = new StringBuilder();
        switchCode.append("switch(").append(visit(ctx.expresion())).append(") {\n");
        for(ShinobiScriptParser.DeclaracionCaseContext caseCtx : ctx.declaracionCase()) {
            switchCode.append(visit(caseCtx)).append("\n");
        }
        if(ctx.declaracionDefault() != null) {
            switchCode.append(visit(ctx.declaracionDefault())).append("\n");
        }
        switchCode.append("}");
        return switchCode.toString();
    }

    @Override
    public String visitDeclaracionCase(ShinobiScriptParser.DeclaracionCaseContext ctx) {
        StringBuilder caseCode = new StringBuilder();
        caseCode.append("\tcase ").append(visit(ctx.literal())).append(":\n");
        if(ctx.sentencia() != null) {
            for(ShinobiScriptParser.SentenciaContext sentenciaCtx : ctx.sentencia()) {
                caseCode.append("\t\t").append(visit(sentenciaCtx));
            }
        }
        caseCode.append("\t\tbreak;");
        return caseCode.toString();
    }

    @Override
    public String visitDeclaracionDefault(ShinobiScriptParser.DeclaracionDefaultContext ctx) {
        StringBuilder defaultCode = new StringBuilder();
        defaultCode.append("\tdefault:\n");
        if(ctx.sentencia() != null) {
            for(ShinobiScriptParser.SentenciaContext sentenciaCtx : ctx.sentencia()) {
                defaultCode.append("\t\t").append(visit(sentenciaCtx));
            }
        }
        if(ctx.KOWASU() != null) {
            defaultCode.append("\t\tbreak;");
        }
        return defaultCode.toString();
    }

    private String convertToCppType(String type) {
        return switch (type) {
            case "CHAKRA" -> "int";
            case "RYO" -> "double";
            case "SHINRI" -> "bool";
            case "KANA" -> "char";
            case "MOJI" -> "string";
            case "MU" -> "void";
            default -> "/* error_type */ void";
        };
    }
}
