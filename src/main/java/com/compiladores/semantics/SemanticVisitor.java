package com.compiladores.semantics;

import com.compiladores.ShinobiScriptBaseVisitor;
import com.compiladores.ShinobiScriptParser;
import com.compiladores.models.CallModel;
import com.compiladores.models.ErrorType;
import com.compiladores.models.ScopeModel;
import com.compiladores.semantics.handlers.SemanticErrorHandler;
import com.compiladores.semantics.models.Category;
import com.compiladores.semantics.models.Symbol;
import com.compiladores.semantics.models.Type;

import java.util.*;

public class SemanticVisitor extends ShinobiScriptBaseVisitor<Type> {
    private final ScopeManager scopeManager;
    private final SemanticErrorHandler errorHandler;
    private final CallManager callManager;

    private Type currentFunctionReturnType = null;

    public SemanticVisitor() {
        this.scopeManager = ScopeManager.getInstance();
        this.errorHandler = new SemanticErrorHandler(ErrorType.SEMANTICO);
        this.callManager = CallManager.getInstance();
    }

    public SemanticErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public Map<String, List<ScopeModel>> getScopesReport() {
        return this.scopeManager.getScopes();
    }

    public Map<String, List<CallModel>> getCallsReport() {
        return this.callManager.getCalls();
    }

    public void resetSemanticVisitor() {
        scopeManager.reset();
        callManager.reset();
    }

    @Override
    public Type visitInit(ShinobiScriptParser.InitContext ctx) {
        for(ShinobiScriptParser.DeclaracionFuncionContext func : ctx.declaracionFuncion()) {
            visit(func);
        }

        visit(ctx.main());

        return Type.MU;
    }

    @Override
    public Type visitMain(ShinobiScriptParser.MainContext ctx) {
        this.currentFunctionReturnType = Type.CHAKRA;

        scopeManager.push("MAIN_KAKEMONO_LN" + ctx.start.getLine());
        visit(ctx.bloque());
        scopeManager.pop();

        this.currentFunctionReturnType = null;
        return Type.CHAKRA;
    }

    @Override
    public Type visitTipo(ShinobiScriptParser.TipoContext ctx) {
        if(ctx.CHAKRA() != null) return Type.CHAKRA;
        if(ctx.RYO() != null) return Type.RYO;
        if(ctx.KANA() != null) return Type.KANA;
        if(ctx.MOJI() != null) return Type.MOJI;
        if(ctx.SHINRI() != null) return Type.SHINRI;

        return Type.ERROR;
    }

    @Override
    public Type visitLInt(ShinobiScriptParser.LIntContext ctx) {
        return Type.CHAKRA;
    }

    @Override
    public Type visitLDouble(ShinobiScriptParser.LDoubleContext ctx) {
        return Type.RYO;
    }

    @Override
    public Type visitLChar(ShinobiScriptParser.LCharContext ctx) {
        return Type.KANA;
    }

    @Override
    public Type visitLString(ShinobiScriptParser.LStringContext ctx) {
        return Type.MOJI;
    }

    @Override
    public Type visitLTrue(ShinobiScriptParser.LTrueContext ctx) {
        return Type.SHINRI;
    }

    @Override
    public Type visitLFalse(ShinobiScriptParser.LFalseContext ctx) {
        return Type.SHINRI;
    }

    @Override
    public Type visitSDeclaracion(ShinobiScriptParser.SDeclaracionContext ctx) {
        String id = ctx.declaracion().ID().getText();

        String txtType = ctx.declaracion().tipo().getText();
        Type type = Type.toType(txtType);

        if(ctx.declaracion().expresion() != null) {
            Type expresionType = visit(ctx.declaracion().expresion());

            if(expresionType != null && expresionType != Type.ERROR && type != expresionType) {
                errorHandler.addSemanticError(
                        "IncompatibleElementConflict: No puedes sellar un elemento " + expresionType + " en un contenedor " + type + ".",
                        ctx.start.getLine(),
                        ctx.start.getCharPositionInLine()
                );
            }
        }

        Symbol newSymbol = new Symbol.Builder(id)
                .setType(type)
                .setCategory(Category.VARIABLE)
                .setLineOfDeclaration(ctx.start.getLine())
                .build();

        boolean successful = scopeManager.getCurrentScope().insert(newSymbol);

        if(!successful) {
            errorHandler.addSemanticError(
                    "DuplicateClanMemberException: El nombre " + id + " ya ha sido reclamado por otro ninja en este equipo.",
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine()
            );
        }

        return Type.MU;
    }

    @Override
    public Type visitEVariable(ShinobiScriptParser.EVariableContext ctx) {
        String id = ctx.ID().getText();

        Symbol s = scopeManager.getCurrentScope().lookup(id);

        if(s == null) {
            errorHandler.addSemanticError(
                    "UndeclaredNinjaException: El ninja [ " + id + " ], no ha sido declarado en este contexto.",
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine()
            );

            return Type.ERROR;
        }

        s.addUsage(ctx.start.getLine());

        return s.getType();
    }

    @Override
    public Type visitEMultiplicativa(ShinobiScriptParser.EMultiplicativaContext ctx) {
        return validateArithmeticRule(
                visit(ctx.expresion(0)),
                visit(ctx.expresion(1)),
                ctx.op.getText(),
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine()
        );
    }

    @Override
    public Type visitEAditiva(ShinobiScriptParser.EAditivaContext ctx) {
        return validateArithmeticRule(
                visit(ctx.expresion(0)),
                visit(ctx.expresion(1)),
                ctx.op.getText(),
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine()
        );
    }

    @Override
    public Type visitERelacional(ShinobiScriptParser.ERelacionalContext ctx) {
        return validateComparisonRule(
                visit(ctx.expresion(0)),
                visit(ctx.expresion(1)),
                ctx.op.getText(),
                true,
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine()
        );
    }

    @Override
    public Type visitEIgualdad(ShinobiScriptParser.EIgualdadContext ctx) {
        return validateComparisonRule(
                visit(ctx.expresion(0)),
                visit(ctx.expresion(1)),
                ctx.op.getText(),
                false,
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine()
        );
    }

    @Override
    public Type visitELogica(ShinobiScriptParser.ELogicaContext ctx) {
        Type izq = visit(ctx.expresion(0));
        Type der = visit(ctx.expresion(1));
        String op = ctx.op.getText();

        if(izq == Type.ERROR || der == Type.ERROR) return Type.ERROR;

        if(izq == Type.SHINRI && der == Type.SHINRI) return Type.SHINRI;

        errorHandler.addSemanticError(
                "ForbiddenJutsuOperation: El operador lógico [ " + op + " ] no puede comparar los elementos " + izq + " con " + der + ".",
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine()
        );

        return Type.ERROR;
    }

    @Override
    public Type visitENegacion(ShinobiScriptParser.ENegacionContext ctx) {
        Type exp = visit(ctx.expresion());

        if(exp == Type.ERROR) return Type.ERROR;

        if(exp == Type.SHINRI) return Type.SHINRI;

        errorHandler.addSemanticError(
                "ForbiddenJutsuOperation: No puedes aplicar el sello de negacion a un elemento del tipo: " + exp + ".",
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine()
        );

        return Type.ERROR;
    }

    @Override
    public Type visitEParentesis(ShinobiScriptParser.EParentesisContext ctx) {
        return visit(ctx.expresion());
    }

    @Override
    public Type visitELiteral(ShinobiScriptParser.ELiteralContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public Type visitDeclaracionIf(ShinobiScriptParser.DeclaracionIfContext ctx) {
        Type initConditionType = visit(ctx.expresion(0));
        validateBooleanCondition(initConditionType, ctx.expresion(0));

        for(int i = 0; i < ctx.expresion().size(); i++) {
            Type nestedConditionType = visit(ctx.expresion(i));
            validateBooleanCondition(nestedConditionType, ctx.expresion(i));
        }

        for(int i = 0; i < ctx.bloque().size(); i++) {
            String tag = (i == 0) ? "IF" : (i < ctx.expresion().size() ? "ELSE IF" : "THEN");

            scopeManager.push(tag + "_LN" + ctx.bloque(i).start.getLine());

            visit(ctx.bloque(i));

            scopeManager.pop();
        }

        return Type.MU;
    }

    @Override
    public Type visitDeclaracionSwitch(ShinobiScriptParser.DeclaracionSwitchContext ctx) {
        scopeManager.push("SWITCH_LN" + ctx.start.getLine());
        Type initConditionType = visit(ctx.expresion());

        if(initConditionType == Type.ERROR) return Type.ERROR;

        List<ShinobiScriptParser.DeclaracionCaseContext> cases = ctx.declaracionCase();

        if(cases.isEmpty()) {
            errorHandler.addSemanticError(
                    "ForbbidenJutsuOperation: se requiere de por lo menos una elección dentro de la transformación",
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        boolean areCasesOk = true;
        int caseIndex = 0;
        Set<String> literals = new HashSet<>();

        for(ShinobiScriptParser.DeclaracionCaseContext caseContext : cases) {
            String literalText = caseContext.literal().getText();

            if(!literals.add(literalText)) {
                errorHandler.addSemanticError(
                        "DuplicatedClanMemberException: la eleccion " + literalText + " ya existe en esta transformación.",
                        caseContext.start.getLine(),
                        caseContext.start.getCharPositionInLine()
                );

                if(areCasesOk) {
                    areCasesOk = false;
                }
            }

            Type caseType = visit(caseContext.literal());
            caseIndex++;
            if(caseType != Type.ERROR && caseType != initConditionType) {
                errorHandler.addSemanticError(
                        "IncompatibleElementConflict: La elección #" + caseIndex + " en la transformación no se puede evaluar, al tratar de elegir un tipo "
                        + caseType + " de un tipo " + initConditionType + ".",
                        caseContext.start.getLine(),
                        caseContext.start.getCharPositionInLine()
                );

                if(areCasesOk) {
                    areCasesOk = false;
                }
            }
        }

        if(!areCasesOk) {
            errorHandler.addSemanticError(
                    "ForbbidenJutsuOperation: Los operdores de una transformación (switch) deben ser del mismo tipo.",
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        for(ShinobiScriptParser.DeclaracionCaseContext caseContext : cases) {
            for(ShinobiScriptParser.SentenciaContext sentence : caseContext.sentencia()) {
                visit(sentence);
            }
        }

        ShinobiScriptParser.DeclaracionDefaultContext defaultContext = ctx.declaracionDefault();

        if(defaultContext != null) {
            visit(defaultContext);
        }

        scopeManager.pop();
        return Type.MU;
    }

    @Override
    public Type visitDeclaracionCase(ShinobiScriptParser.DeclaracionCaseContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public Type visitDeclaracionDefault(ShinobiScriptParser.DeclaracionDefaultContext ctx) {

        for(ShinobiScriptParser.SentenciaContext sentence : ctx.sentencia()) {
            visit(sentence);
        }

        return Type.MU;
    }

    @Override
    public Type visitDeclaracionFor(ShinobiScriptParser.DeclaracionForContext ctx) {
        scopeManager.push("FOR_LN"+ctx.start.getLine());

        boolean hasInitialAssignment = false;

        if(ctx.declaracion() != null) {
            visit(ctx.declaracion());
        }else if(ctx.asignacion() != null && !ctx.asignacion().isEmpty()) {
            hasInitialAssignment = true;
            visit(ctx.asignacion(0));
        }

        if(ctx.expresion() != null) {
            Type expType = visit(ctx.expresion());
            if(expType != Type.ERROR && expType != Type.SHINRI) {
                errorHandler.addSemanticError(
                        "ConditionNotShinriException: La condicion debe ser SHINRI (boolean) pero en cambio se recibio " + expType + ".",
                        ctx.expresion().start.getLine(),
                        ctx.expresion().start.getCharPositionInLine()
                );
            }
        }

        visit(ctx.bloque());

        if(hasInitialAssignment) {
            visit(ctx.asignacion(0));
        }else if(ctx.asignacion().size() > 1){
            visit(ctx.asignacion(1));
        }


        scopeManager.pop();
        return Type.MU;
    }

    @Override
    public Type visitDeclaracionWhile(ShinobiScriptParser.DeclaracionWhileContext ctx) {
        Type initConditionType = visit(ctx.expresion());

        if(initConditionType == Type.ERROR) return Type.ERROR;

        if(initConditionType != Type.SHINRI) {
            errorHandler.addSemanticError(
                    "IncompatibleElementConflict: la condicion debe ser SHINRI (boolean) pero en cambio se recibio " + initConditionType + ".",
                    ctx.expresion().start.getLine(),
                    ctx.expresion().start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        scopeManager.push("WHILE_LN"+ctx.start.getLine());

        visit(ctx.bloque());

        scopeManager.pop();

        return Type.MU;
    }

    @Override
    public Type visitIDoWhile(ShinobiScriptParser.IDoWhileContext ctx) {
        return visit(ctx.declaracionDoWhile());
    }

    @Override
    public Type visitDeclaracionDoWhile(ShinobiScriptParser.DeclaracionDoWhileContext ctx) {
        scopeManager.push("DO_WHILE_LN"+ctx.start.getLine());

        visit(ctx.bloque());

        Type initConditionType = visit(ctx.expresion());

        scopeManager.pop();

        if(initConditionType == Type.ERROR) return Type.ERROR;

        if(initConditionType != Type.SHINRI) {
            errorHandler.addSemanticError(
                    "IncompatibleElementConflict: la condicion debe ser SHINRI (boolean) pero en cambio se recibio " + initConditionType + ".",
                    ctx.expresion().start.getLine(),
                    ctx.expresion().start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        return Type.MU;
    }

    @Override
    public Type visitSImpresion(ShinobiScriptParser.SImpresionContext ctx) {
        return visit(ctx.impresion());
    }

    @Override
    public Type visitImpresion(ShinobiScriptParser.ImpresionContext ctx) {
        Type expType = visit(ctx.expresion());

        if(expType == Type.ERROR) return Type.ERROR;

        if(expType == Type.MU) {
            errorHandler.addSemanticError(
                    "ForbiddenJutsuOperation: No se puede proyectar el vacío (MU) en la consola.",
                    ctx.expresion().start.getLine(),
                    ctx.expresion().start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        return Type.MU;
    }

    @Override
    public Type visitDeclaracionFuncion(ShinobiScriptParser.DeclaracionFuncionContext ctx) {
        if(ctx.MU() != null) {
            this.currentFunctionReturnType = Type.MU;
        } else {
            this.currentFunctionReturnType = visit(ctx.tipo());
        }

        String functionName = ctx.ID().getText();

        Symbol s = scopeManager.getCurrentScope().lookup(functionName);

        if(s != null) {
            errorHandler.addSemanticError(
                    "DuplicatedClanMemberException: el Jutsu [ " + functionName + " ] ya ha sido creado por otro ninja en otra aldea.",
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        Symbol.Builder funcBuild = new Symbol.Builder(functionName)
                .setType(this.currentFunctionReturnType)
                .setCategory(Category.FUNCTION)
                .setLineOfDeclaration(ctx.start.getLine());

        ShinobiScriptParser.ParametrosContext paramsCtx = ctx.parametros();

        List<Symbol> params = new ArrayList<>();

        if(paramsCtx != null) {
            for(int i = 0; i<paramsCtx.tipo().size(); i++) {
                Type pType = visit(paramsCtx.tipo(i));
                String pName = paramsCtx.ID(i).getText();

                for(Symbol existingSymbol : params) {
                    if(existingSymbol.getName().equals(pName)) {
                        errorHandler.addSemanticError(
                                "DuplicatedClanMemberException: El paramtero [ " + pName + " ] ya esta definido dentro de la firma de este Jutsu.",
                                paramsCtx.ID(i).getSymbol().getLine(),
                                paramsCtx.ID(i).getSymbol().getCharPositionInLine()
                        );
                    }
                }

                funcBuild.addParameter(pType);

                Symbol paramSymbol = new Symbol.Builder(pName)
                        .setType(pType)
                        .setCategory(Category.PARAMETER)
                        .setLineOfDeclaration(paramsCtx.ID(i).getSymbol().getLine())
                        .build();

                params.add(paramSymbol);
            }
        }

        Symbol functionSymbol = funcBuild.build();
        scopeManager.getCurrentScope().insert(functionSymbol);

        scopeManager.push("JUTSU_"+functionName.toUpperCase() +"_LN" + ctx.start.getLine());
        if(!params.isEmpty()) {
            for(Symbol param : params) {
                scopeManager.getCurrentScope().insert(param);
            }
        }

        visit(ctx.bloque());

        scopeManager.pop();
        this.currentFunctionReturnType = null;
        return Type.MU;
    }

    @Override
    public Type visitSLlamada(ShinobiScriptParser.SLlamadaContext ctx) {
        String callee = ctx.llamadaFuncion().ID().getText();
        String caller = scopeManager.getCurrentScope().getScopeName();
        callManager.add(caller,callee, ctx.start.getLine());

        visit(ctx.llamadaFuncion());
        return Type.MU;
    }

    @Override
    public Type visitELlamada(ShinobiScriptParser.ELlamadaContext ctx) {
        String callee = ctx.llamadaFuncion().ID().getText();
        String caller = scopeManager.getCurrentScope().getScopeName();
        callManager.add(caller,callee, ctx.start.getLine());

        Type returnType = visit(ctx.llamadaFuncion());

        if(returnType == Type.ERROR) return Type.ERROR;
        if(returnType == Type.MU) {
            errorHandler.addSemanticError(
                    "ForbiddenJutsuOperation: El Jutsu [ " + ctx.llamadaFuncion().ID().getText() +
                            " ] no devuelve energía (MU) y no puede ser parte de una expresión.",
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        return returnType;
    }

    @Override
    public Type visitLlamadaFuncion(ShinobiScriptParser.LlamadaFuncionContext ctx) {
        String functionName = ctx.ID().getText();
        Symbol functionSymbol = scopeManager.getCurrentScope().lookup(functionName);

        if(functionSymbol == null || functionSymbol.getCategory() != Category.FUNCTION) {
            errorHandler.addSemanticError(
                    "ForbiddenJutsuOperation: El Jutsu [ " + functionName + " ] no ha sido invocado por que no existe en ninguan aldea.",
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        List<ShinobiScriptParser.ExpresionContext> sentArguments = ctx.expresion();
        List<Type> expectedTypes = functionSymbol.getParametersTypes();

        if(sentArguments.size() != expectedTypes.size()) {
            errorHandler.addSemanticError(
                    "IncompatibleElementConflict: El Jutsu [ " + functionName + " ] esperaba " +
                            expectedTypes.size() + " pergaminos, pero se enviaron " + sentArguments.size() + ".",
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        for(int i = 0; i<sentArguments.size(); i++) {
            Type pType = visit(sentArguments.get(i));
            Type eType = expectedTypes.get(i);

            if(pType != Type.ERROR && pType != eType) {
                errorHandler.addSemanticError(
                        "IncompatibleElementConflict: El pergamino #" + (i+1) + " para el Jutsu [ " +
                                functionName + " ] deberia ser " + eType + " pero es " + pType + ".",
                        sentArguments.get(i).start.getLine(),
                        sentArguments.get(i).start.getCharPositionInLine()
                );
            }
        }

        return functionSymbol.getType();
    }

    @Override
    public Type visitSRetorno(ShinobiScriptParser.SRetornoContext ctx) {
        return visit(ctx.retorno());
    }

    @Override
    public Type visitRetorno(ShinobiScriptParser.RetornoContext ctx) {
        Type returnTypeFound = (ctx.expresion() != null) ? visit(ctx.expresion()) : Type.MU;

        if(this.currentFunctionReturnType == null) {
            errorHandler.addSemanticError(
                    "ForbiddenJutsuOperation: El sello KUCHIYOSE (return) solo puede usarse dentro de un Jutsu.",
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        if(returnTypeFound != Type.ERROR ) {
            if(this.currentFunctionReturnType == Type.MU && returnTypeFound != Type.MU) {
                errorHandler.addSemanticError(
                        "IncompatibleElementConcflict: El Jutsu que invoca al sello es de tipo MU, pero el sello no retorna ese valor.",
                        ctx.start.getLine(),
                        ctx.start.getCharPositionInLine()
                );
                return Type.ERROR;
            }
            if(returnTypeFound != this.currentFunctionReturnType) {
                errorHandler.addSemanticError(
                        "IncompatibleElementConflict: Se esperaba devolver " + this.currentFunctionReturnType +
                                " pero se encontro " + returnTypeFound + ".",
                        ctx.start.getLine(),
                        ctx.start.getCharPositionInLine()
                );
                return Type.ERROR;
            }
        }

        return returnTypeFound;
    }

    @Override
    public Type visitDeclaracion(ShinobiScriptParser.DeclaracionContext ctx) {
        String id = ctx.ID().getText();
        Type tipoDeclarado = Type.toType(ctx.tipo().getText());

        if (scopeManager.getCurrentScope().getSymbols().containsKey(id)) {
            errorHandler.addSemanticError(
                    "NinjaCloningException: El ninja [ " + id + " ] ya ha sido entrenado en este contexto.",
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine()
            );
            return Type.ERROR;
        }

        if (ctx.expresion() != null) {
            Type tipoExpresion = visit(ctx.expresion());
            if (tipoExpresion != Type.ERROR && tipoDeclarado != tipoExpresion) {
                errorHandler.addSemanticError(
                        "ChakraMismatchException: No se puede asignar " + tipoExpresion +
                                " a un ninja de tipo " + tipoDeclarado + ".",
                        ctx.expresion().start.getLine(),
                        ctx.expresion().start.getCharPositionInLine()
                );
                return Type.ERROR;
            }
        }

        Symbol nuevoNinja = new Symbol.Builder(id)
                .setType(tipoDeclarado)
                .setCategory(Category.VARIABLE)
                .setLineOfDeclaration(ctx.start.getLine())
                .build();

        scopeManager.getCurrentScope().insert(nuevoNinja);

        return tipoDeclarado;
    }

    private Type validateArithmeticRule(Type izq, Type der, String op, int line, int column) {
        if(izq == Type.ERROR || der == Type.ERROR) return Type.ERROR;

        if(izq == Type.CHAKRA && der == Type.CHAKRA) return Type.CHAKRA;
        if((izq == Type.CHAKRA || izq == Type.RYO) && (der == Type.CHAKRA || der == Type.RYO)) return Type.RYO;
        if(izq == Type.MOJI && der == Type.MOJI) return Type.MOJI;

        errorHandler.addSemanticError(
                "ForbiddenJutsuOperation: El operador aritmetico \'" + op + "\' no puede combinar elementos " + izq + " con " + der + ".",
                line,
                column
        );

        return Type.ERROR;
    }

    private Type validateComparisonRule(Type izq, Type der, String op, boolean isOnlyNumberComparison, int line, int column) {
        if(izq == Type.ERROR || der == Type.ERROR) return Type.ERROR;

        if((izq == Type.CHAKRA || izq == Type.RYO) && (der == Type.CHAKRA || der == Type.RYO)) return Type.SHINRI;
        if(!isOnlyNumberComparison) {
            if(izq == der && izq != Type.MU) return Type.SHINRI;
        }

        errorHandler.addSemanticError(
                "IncompatibleElementConflict: No se puede comparar " + izq + " con " + der + " usando el operador " + op + ".",
                line,
                column
        );

        return Type.ERROR;
    }

    private void validateBooleanCondition(Type type, ShinobiScriptParser.ExpresionContext expCtx) {
        if(type != Type.ERROR && type != Type.SHINRI) {
            errorHandler.addSemanticError(
                    "ConditionNotShinriExcpetion: La condicion debe ser SHINRI (boolean) pero en cambio se recibió " + type + ".",
                    expCtx.start.getLine(),
                    expCtx.start.getCharPositionInLine()
            );
        }
    }
}
