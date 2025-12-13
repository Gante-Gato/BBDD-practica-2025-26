package es.upm.etsisi.IWSIM22_02;

import es.upm.etsisi.IWSIM22_02.Connection.DBConnector;
import es.upm.etsisi.IWSIM22_02.Exporters.CSVExporter;
import es.upm.etsisi.IWSIM22_02.Exporters.XMLExporter;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private final String VALID_FORMATS = "XML, CSV";
    private final String MAKE_CONNECTION = "You are not connected to any database. Please make the connection first";
    private final String MAKE_QUERY_FIRST = "Please define the patient filter in Option 2 first.";
    private final String SHUTDOWN = "Program shutting down";
    private final String FAILED_CONNECTION = "There was an error connecting to the database.";
    private final String SUCCESFUL_CONNECTION = "Connection established successfully.";
    private static final String INVALID_INPUT_YES_NO = "Invalid input. Please answer 'y' (yes) or 'n' (no).";

    // Variable para mantener el filtro entre la opción 2 y la 3
    private String currentPatientFilter = null;
    private DBConnector dbConnector = null;

    public static void main(String[] args) {
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
                                2. Define patient filter
                                3. Export table (using filter)
                                0. Exit
                                Choose option:\s""");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    String[] connectionParams = getParameters();
                    dbConnector = new DBConnector(connectionParams[0], connectionParams[1], connectionParams[2]);
                    dbConnector.connect();
                    connected = dbConnector.isConnected();
                    if (!connected) {
                        System.out.println(FAILED_CONNECTION);
                    } else {
                        System.out.println(SUCCESFUL_CONNECTION);
                    }
                    break;

                case "2":
                    if (connected){
                        System.out.print("Enter Patient Name to filter by: ");
                        currentPatientFilter = scanner.nextLine().trim();
                        System.out.println("Filter set to: " + currentPatientFilter);
                    } else {
                        System.out.println(MAKE_CONNECTION);
                    }
                    break;

                case "3":
                    if (connected) {
                        if (currentPatientFilter == null || currentPatientFilter.isEmpty()) {
                            System.out.println(MAKE_QUERY_FIRST);
                        } else {
                            String format = decideFormat();
                            if(checkFileOverwrite(format)) {
                                exportTable(format);
                            }
                        }
                    } else {
                        System.out.println(MAKE_CONNECTION);
                    }
                    break;

                case "0":
                    if (dbConnector != null) dbConnector.close();
                    System.out.println(SHUTDOWN);
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option");
            }

        } while (running);

        scanner.close();
    }

    public static String[] getParameters() {
        Scanner scanner = new Scanner(System.in);
        boolean answered = false;
        String[] connectionParameters = new String[3];
        // Asegúrate de que esta URL coincida con tu configuración local
        connectionParameters[0] = "jdbc:mysql://localhost:3306/hospital_management_system";
        connectionParameters[1] = "root"; // Usamos el usuario que creamos antes
        connectionParameters[2] = "";

        String[] paramNames = {"Database URL", "username", "password"};

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
        System.out.println("Exporting filtered data to: " + format.toUpperCase());

        Connection conn = dbConnector.getConnection();

        // Esto evita inyecciones SQL
        String query = "SELECT * FROM vista_medicamentos_prescritos WHERE Nombre_Paciente = ?";

        // Try-with-resources asegura que el ResultSet y Statement se cierren
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {


            pstmt.setString(1, currentPatientFilter);

            try (ResultSet resultSet = pstmt.executeQuery()) {

                // Verificamos si hay datos antes de exportar
                if (!resultSet.isBeforeFirst()) {
                    System.out.println("No records found for patient: " + currentPatientFilter);
                    return;
                }

                if (format.equalsIgnoreCase("csv")) {
                    CSVExporter csvExporter = new CSVExporter(resultSet);
                    csvExporter.export();
                    System.out.println("CSV Export successful.");
                } else {
                    XMLExporter xmlExporter = new XMLExporter(resultSet);
                    xmlExporter.export();
                    System.out.println("XML Export successful.");
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("General Error: " + e.getMessage());
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

    public static boolean checkFileOverwrite(String format) {
        String path = "./src/main/resources/Medicine." + format;
        File file = new File(path);

        // Creamos el directorio si no existe para evitar FileNotFoundException
        file.getParentFile().mkdirs();

        if (file.exists()) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("File already exists. Do you want to overwrite it? (y/n): ");
                String answer = scanner.nextLine().trim().toLowerCase();
                if (answer.equals("y")) return true;
                if (answer.equals("n")) return false;
                System.out.println(INVALID_INPUT_YES_NO);
            }
        } else {
            return true;
        }
    }
}