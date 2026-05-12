package com.compiladores;

import com.compiladores.handlers.CustomErrorHandler;
import com.compiladores.io.HTMLReportGenerator;
import com.compiladores.io.ObjectCodeGenerator;
import com.compiladores.io.SourceReader;
import com.compiladores.models.*;
import com.compiladores.semantics.SemanticVisitor;
import com.compiladores.semantics.handlers.SemanticErrorHandler;
import com.compiladores.transpiler.ThreeAddressCodeVisitor;
import com.compiladores.transpiler.TranslationVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name="ArrayIntCommand", mixinStandardHelpOptions = true, version = "0.0.1", description = "Ultra basic ANTLR4 analizer")
public class App implements Callable<Integer> {

    @Override
    public Integer call() throws Exception{

        String src = SourceReader.readSource("src/test/resources/inputs/Menu.txt");

        //instancias de handlers para errores en etapas de analisis
        CustomErrorHandler lexerErrorHandler = new CustomErrorHandler(ErrorType.LEXICO);
        CustomErrorHandler parserErrorHandler = new CustomErrorHandler(ErrorType.SINTACTICO);

        //instancias de generadores para bitacoras
        HTMLReportGenerator<TokenModel> tokenTable = new HTMLReportGenerator<>();
        HTMLReportGenerator<ScopeModel> scopeTable = new HTMLReportGenerator<>();
        HTMLReportGenerator<CallModel> callTable = new HTMLReportGenerator<>();

        HTMLReportGenerator<ErrorModel> lexerErrorTable = new HTMLReportGenerator<>();
        HTMLReportGenerator<ErrorModel> parserErrorTable = new HTMLReportGenerator<>();
        HTMLReportGenerator<ErrorModel> semanticErrorTable = new HTMLReportGenerator<>();


        //generador de lexer
        ShinobiScriptLexer lexer = new ShinobiScriptLexer(CharStreams.fromString(src));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        //inyeccion de error handler personalizado para lexer
        lexer.removeErrorListeners();
        lexer.addErrorListener(lexerErrorHandler);

        //llenado de tokens
        tokenStream.fill();

        //recopilacion de tokens
        Vocabulary v = lexer.getVocabulary();
        List<TokenModel> tokenList = new ArrayList<>();

        for(Token t : tokenStream.getTokens()) {
            if(t.getType() != Token.EOF) {
                tokenList.add(new TokenModel(t,v));
            }
        }

        if(lexerErrorHandler.hasErrors()) {
            System.out.println("PROCESO DETENIDO: El pergamino contiene errores LEXICOS, por favor verifique la bitacora ninja.");
            //generacion de bitacora de errores lexicos
            lexerErrorTable.generate(
                    "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\btc_err_lexicos.html",
                    "Bitacora de Errores Lexicos",
                    lexerErrorHandler.getErrorList()
            );
            return 1;
        }

        //generacion de bitacora de tokens
        tokenTable.generate(
                "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\btc_tokens.html",
                "Bitacora de Tokens",
                tokenList
        );

        //reinicio de tokenStream para parser
        tokenStream.seek(0);

        //generador de parser
        ShinobiScriptParser parser = new ShinobiScriptParser(tokenStream);

        //inyeccion de error handler personalizado para parser
        parser.removeErrorListeners();
        parser.addErrorListener(parserErrorHandler);

        ParseTree tree = parser.init();

        if(parserErrorHandler.hasErrors()) {
            System.out.println("PROCESO DETENIDO: El pergamino contiene errores SINTACTICOS, por favor verifique la bitacora ninja.");
            parserErrorTable.generate(
                    "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\btc_err_sintacticos.html",
                    "Bitacora de Errores Sintacticos",
                    parserErrorHandler.getErrorList()
            );
            return 1;
        }

        SemanticVisitor semanticVisitor = new SemanticVisitor();
        semanticVisitor.visit(tree);

        SemanticErrorHandler semanticErrorHandler = semanticVisitor.getErrorHandler();

        if(semanticErrorHandler.hasErrors()) {
            System.out.println("PROCESO DETENIDO: El pergamino contiene errores SEMANTICOS, por favor verifique la bitacora ninja.");
            semanticErrorTable.generate(
                    "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\btc_err_semanticos.html",
                    "Bitacora de Errores Semanticos",
                    semanticErrorHandler.getErrorList()
            );
            return 1;
        }

        scopeTable.generate(
                "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\btc_scopes.html",
                "Bitacora de contextos",
                semanticVisitor.getScopesReport()
        );

        callTable.generate(
                "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\btc_calls.html",
                "Bitacora de llamadas",
                semanticVisitor.getCallsReport()
        );

        ObjectCodeGenerator objCodeGenerator = new ObjectCodeGenerator();
        ThreeAddressCodeVisitor tacCode = new ThreeAddressCodeVisitor();
        tacCode.visit(tree);
        TranslationVisitor cppCode = new TranslationVisitor();
        cppCode.visit(tree);

        ObjectCodeGenerator.write(
                "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\tac_codigo.cpp",
                tacCode.getSourceCode()
        );

        ObjectCodeGenerator.write(
                "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\cpp_codigo.cpp",
                cppCode.getSourceCode()
        );

        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
