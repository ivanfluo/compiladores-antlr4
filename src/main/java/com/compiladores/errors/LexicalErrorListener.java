package com.compiladores.errors;

// [Inicio][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Implementación del listener para la captura de errores sintaácticos, se extiende de la clase de BaseErrorListener producida por ANTLR4 para interceptar los errores generados por el parse.]

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Listener personalizado que captura errores léxicos producidos durante el proceso de analisis en ANTLR.
 * @author Ivan Hernandez
 */
public class LexicalErrorListener extends BaseErrorListener {
    private final IErrorLogger logger;

    public LexicalErrorListener(IErrorLogger logger) {
        this.logger = logger;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException exception){
        logger.logLexicalError(line, charPositionInLine, msg);
    }
}
