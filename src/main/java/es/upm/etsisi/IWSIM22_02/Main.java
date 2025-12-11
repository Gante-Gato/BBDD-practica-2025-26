package es.upm.etsisi.IWSIM22_02;

import es.upm.etsisi.IWSIM22_02.Connection.DBConnector;
import es.upm.etsisi.IWSIM22_02.Exporters.CSVExporter;
import es.upm.etsisi.IWSIM22_02.Exporters.XMLExporter;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private final String VALID_FORMATS = "XML, CSV";
    private final String MAKE_CONNECTION = "You are not connected to any database. Please make the connection first";
    private final String SHUTDOWN = "Program shutting down";
    private final String FAILED_CONNECTION = "There was an error connecting to the database.";
    private static final String INVALID_INPUT_YES_NO = "Invalid input. Please answer 'y' (yes) or 'n' (no).";

    static void main(String[] args) {
        new Main().LanzarMenu();
    }

    private void LanzarMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        boolean connected = false;

        do {
            System.out.print("""
                                \n--- MENU ---
                                1. Make database connection
                                2. Make user medication query
                                3. Export table
                                0. Exit
                                Choose option:\s""");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    String[] connectionParams = new String[3];
                    connectionParams = getParameters();
                    DBConnector dbConnector = new DBConnector(connectionParams[0], connectionParams[1], connectionParams[2]);
                    dbConnector.connect();
                    connected = dbConnector.isConnected();
                    if (!connected) {
                        System.out.println(FAILED_CONNECTION);
                    }
                    break;

                case "2":
                    if (connected){

                    } else {
                        System.out.println(MAKE_CONNECTION);
                    }
                    break;

                case "3":
                    if (connected) {
                        String format = decideFormat();
                        if(checkFileOverwrite(format)) {
                            exportTable(format);
                        }
                    } else {
                        System.out.println(MAKE_CONNECTION);
                    }
                    break;

                case "0":
                    System.out.println(SHUTDOWN);
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option");
            }

        } while (running);

        scanner.close();
    }


    /**
     * Le da al usuario la oportunidad de modificar los parámetros de conexión
     * de la BBDD. Los defaults son los que aparecen predefinidos y son los que se usan,
     * pero no era complicado añadir la opción de modificación por si acaso hubiese errores con ellos.
     */
    public static String[] getParameters() {
        Scanner scanner = new Scanner(System.in);
        boolean answered = false;
        String[] connectionParameters = new String[3];
        connectionParameters[0] = "jdbc:mysql://localhost:3306/hospital_management_system";
        connectionParameters[1] = "root";
        connectionParameters[2] = "";

        String[] paramNames = new String[3];
        paramNames[0] = "Database URL";
        paramNames[1] = "username";
        paramNames[2] = "password";

        while (!answered) {
            System.out.print("Do you want to use the default connection? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("y")) {
                answered = true;
            } else if (answer.equals("n")) {
                for (int i = 0; i < connectionParameters.length; i++) {
                    System.out.print("Enter " + (paramNames[i]) + ": ");
                    connectionParameters[i] = scanner.nextLine().trim();
                }
                answered = true;
            } else {
                System.out.println(INVALID_INPUT_YES_NO);
            }
        }

        return connectionParameters;
    }



    private void exportTable(String format) {
        System.out.println("Exporting: " + format);
        //TODO diferenciar y añadir ResultSet
        ResultSet resultSetFalsoParaQueCompile = null;


        if (format.equalsIgnoreCase("csv")) {
            CSVExporter csvExporter = new CSVExporter(resultSetFalsoParaQueCompile);
            try {
                csvExporter.export();
            } catch (IOException | SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            XMLExporter xmlExporter = new XMLExporter(resultSetFalsoParaQueCompile);
            try {
                xmlExporter.export();
            } catch (Exception e) {
                //Se ha quedado como Exception genérico porque si intentaba usar algún set de excepciones
                //concreto siempre me daba un error de compilación.
                System.out.println(e.getMessage());
            }
        }
    }

    private String decideFormat() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("In which format? (csv/xml): ");
            String format = scanner.nextLine().trim().toLowerCase();

            if (format.equals("csv") || format.equals("xml")) {
                return format;
            } else {
                System.out.println("Invalid format, please use " + VALID_FORMATS);
            }
        }
    }

    /**
     * Comprueba si el archivo .CSV o .XML ya existe antes de sobreescribirlo. Si existe, le pregunta al usuario
     * si lo quiere sobreescribir.
     * @param format el formato del archivo que busca. Ambos se generan en el mismo sitio, pero solo checkea el que tenga el formato correspondiente
     * @return variable booleana respecto a que se ha permitido escritura.
     */
    public static boolean checkFileOverwrite(String format) {
        String path = "./src/main/resources/Medicine." + format;
        File file = new File(path);
        Scanner scanner = new Scanner(System.in);

        if (file.exists()) {
            while (true) {
                System.out.print("File already exists. Do you want to overwrite it? (y/n): ");
                String answer = scanner.nextLine().trim().toLowerCase();
                if (answer.equals("y")) {
                    return true;
                } else if (answer.equals("n")) {
                    return false;
                } else {
                    System.out.println(INVALID_INPUT_YES_NO);
                }
            }
        } else {
            return true;
        }
    }
}