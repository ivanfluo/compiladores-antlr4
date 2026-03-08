package com.compiladores.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Generador universal de reporteria en formato HTML.
 * <p>
 *     Clase generadora de reportes en formato HTML la cual trabaja con objetos
 *     genericos que extienden de la interfaz {@link IReportable} con lo cual
 *     permite la creacion de modelos que luego se convierten en los datos que
 *     tabula.
 * </p>
 * @param <T> El tipo de objeto a tabular. Debe implementar {@link IReportable} para proporcionar los dato de las filas y encabezados.
 * @author Daniel Aldana / DaS6T
 * @version 1.0
 */
public class HTMLReportGenerator<T extends IReportable>{

    /**
     * Procesa un set de datos y construye la tabla.
     * Requiere de modelos de datos personalizados que implementen {@link IReportable}
     * para llamar a {@code toRow()} en cada objeto, independientemente si es un token,
     * un error, o un simbolo.
     * @param filename Ruta del archivo de salida.
     * @param title Encabezado del documento HTML.
     * @param data Coleccion de elementos que seran transformados en filas.
     */
    public void generate(String filename, String title, List<T> data) {
        if(data.isEmpty()) return;

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            //Creación de la tabla
            bw.write("<html><head><title>" + title + "</title>");
            bw.write("<style>table { border-collapse: collapse; width: 100%; }" +
                    "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" +
                    "th { background-color: #4CAF50; color: white;}</style></head><body>");
            bw.write("<h1>" + title + "</h1><table><tr>");

            //Escribe el encabezado de la tabla (tomado del primer elemento)
            for(String header : data.get(0).getHeaders()) {
                bw.write("<th>" + header + "</th>");
            }
            bw.write("</tr>");

            //Escribe las filas en data
            for(T item : data) {
                bw.write("<tr>");
                for(String cell : item.toRow()) {
                    bw.write("<td>" + (cell != null ? cell : "") + "</td>");
                }
                bw.write("</tr>");
            }

            bw.write("</table></body></html>");
        }catch (IOException e) {
            System.out.println("Error al escribir el reporte: " + e.getMessage());
        }
    }
}
