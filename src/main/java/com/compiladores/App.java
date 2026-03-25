package com.compiladores;

import com.compiladores.handlers.CustomErrorHandler;
import com.compiladores.io.HTMLReportGenerator;
import com.compiladores.io.SourceReader;
import com.compiladores.models.ErrorModel;
import com.compiladores.models.ErrorType;
import com.compiladores.models.TokenModel;
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

        String src = SourceReader.readSource("src/test/resources/inputs/testParseError.txt");

        //instancias de handlers para errores en etapas de analisis
        CustomErrorHandler lexerErrorHandler = new CustomErrorHandler(ErrorType.LEXICO);
        CustomErrorHandler parserErrorHandler = new CustomErrorHandler(ErrorType.SINTACTICO);

        //instancias de generadores para bitacoras
        HTMLReportGenerator<TokenModel> tokenTable = new HTMLReportGenerator<>();
        HTMLReportGenerator<ErrorModel> lexerErrorTable = new HTMLReportGenerator<>();
        HTMLReportGenerator<ErrorModel> parserErrorTable = new HTMLReportGenerator<>();

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

        //generacion de bitacora de tokens
        tokenTable.generate(
                "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\btc_tokens.html",
                "Bitacora de Tokens",
                tokenList
        );

        //generacion de bitacora de errores lexicos
        lexerErrorTable.generate(
                "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\btc_err_lexicos.html",
                "Bitacora de Errores Lexicos",
                lexerErrorHandler.getErrorList()
        );

        //reinicio de tokenStream para parser
        tokenStream.seek(0);

        //generador de parser
        ShinobiScriptParser parser = new ShinobiScriptParser(tokenStream);

        //inyeccion de error handler personalizado para parser
        parser.removeErrorListeners();
        parser.addErrorListener(parserErrorHandler);

        ParseTree tree = parser.init();

        parserErrorTable.generate(
                "C:\\Programs\\IntelliJ\\compiladores-antlr4\\output\\btc_err_sintacticos.html",
                "Bitacora de Errores Sintacticos",
                parserErrorHandler.getErrorList()
        );

        if(parserErrorHandler.hasErrors()) {
            //terminar ejecucion
            return 0;
        }

        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
