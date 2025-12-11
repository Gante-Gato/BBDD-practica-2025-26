package es.upm.etsisi.IWSIM22_02.Exporters;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class CSVExporter {

    private static final String CSV_PATH = "./src/main/resources/Medicine.csv";

    private final ResultSet resultSet;

    public CSVExporter(ResultSet rs) {
        this.resultSet = rs;
    }

    public void export() throws IOException, SQLException {
        File file = new File(CSV_PATH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try (
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT)
        ) {
            int columnCount = resultSet.getMetaData().getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                csvPrinter.print(resultSet.getMetaData().getColumnLabel(i));
            }
            csvPrinter.println();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    if (value instanceof java.sql.Date) {
                        value = sdf.format(value);
                    }
                    csvPrinter.print(value != null ? value : "");
                }
                csvPrinter.println();
            }

            csvPrinter.flush();
        }
    }
}