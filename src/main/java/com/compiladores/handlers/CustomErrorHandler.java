package com.compiladores.handlers;

import com.compiladores.models.ErrorModel;
import com.compiladores.models.ErrorType;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Manejador de errores personalizado para llenado de bitacoras.
 * <p>
 *     Manejador de errores que extiende del {@link BaseErrorListener} en ANTLR v4
 *     el cual reune todos los errores que se generen en determinada fase de compilación
 *     dada por las categorias contenidas en {@link ErrorType} generando objetos del tipo
 *     {@link ErrorModel}, los cuales luego puedan ser manipulados por el {@link com.compiladores.io.HTMLReportGenerator}
 *     para generación de su correspondiente bitacora.
 * </p>
 * @author Daniel Aldana / DaS6T
 * @version 1.0
 */
public class CustomErrorHandler extends BaseErrorListener {
    private final List<ErrorModel> errorList = new ArrayList<>();
    private final ErrorType defaultErrorType;

    public CustomErrorHandler(ErrorType defaultErrorType) {
        this.defaultErrorType = defaultErrorType;
    }

    /**
     * Método interceptor invocado automaticamente por ANTLR cuando ocurre un error.
     * <p>
     *     Captura los detalles del error, como la ubicación exacta (linea y columna)
     *     y el mensaje descriptivo, para almacenarlos en bitacora interna.
     * </p>
     * @param recognizer El analizador (lexer o parser) que emitió el error.
     * @param offendingSymbol El simbolo que causó la infracción.
     * @param line Número de línea donde se origino el error.
     * @param charPositionInLine Posición del caracter dentro de la linea.
     * @param msg Mensaje de error proporcionado por el motor de ANTLR v4.
     * @param e Excepcion de reconocimiento asociada.
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        errorList.add(new ErrorModel(defaultErrorType, line, charPositionInLine, msg));
    }

    /**
     * Método para recuperar la lista de errores acumulados durante el proceso de análisis.
     * @return una {@link List} de objetos {@link ErrorModel} listos para ser procesados por el
     * generador de reportes.
     */
    public List<ErrorModel> getErrorList() {
        return errorList;
    }

    /**
     * Verifica si se han detectado errores en la fase actual.
     * @return {@code true} si la lista de errores no esta vacia.
     */
    public boolean hasErrors() {
        return !errorList.isEmpty();
    }
}
