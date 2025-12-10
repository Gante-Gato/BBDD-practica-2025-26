package es.upm.etsisi.IWSIM22_02.Exporters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Clase que exporta los resultados de una consulta SQL a un archivo XML.
 */
public class XMLExporter implements DataExporter {

    /**
     * Exporta un ResultSet a un archivo XML situado dentro de la carpeta IdeaProjects.
     * Pide al usuario el nombre del archivo por consola y confirma si debe sobrescribirse.
     *
     * @param resultSet Resultados de la consulta SQL.
     * @throws Exception Errores de lectura/escritura.
     */
    @Override
    public void export(ResultSet resultSet) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter XML file name (without extension): ");
        String fileName = reader.readLine().trim();

        /**
         * Igual que en CSVExporter:
         * user.home permite obtener la carpeta personal del usuario sin
         * tener que escribir rutas absolutas dependientes de un sistema operativo concreto.
         *
         * Esto hace que el código funcione en Windows, macOS o Linux sin cambiar nada.
         */
        String userHome = System.getProperty("user.home");

        File targetFile = new File(userHome + "/IdeaProjects/" + fileName + ".xml");

        if (targetFile.exists()) {
            System.out.print("File exists. Overwrite? (yes/no): ");
            if (!reader.readLine().trim().equalsIgnoreCase("yes")) return;
        }

        writeXml(targetFile, resultSet);
    }

    /**
     * Escribe el ResultSet en formato XML utilizando un BufferedWriter.
     * BufferedWriter permite controlar la codificación (UTF-8) y mejora la eficiencia
     * al trabajar con cadenas largas.
     *
     * @param file Archivo destino.
     * @param resultSet Datos SQL a exportar.
     * @throws Exception Errores de I/O.
     */
    private void writeXml(File file, ResultSet resultSet) throws Exception {
        /**
         * ResultSetMetaData permite obtener información sobre cada columna:
         * nombre, tipo, número de columnas, etc. Esto evita hardcodear los nombres
         * y hace que el exportador funcione con cualquier tabla.
         */
        ResultSetMetaData meta = resultSet.getMetaData();
        int columnCount = meta.getColumnCount();

        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)
        )) {

            bufferedWriter.write("<rows>\n");

            while (resultSet.next()) {
                bufferedWriter.write("  <row>\n");
                for (int i = 1; i <= columnCount; i++) {
                    String col = meta.getColumnName(i);
                    Object val = resultSet.getObject(i);
                    bufferedWriter.write("    <" + col + ">" + (val == null ? "" : val) + "</" + col + ">\n");
                }
                bufferedWriter.write("  </row>\n");
            }

            bufferedWriter.write("</rows>");
            bufferedWriter.flush();
        }
    }
}
