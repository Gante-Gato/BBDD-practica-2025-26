package es.upm.etsisi.IWSIM22_02;

import java.util.List;
import java.util.Scanner;

public class Main {

    private ConfigLoader config;
    private DatabaseConnector dbConnector;
    private QueryExecutor queryExecutor;
    private CsvExporter csvExporter;
    private XmlExporter xmlExporter;

    public static void main(String[] args) {
        new Main().LanzarMenu();
    }

    private void LanzarMenu() {
        Scanner scanner = new Scanner(System.in);

        config = new ConfigLoader("config.properties");
        dbConnector = new DatabaseConnector(
                config.getDatabaseUrl(),
                config.getDatabaseUser(),
                config.getDatabasePassword()
        );
        queryExecutor = new QueryExecutor(dbConnector);
        csvExporter = new CsvExporter();
        xmlExporter = new XmlExporter();

        boolean running = true;

        do {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Test database connection");
            System.out.println("2. List tables");
            System.out.println("3. Export single table");
            System.out.println("4. Export all tables");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    handleTestConnection();
                    break;

                case "2":
                    handleListTables();
                    break;

                case "3":
                    handleExportSingleTable(scanner);
                    break;

                case "4":
                    handleExportAllTables();
                    break;

                case "0":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option");
            }

        } while (running);

        scanner.close();
    }

    private void handleTestConnection() {
        if (dbConnector.testConnection()) {
            System.out.println("Connection OK");
        } else {
            System.out.println("Connection FAILED");
        }
    }

    private void handleListTables() {
        List<String> tables = queryExecutor.getTableNames();
        listTables(tables);
    }

    private void handleExportSingleTable(Scanner sc) {
        System.out.print("Table name: ");
        String table = sc.nextLine().trim();
        exportSingleTable(table);
    }

    private void handleExportAllTables() {
        exportAllTables();
    }

    // Stubs (rellena con tu lógica)

    private void listTables(List<String> tables) {
        System.out.println("\nTables:");
        for (String t : tables) {
            System.out.println(" - " + t);
        }
    }

    private void exportSingleTable(String table) {
        System.out.println("Exporting: " + table);
        // Implementación real aquí.
    }

    private void exportAllTables() {
        System.out.println("Exporting all tables...");
        // Implementación real aquí.
    }
}

