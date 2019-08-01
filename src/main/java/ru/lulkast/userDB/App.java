package ru.lulkast.userDB;


import org.lulkast.di.annotations.Component;
import org.lulkast.di.annotations.Inject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class App implements AppInterface {

    private static final String url = "jdbc:postgresql://localhost:5432/postgres";
    private static final String user = "postgres";
    private static final String password = "0000";

    @Override
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    @Override
    @Inject
    public void createDbUserTable() throws SQLException {
        Connection dbConnection = null;
        Statement statement = null;

        String createTableSQL = "CREATE TABLE IF NOT EXISTS DBUSER("
                + "USER_UUID uuid NOT NULL, "
                + "USERNAME VARCHAR (20) NOT NULL UNIQUE, "
                + "USER_PASSWORD VARCHAR (10) NOT NULL "
                + ")";

        try {
            App app = new App();
            dbConnection = app.connect();
            statement = dbConnection.createStatement();
            statement.execute(createTableSQL);
            System.out.println("Table \"dbuser\" is created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }
}
