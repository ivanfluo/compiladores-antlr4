package com.compiladores.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utilidad encargada de generar la persistencia del codigo objeto generado.
 */
public class ObjectCodeGenerator {

    /**
     * Escribe el código fuente que recibe en un archivo físico.
     * @param filename Nombre del archivo de salida (p. ej. "output.cpp")
     * @param content El código transpilado.
     * @throws IOException Si ocurre un error al crear o escribir el archvio.
     */
    public static void write(String filename, String content) throws IOException {
        File file = new File(filename);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content);
        }
    }
}
