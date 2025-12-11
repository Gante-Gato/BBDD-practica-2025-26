package es.upm.etsisi.IWSIM22_02.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos.
 */
public class DBConnector {

    private String url;
    private String user;
    private String password;
    private Connection connection;
    private boolean connected;

    /**
     * Inicializa con los parámetros de conexión.
     */
    public DBConnector(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.connected = false;
        this.connection = null;
    }

    /**
     * Intenta establecer la conexión a la base de datos.
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
            e.printStackTrace();
        }
    }

    /**
     * Devuelve true si la conexión fue exitosa.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Devuelve el objeto Connection si está conectado.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Cierra la conexión si está abierta.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.getMessage();
            } finally {
                connected = false;
            }
        }
    }
}

