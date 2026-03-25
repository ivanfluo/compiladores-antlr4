package com.compiladores.models;

import com.compiladores.io.IReportable;

import java.util.Arrays;
import java.util.List;

/**
 * Clase envoltura para errores.
 * <p>
 *     Envuelve los errores originales encontrados por el analizador
 *     en la fase que corresponde y los devuelve en forma de objetos
 *     con los cuales {@link com.compiladores.io.HTMLReportGenerator}
 *     pueda generar una tabla al implementar {@link IReportable}.
 * </p>
 */
public class ErrorModel implements IReportable {
    private final ErrorType tipo;
    private final int linea;
    private final int columna;
    private final String mensaje;

    public ErrorModel(ErrorType tipo, int linea, int columna, String mensaje) {
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
        this.mensaje = mensaje;
    }

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("Tipo", "Linea", "Columna", "Mensaje");
    }

    @Override
    public List<String> toRow() {
        return Arrays.asList(
                tipo.toString(),
                String.valueOf(linea),
                String.valueOf(columna),
                mensaje
        );
    }
}
