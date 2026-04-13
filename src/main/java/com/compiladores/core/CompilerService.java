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
            CustomErrorHandler lexerErrorHandler = new CustomErrorHandler(ErrorType.LEXICO);
            CustomErrorHandler parserErrorHandler = new CustomErrorHandler(ErrorType.SINTACTICO);

            HTMLReportGenerator<TokenModel> tokenTable = new HTMLReportGenerator<>();
            HTMLReportGenerator<ScopeModel> scopeTable = new HTMLReportGenerator<>();
            HTMLReportGenerator<CallModel> callTable = new HTMLReportGenerator<>();

            HTMLReportGenerator<ErrorModel> lexerErrorTable = new HTMLReportGenerator<>();
            HTMLReportGenerator<ErrorModel> parserErrorTable = new HTMLReportGenerator<>();
            HTMLReportGenerator<ErrorModel> semanticErrorTable = new HTMLReportGenerator<>();


            ShinobiScriptLexer lexer = new ShinobiScriptLexer(CharStreams.fromString(src));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);

            lexer.removeErrorListeners();
            lexer.addErrorListener(lexerErrorHandler);

            tokenStream.fill();

            Vocabulary v = lexer.getVocabulary();
            List<TokenModel> tokenList = new ArrayList<>();

            for (Token t : tokenStream.getTokens()) {
                if (t.getType() != Token.EOF) {
                    tokenList.add(new TokenModel(t, v));
                }
            }

            if (lexerErrorHandler.hasErrors()) {
                System.out.println("Errores léxicos detectados");

                lexerErrorTable.generate(
                        "output/btc_err_lexicos.html",
                        "Errores Léxicos",
                        lexerErrorHandler.getErrorList()
                );

                return 1;
            }

            tokenTable.generate(
                    "output/btc_tokens.html",
                    "Tokens",
                    tokenList
            );

            tokenStream.seek(0);

            ShinobiScriptParser parser = new ShinobiScriptParser(tokenStream);

            parser.removeErrorListeners();
            parser.addErrorListener(parserErrorHandler);

            ParseTree tree = parser.init();

            if (parserErrorHandler.hasErrors()) {
                System.out.println("Errores sintácticos detectados");

                parserErrorTable.generate(
                        "output/btc_err_sintacticos.html",
                        "Errores Sintácticos",
                        parserErrorHandler.getErrorList()
                );

                return 1;
            }

            SemanticVisitor semanticVisitor = new SemanticVisitor();
            semanticVisitor.visit(tree);

            SemanticErrorHandler semanticErrorHandler = semanticVisitor.getErrorHandler();

            if (semanticErrorHandler.hasErrors()) {
                System.out.println("Errores semánticos detectados");

                semanticErrorTable.generate(
                        "output/btc_err_semanticos.html",
                        "Errores Semánticos",
                        semanticErrorHandler.getErrorList()
                );

                return 1;
            }

            scopeTable.generate(
                    "output/btc_scopes.html",
                    "Scopes",
                    semanticVisitor.getScopesReport()
            );

            callTable.generate(
                    "output/btc_calls.html",
                    "Calls",
                    semanticVisitor.getCallsReport()
            );

            return 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }
}
