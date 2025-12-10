package es.upm.etsisi.IWSIM22_02.Exporters;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

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
 * Clase que exporta los resultados de una consulta SQL a un archivo CSV.
 * Usa Apache Commons CSV para simplificar la escritura.
 */
public class CSVExporter implements DataExporter {

    /**
     * Exporta el ResultSet a un archivo CSV dentro de la carpeta IdeaProjects del usuario.
     * Pide el nombre del archivo por consola y confirma sobreescritura.
     *
     * @param resultSet Resultados de una consulta SQL.
     * @throws Exception Cualquier error de lectura/escritura.
     */
    @Override
    public void export(ResultSet resultSet) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        //me comentó un compañero que FileReader tiene problemas a la hora de manejar
        //caracteres con tilde. Me dijo que inputStream era menos finnicky, y luego el buffer
        //viene de que añadir un búfer ayuda a que computar tanto I/O sea más fácil para la máquina.

        System.out.print("Enter the name of the file you want to write to:\n");
        String fileName = bufferedReader.readLine().trim();

        /**
         * user.home es una propiedad del sistema que Java rellena automáticamente según
         * el sistema operativo. Esto permite construir rutas relativas sin
         * hardcodear C:/Users/... o /home/..., que no funciona en otros OSes
         *
         */

        //OSX? MacOS? Lo he visto escrito de las dos maneras, si está mal escrito por favor no me fusilen

        String userHome = System.getProperty("user.home");
        File targetFile = new File(userHome + "/IdeaProjects/" + fileName + ".csv");

        if (targetFile.exists()) {
            System.out.print("File exists. Overwrite? (yes/no): ");
            if (!bufferedReader.readLine().trim().equalsIgnoreCase("yes")) return;
        }

        writeCsv(targetFile, resultSet);
    }

    /**
     * Escribe el contenido del ResultSet dentro del archivo CSV usando un BufferedWriter.
     * BufferedWriter es más eficiente para textos largos y permite controlar la codificación.
     *
     * @param file El .csv al que se escribe o sobreescribe.
     * @param resultSet Los datos de la query de SQL que tiene que exportar.
     * @throws Exception Errores de I/O.
     */
    private void writeCsv(File file, ResultSet resultSet) throws Exception {
        /**
         * ResultSetMetaData permite obtener información de las columnas
         * (nombres, cantidad, etc.) en tiempo de ejecución. Así el código
         * no depende de conocer la estructura de la tabla por adelantado.
         */
        ResultSetMetaData meta = resultSet.getMetaData();
        int columnCount = meta.getColumnCount();

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)
        );
             /**
              * CSVPrinter es una utilidad de Apache Commons CSV que evita tener que
              * escribir las comas a mano o escapar caracteres.
              */

             CSVPrinter printer = new CSVPrinter(bw, CSVFormat.DEFAULT)) {

            String[] headers = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) headers[i - 1] = meta.getColumnName(i);
            printer.printRecord((Object[]) headers);

            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) row[i - 1] = resultSet.getObject(i);
                printer.printRecord(row);
            }
        }
    }
}
