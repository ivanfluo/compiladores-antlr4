package com.compiladores.io;

import java.util.List;

/**
 * Interfaz que define el contrato para cualquier objeto que deba ser visualizado en
 * un reporte tabular (HTML).
 * <p>
 *     Al implementar esta interfaz, una clase permite que el {@link HTMLReportGenerator}
 *     extraiga sus datos de forma genérica para construir filas y encabezados de tabla.
 * </p>
 * @author Daniel Aldana / DaS6T
 * @version 1.0
 */
public interface IReportable {
    /**
     * Transforma los atributos del objeto en una lista de celdas para una fila de la tabla.
     * <p>
     *     El orden de los elementos en esta lista deben coincidir exactamente con el orden
     *     definido en {@link #getHeaders()}.
     * </p>
     * @return Una lista de cadenas de texto (String) que representan los valores de cada celda.
     * Ejemplo: ["variable1", "0", "1"]
     */
    List<String> toRow();

    /**
     * Define los nombres de las columnas que aparecerán en la parte superior de la tabla HTML.
     * @return Una lista de cadenas de texto (String) con los títulos de los encabezados.
     * Ejemplo: ["Lexema", "Linea", "Columna"]
     */
    List<String> getHeaders();
}
