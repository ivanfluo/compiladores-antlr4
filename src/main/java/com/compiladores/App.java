package com.compiladores;

import com.compiladores.errors.HtmlErrorLogger;
import com.compiladores.errors.IErrorLogger;
import com.compiladores.errors.LexicalErrorListener;
import com.compiladores.errors.SyntaxErrorListener;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import picocli.CommandLine;

import java.util.Scanner;
import java.util.concurrent.Callable;

@CommandLine.Command(name="ArrayIntCommand", mixinStandardHelpOptions = true, version = "0.0.1", description = "Ultra basic ANTLR4 analizer")
public class App implements Callable<Integer> {

    @Override
    public Integer call() throws Exception{
        String input = "";
        Scanner scanner = new Scanner(System.in);

        IErrorLogger logger = new HtmlErrorLogger();

        do {
            System.out.println("Ingrese una cadena > ");
            input = scanner.nextLine();

            if(input.equals("exit")) continue;

            ArrayIntLexer lexer = new ArrayIntLexer(CharStreams.fromString(input));

            lexer.removeErrorListeners();
            lexer.addErrorListener(new LexicalErrorListener(logger));

            CommonTokenStream tokenStream = new CommonTokenStream(lexer);

            ArrayIntParser parser = new ArrayIntParser(tokenStream);

            ParseTree tree = parser.init();

            System.out.println(tree.toStringTree(parser));

            logger.generateReport("errores.html");

        }while (!input.equals("exit"));
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
