package es.upm.etsisi.IWSIM22_02.Exporters;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;

public class XMLExporter {

    private static final String XML_PATH = "./src/main/resources/medicine.xml";
    private final ResultSet resultSet;

    public XMLExporter(ResultSet rs) {
        this.resultSet = rs;
    }

    public void export() throws Exception {
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(XML_PATH);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8))
        ) {
            //El encabezado del XML. Según ChatGPT hay un XMLWriter que puede hacérlo solo
            //pero no recuerdo haberlo visto en clase así que no quería usarlo por si acaso.
            bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            //encodearlo en UTF-8 haceque las tildes y las eñe no revienten el programa
            bufferedWriter.newLine();
            bufferedWriter.write("<medicamentos>");
            bufferedWriter.newLine();

            int columnCount = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                bufferedWriter.write("  <registro>");
                bufferedWriter.newLine();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnLabel(i);
                    Object val = resultSet.getObject(i);
                    String text = val != null ? escapeXML(val.toString()) : "";
                    bufferedWriter.write("    <" + columnName + ">" + text + "</" + columnName + ">");
                    bufferedWriter.newLine();
                }
                bufferedWriter.write("  </registro>");
                bufferedWriter.newLine();
            }
            bufferedWriter.write("</medicamentos>");
            bufferedWriter.newLine();
        }
    }

    private String escapeXML(String text) { //escapa los caracteres reservados
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}