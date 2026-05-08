package com.compiladores.transpiler;

import com.compiladores.ShinobiScriptBaseVisitor;
import com.compiladores.ShinobiScriptParser;
import com.compiladores.semantics.ScopeManager;
import com.compiladores.semantics.models.Symbol;
import com.compiladores.semantics.models.Type;

import java.util.ArrayList;
import java.util.List;

public class ThreeAddressCodeVisitor extends ShinobiScriptBaseVisitor<String> {
    private int tempCount = 0;
    private int labelCount = 0;
    private String currentSwitchEndLabel = "";
    private String lastResult = "";
    private String sourceCode = "";
    private ScopeManager scopeManager = ScopeManager.getInstance();

    public String getSourceCode() {
        return this.sourceCode;
    }

    public void resetThreeAddressCodeVisitor() {
        this.sourceCode = "";
        this.tempCount = 0;
        this.labelCount = 0;
        this.currentSwitchEndLabel = "";
        this.lastResult = "";
    }

    private String generateLabel(String prefix) {
        return "L_" + prefix + "_" + (labelCount++);
    }
    private String generateTemporal() {
        return "t_" + (tempCount++);
    }

    @Override
    public String visitLInt(ShinobiScriptParser.LIntContext ctx) {
        this.lastResult = ctx.INT().getText();
        return "";
    }

    @Override
    public String visitLDouble(ShinobiScriptParser.LDoubleContext ctx) {
        this.lastResult = ctx.DOUBLE().getText();
        return "";
    }

    @Override
    public String visitLChar(ShinobiScriptParser.LCharContext ctx) {
        this.lastResult = ctx.CHAR().getText();
        return "";
    }

    @Override
    public String visitLString(ShinobiScriptParser.LStringContext ctx) {
        this.lastResult = ctx.STRING().getText();
        return "";
    }

    @Override
    public String visitLTrue(ShinobiScriptParser.LTrueContext ctx) {
        this.lastResult = "true";
        return "";
    }

    @Override
    public String visitLFalse(ShinobiScriptParser.LFalseContext ctx) {
        this.lastResult = "false";
        return "";
    }

    @Override
    public String visitInit(ShinobiScriptParser.InitContext ctx) {
        StringBuilder program = new StringBuilder();

        program.append("#include <iostream>\n");
        program.append("#include <string>\n\n");
        program.append("#using namespace std;\n\n");

        List<ShinobiScriptParser.DeclaracionFuncionContext> functions = ctx.declaracionFuncion();

        if(!functions.isEmpty()) {
            for(ShinobiScriptParser.DeclaracionFuncionContext func : functions) {
                program.append(visit(func));
            }
        }

        program.append(visit(ctx.main()));

        this.sourceCode = program.toString();
        return "";
    }

    @Override
    public String visitMain(ShinobiScriptParser.MainContext ctx) {
        StringBuilder main = new StringBuilder();
        main.append("int main() {\n");
        main.append(visit(ctx.bloque()));
        main.append("\n    return 0;\n}\n");
        return main.toString();
    }

    @Override
    public String visitBloque(ShinobiScriptParser.BloqueContext ctx) {
        StringBuilder code = new StringBuilder();
        code.append("\n");
        if(!ctx.sentencia().isEmpty()) {
            for(ShinobiScriptParser.SentenciaContext stnCtx : ctx.sentencia()) {
                code.append(visit(stnCtx));
            }
        }
        code.append("\n");
        return code.toString();
    }

    @Override
    public String visitSAsignacion(ShinobiScriptParser.SAsignacionContext ctx) {
        return visit(ctx.asignacion());
    }

    @Override
    public String visitSImpresion(ShinobiScriptParser.SImpresionContext ctx) {
        return visit(ctx.impresion());
    }

    @Override
    public String visitSLectura(ShinobiScriptParser.SLecturaContext ctx) {
        return visit(ctx.lectura());
    }

    @Override
    public String visitSDeclaracion(ShinobiScriptParser.SDeclaracionContext ctx) {
        return visit(ctx.declaracion());
    }

    @Override
    public String visitSRetorno(ShinobiScriptParser.SRetornoContext ctx) {
        return visit(ctx.retorno());
    }

    @Override
    public String visitSControl(ShinobiScriptParser.SControlContext ctx) {
        return visit(ctx.sentenciaControl());
    }

    @Override
    public String visitSIterativa(ShinobiScriptParser.SIterativaContext ctx) {
        return visit(ctx.sentenciaIterativa());
    }

    @Override
    public String visitSLlamada(ShinobiScriptParser.SLlamadaContext ctx) {
        return visit(ctx.llamadaFuncion());
    }

    @Override
    public String visitSBloque(ShinobiScriptParser.SBloqueContext ctx) {
        return visit(ctx.bloque());
    }

    @Override
    public String visitDeclaracionIf(ShinobiScriptParser.DeclaracionIfContext ctx) {
        StringBuilder code = new StringBuilder();
        String endLabel = generateLabel("IF_END");

        code.append(visit(ctx.expresion(0)));

        String firstCondition = this.lastResult;
        String firstTrueLabel = generateLabel("IF_TRUE");
        String nextEvalLabel = generateLabel("NEXT_COND");

        code.append("if (")
                .append(firstCondition)
                .append(")")
                .append(" goto ")
                .append(firstTrueLabel)
                .append(";\n");

        code.append(firstTrueLabel).append(":\n");
        code.append(
                visit(ctx.bloque(0))
        );
        code.append("goto ").append(endLabel).append(";\n");

        code.append(nextEvalLabel).append(":\n");
        for(int i = 1; i < ctx.expresion().size(); i++) {
            code.append(visit(ctx.expresion(i)));
            String elseIfCondition = this.lastResult;
            String elseTrueLabel = generateLabel("ELSE_IF_TRUE");
            nextEvalLabel = generateLabel("NEXT_COND");

            code.append("if (")
                    .append(elseIfCondition)
                    .append(")")
                    .append(" goto ")
                    .append(elseTrueLabel)
                    .append(";\n");
            code.append("goto ").append(nextEvalLabel).append(";\n");

            code.append(elseTrueLabel).append(":\n");
            code.append(
                    visit(ctx.bloque(i))
            );
            code.append("goto ").append(endLabel).append(";\n");

            code.append(nextEvalLabel).append(":\n");
        }

        if(ctx.SORE() != null) {
            int lastBlockIndex = ctx.bloque().size() - 1;
            code.append(
                    visit(ctx.bloque(lastBlockIndex))
            );
        }

        code.append(endLabel).append(":\n");
        return code.toString();
    }

    @Override
    public String visitDeclaracionSwitch(ShinobiScriptParser.DeclaracionSwitchContext ctx) {
        StringBuilder code = new StringBuilder();
        code.append(visit(ctx.expresion()));
        String option = this.lastResult;
        String endLabel = generateLabel("SWITCH_END");

        List<String> caseLabels = new ArrayList<>();
        for(int i = 0; i < ctx.declaracionCase().size(); i++) {
            caseLabels.add(generateLabel("CASE_" + i));
        }

        String defaultLabel = (ctx.declaracionDefault() != null) ? generateLabel("DEFAULT") : endLabel;

        for(int i = 0; i < ctx.declaracionCase().size(); i++) {
            String temp = generateTemporal();
            String caseValue = ctx.declaracionCase(i).literal().getText();

            code.append("auto ")
                    .append(temp)
                    .append(" = ")
                    .append(option)
                    .append(" == ")
                    .append(caseValue)
                    .append("\n");

            code.append("if (")
                    .append(temp)
                    .append(")")
                    .append(" goto ")
                    .append(caseLabels.get(i))
                    .append(";\n");
        }
        code.append("goto ").append(defaultLabel).append(";\n");

        for(int i = 0; i < ctx.declaracionCase().size(); i++) {
            code.append(caseLabels.get(i)).append(":\n");
            this.currentSwitchEndLabel = endLabel;
            code.append(visit(ctx.declaracionCase(i)));
        }

        if(ctx.declaracionDefault() != null) {
            code.append(defaultLabel).append(":\n");
            code.append(visit(ctx.declaracionDefault()));
        }

        code.append(endLabel).append(":\n");
        return code.toString();
    }

    @Override
    public String visitDeclaracionCase(ShinobiScriptParser.DeclaracionCaseContext ctx) {
        StringBuilder code = new StringBuilder();

        if(ctx.sentencia() != null) {
            for(int i = 0; i < ctx.sentencia().size(); i++) {
                code.append(
                        visit(ctx.sentencia(i))
                );
            }
        }

        if(ctx.KOWASU() != null) {
            code.append("goto ").append(this.currentSwitchEndLabel).append(";\n");
        }

        return code.toString();
    }

    @Override
    public String visitDeclaracionDefault(ShinobiScriptParser.DeclaracionDefaultContext ctx) {
        StringBuilder code = new StringBuilder();

        if(ctx.sentencia() != null) {
            for(int i = 0; i < ctx.sentencia().size(); i++) {
                code.append(
                        visit(ctx.sentencia(i))
                );
            }
        }

        return code.toString();
    }

    @Override
    public String visitDeclaracionFor(ShinobiScriptParser.DeclaracionForContext ctx) {
        StringBuilder code = new StringBuilder();

        String startLabel = generateLabel("FOR_START");
        String endLabel = generateLabel("FOR_END");

        if(ctx.asignacion(0) != null) {
            code.append(visit(ctx.asignacion(0)));
        } else if(ctx.declaracion() != null) {
            code.append(visit(ctx.declaracion()));
        }

        code.append(startLabel).append(":\n");

        code.append(visit(ctx.expresion()));
        String condition = this.lastResult;
        String trueLabel = generateLabel("FOR_BODY");

        code.append("if (")
                .append(condition)
                .append(")")
                .append(" goto ")
                .append(trueLabel)
                .append(";\n");
        code.append("goto ")
                .append(endLabel)
                .append(";\n");

        code.append(trueLabel).append(":\n");
        code.append(visit(ctx.bloque()));

        int updateIndex = (ctx.asignacion().size() > 1) ? 1:0;

        if(ctx.asignacion(updateIndex) != null && ctx.asignacion().size() > 1) {
            code.append(visit(ctx.asignacion(updateIndex)));
        }

        code.append("goto ").append(startLabel).append(";\n");
        code.append(endLabel).append(":\n");

        return code.toString();
    }

    @Override
    public String visitDeclaracionWhile(ShinobiScriptParser.DeclaracionWhileContext ctx) {
        StringBuilder code = new StringBuilder();

        String startLabel = generateLabel("WHILE_START");
        String endLabel = generateLabel("WHILE_END");
        String bodyLabel = generateLabel("WHILE_BODY");

        code.append(startLabel).append(":\n");

        code.append(visit(ctx.expresion()));

        String condition = this.lastResult;

        code.append("if (").append(condition).append(") goto ").append(bodyLabel).append(";\n");
        code.append("goto ").append(endLabel).append(";\n");

        code.append(bodyLabel).append(":\n");
        code.append(visit(ctx.bloque()));

        code.append("goto ").append(startLabel).append(";\n");

        code.append(endLabel).append(":\n");
        return code.toString();
    }

    @Override
    public String visitDeclaracionDoWhile(ShinobiScriptParser.DeclaracionDoWhileContext ctx) {
        StringBuilder code = new StringBuilder();

        String startLabel = generateLabel("DO_START");
        String endLabel = generateLabel("DO_END");

        code.append(startLabel).append(":\n");
        code.append(visit(ctx.bloque()));

        code.append(visit(ctx.expresion()));

        String condition = this.lastResult;
        code.append("if (")
                .append(condition)
                .append(") goto ")
                .append(startLabel)
                .append(";\n");
        code.append(endLabel).append(":\n");
        return code.toString();
    }

    @Override
    public String visitDeclaracion(ShinobiScriptParser.DeclaracionContext ctx) {
        StringBuilder code = new StringBuilder();

        String variableName = (ctx.ID() != null) ? ctx.ID().getText() : "";

        if(ctx.expresion() != null) {
            code.append(visit(ctx.expresion()));

            code.append("auto ")
                    .append(variableName)
                    .append(" = ")
                    .append(this.lastResult)
                    .append(";\n");
        }

        return code.toString();
    }

    @Override
    public String visitAsignacion(ShinobiScriptParser.AsignacionContext ctx) {
        StringBuilder code = new StringBuilder();

        String variableName = (ctx.ID() != null) ? ctx.ID().getText() : "";

        code.append(visit(ctx.expresion()));

        code.append("auto ")
                .append(variableName)
                .append(" = ")
                .append(this.lastResult)
                .append(";\n");
        return code.toString();
    }

    @Override
    public String visitEParentesis(ShinobiScriptParser.EParentesisContext ctx) {
        return visit(ctx.expresion());
    }

    @Override
    public String visitEVariable(ShinobiScriptParser.EVariableContext ctx) {
        this.lastResult = ctx.ID().getText();
        return "";
    }

    @Override
    public String visitENegacion(ShinobiScriptParser.ENegacionContext ctx) {
        StringBuilder code = new StringBuilder();

        code.append(visit(ctx.expresion()));

        String value = this.lastResult;
        String temp = generateTemporal();

        code.append("auto ").append(temp).append(" = !(").append(value).append(");\n");
        this.lastResult = temp;

        return code.toString();
    }

    @Override
    public String visitEAditiva(ShinobiScriptParser.EAditivaContext ctx) {
        StringBuilder code = new StringBuilder();

        code.append(visit(ctx.expresion(0)));
        String left = this.lastResult;

        code.append(visit(ctx.expresion(1)));
        String right = this.lastResult;

        String temp = generateTemporal();
        String op = ctx.op.getText().equals("ZOKA") ? "+" : "?";

        code.append("auto ")
                .append(temp)
                .append(" = ")
                .append(left)
                .append(" ")
                .append(op)
                .append(" ")
                .append(right)
                .append(";\n");
        this.lastResult = temp;
        return code.toString();
    }

    @Override
    public String visitEMultiplicativa(ShinobiScriptParser.EMultiplicativaContext ctx) {
        StringBuilder code = new StringBuilder();

        code.append(visit(ctx.expresion(0)));
        String left = this.lastResult;

        code.append(visit(ctx.expresion(1)));
        String right = this.lastResult;

        String temp = generateTemporal();
        String op = ctx.op.getText().equals("BAI") ? "*" : "/";

        code.append("auto ")
                .append(temp)
                .append(" = ")
                .append(left)
                .append(" ")
                .append(op)
                .append(" ")
                .append(right)
                .append(";\n");
        this.lastResult = temp;
        return code.toString();
    }

    @Override
    public String visitERelacional(ShinobiScriptParser.ERelacionalContext ctx) {
        StringBuilder code = new StringBuilder();

        code.append(visit(ctx.expresion(0)));
        String left = this.lastResult;

        code.append(visit(ctx.expresion(1)));
        String right = this.lastResult;

        String temp = generateTemporal();
        String op = ctx.op.getText();

        String finalOp;

        switch(op) {
            case "SAI" -> finalOp = "<";
            case "DAI" -> finalOp = ">";
            case "SAITO" -> finalOp = "<=";
            case "DAITO" -> finalOp = ">=";
            default -> finalOp = "";
        }

        code.append("auto ")
                .append(temp)
                .append(" = ")
                .append(left)
                .append(" ")
                .append(finalOp)
                .append(" ")
                .append(right)
                .append(";\n");
        this.lastResult = temp;
        return code.toString();
    }

    public String visitEIgualdad(ShinobiScriptParser.EIgualdadContext ctx) {
        StringBuilder code = new StringBuilder();

        code.append(visit(ctx.expresion(0)));
        String left = this.lastResult;

        code.append(visit(ctx.expresion(1)));
        String right = this.lastResult;

        String temp = generateTemporal();

        String op = ctx.op.getText().equals("ONAJI") ? "==" : "!=";

        code.append("auto ")
                .append(temp)
                .append(" = ")
                .append(left)
                .append(" ")
                .append(op)
                .append(" ")
                .append(right)
                .append(";\n");
        this.lastResult = temp;
        return code.toString();
    }

    @Override
    public String visitELogica(ShinobiScriptParser.ELogicaContext ctx) {
        StringBuilder code = new StringBuilder();

        code.append(visit(ctx.expresion(0)));
        String left = this.lastResult;

        code.append(visit(ctx.expresion(1)));
        String right = this.lastResult;

        String temp = generateTemporal();
        String op = ctx.op.getText().equals("TO") ? "AND" : "OR";

        code.append("auto ")
                .append(temp)
                .append(" = ")
                .append(left)
                .append(" ")
                .append(op)
                .append(" ")
                .append(right)
                .append(";\n");
        this.lastResult = temp;
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

        functionCode.append(") {\n");

        functionCode.append(
                visit(ctx.bloque())
        );

        functionCode.append("}\n");

        return functionCode.toString();
    }

    @Override
    public String visitRetorno(ShinobiScriptParser.RetornoContext ctx) {
        StringBuilder code = new StringBuilder();
        code.append(visit(ctx.expresion()));
        code.append("return ").append(this.lastResult).append(";\n");
        return code.toString();
    }

    @Override
    public String visitELlamada(ShinobiScriptParser.ELlamadaContext ctx) {
        return visit(ctx.llamadaFuncion());
    }

    @Override
    public String visitLlamadaFuncion(ShinobiScriptParser.LlamadaFuncionContext ctx) {
        StringBuilder code = new StringBuilder();

        String funcName = ctx.ID().getText();

        List<ShinobiScriptParser.ExpresionContext> args = ctx.expresion();

        for(ShinobiScriptParser.ExpresionContext argCtx : args) {
            code.append(visit(argCtx));
            code.append("param ").append(this.lastResult).append("\n");
        }

        String temp = generateTemporal();
        code.append(temp)
                .append(" = call ")
                .append(funcName)
                .append(", ")
                .append(args.size())
                .append("\n");

        this.lastResult = temp;

        return code.toString();
    }

    @Override
    public String visitImpresion(ShinobiScriptParser.ImpresionContext ctx) {
        StringBuilder code = new StringBuilder();
        code.append(visit(ctx.expresion()));
        code.append("cout<<").append(this.lastResult).append("<<endl;\n");
        return code.toString();
    }

    @Override
    public String visitLectura(ShinobiScriptParser.LecturaContext ctx) {
        StringBuilder code = new StringBuilder();

        String varName = ctx.ID().getText();
        code.append("cin>>").append(varName).append(";\n");

        return code.toString();
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
