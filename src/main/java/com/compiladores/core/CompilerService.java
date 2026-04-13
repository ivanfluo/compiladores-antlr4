package com.compiladores.core;

import com.compiladores.ShinobiScriptLexer;
import com.compiladores.ShinobiScriptParser;
import com.compiladores.handlers.CustomErrorHandler;
import com.compiladores.io.HTMLReportGenerator;
import com.compiladores.models.*;
import com.compiladores.semantics.SemanticVisitor;
import com.compiladores.semantics.handlers.SemanticErrorHandler;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class CompilerService {
    public static int analyze(String src) {
        try {
            String outputDir = System.getProperty("user.dir") + "/output/";
            new java.io.File(outputDir).mkdirs();

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

            for (Token t : tokenStream.getTokens()) {
                if (t.getType() != Token.EOF) {
                    tokenList.add(new TokenModel(t, v));
                }
            }

            if (lexerErrorHandler.hasErrors()) {
                System.out.println("PROCESO DETENIDO: El pergamino contiene errores LEXICOS, por favor verifique la bitacora ninja.");
                //generacion de bitacora de errores lexicos
                lexerErrorTable.generate(
                        outputDir + "btc_err_lexicos.html",
                        "Bitacora de Errores Lexicos",
                        lexerErrorHandler.getErrorList()
                );
                return 1;
            }

            //generacion de bitacora de tokens
            tokenTable.generate(
                    outputDir + "btc_tokens.html",
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

            if (parserErrorHandler.hasErrors()) {
                System.out.println("PROCESO DETENIDO: El pergamino contiene errores SINTACTICOS, por favor verifique la bitacora ninja.");
                parserErrorTable.generate(
                        outputDir + "btc_err_sintacticos.html",
                        "Bitacora de Errores Sintacticos",
                        parserErrorHandler.getErrorList()
                );
                return 1;
            }

            SemanticVisitor semanticVisitor = new SemanticVisitor();
            semanticVisitor.visit(tree);

            SemanticErrorHandler semanticErrorHandler = semanticVisitor.getErrorHandler();

            if (semanticErrorHandler.hasErrors()) {
                System.out.println("PROCESO DETENIDO: El pergamino contiene errores SEMANTICOS, por favor verifique la bitacora ninja.");
                semanticErrorTable.generate(
                        outputDir + "btc_err_semanticos.html",
                        "Bitacora de Errores Semanticos",
                        semanticErrorHandler.getErrorList()
                );
                return 1;
            }

            scopeTable.generate(
                    outputDir + "btc_scopes.html",
                    "Bitacora de contextos",
                    semanticVisitor.getScopesReport()
            );

            callTable.generate(
                    outputDir + "btc_calls.html",
                    "Bitacora de llamadas",
                    semanticVisitor.getCallsReport()
            );

            return 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }
}
