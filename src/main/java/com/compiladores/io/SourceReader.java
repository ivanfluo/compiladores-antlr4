package com.compiladores.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Componente encargado de la gestión de entrada de archivos del compilador.
 * <p>
 *     Provee de utilidades para cargar el código fuente desde el sistema de archivos
 *     y transformarlo en estructuras compatibles con el analizador de ANTLR4.
 * </p>
 * @author Daniel Aldana / DaS6T
 * @version 1.0
 */
public class SourceReader {

    /**
     * Lee el contenido integro del archivo de entrada y lo retorna como una cadena de texto.
     * <p>
     *     Este método utiliza la codificación UTF-8 por defecto para evitar errores de compatibilidad
     *     con caracteres especiales que puedan encontrarse en el código fuente.
     * </p>
     * @param filepath Ruta local o absoluta del archivo a leer (p. ej. "input/test.txt").
     * @return El contenido del archivo convertido a {@code String}.
     * @throws IOException Si el archivo no existe, no tiene permisos de lectura, o ocurre un error E/S.
     * @see java.nio.file.Files#readAllBytes(java.nio.file.Path)
     */
    public static String readSource(String filepath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(filepath));
        return new String(encoded, StandardCharsets.UTF_8);
    }
}
