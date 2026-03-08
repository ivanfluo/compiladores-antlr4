package com.compiladores.errors;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
// [Inicio][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Implementación del listener para la captura de errores sintaácticos, se extiende de la clase de BaseErrorListener producida por ANTLR4 para interceptar los errores generados por el parse.]
/**
 * Listener personalizado que captura errores sintácticos producidos durante el proceso de parsing en ANTLR.
 * @author Ivan Hernandez
 */
public class SyntaxErrorListener extends BaseErrorListener {
    private final IErrorLogger logger;

    /**
     * Constructor base del listener.
     * @param logger Instancia del registro de errores.
     */
    public SyntaxErrorListener(IErrorLogger logger) {
        this.logger = logger;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException exception){
        logger.logSyntaxError(line, charPositionInLine, msg);
    }
}
// [Fin][8/03/2026][Ivan Hernandez][Tarjeta de Jira: https://miumg-team-compi.atlassian.net/browse/SCRUM-9][Implementación del listener para la captura de errores sintaácticos, se extiende de la clase de BaseErrorListener producida por ANTLR4 para interceptar los errores generados por el parse.]
