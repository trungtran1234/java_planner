package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {

    private static final String JDBC_URL = "jdbc:sqlite:app/database.db";
    public Connection connection;

    public Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(JDBC_URL); // Initialize the class-level connection field

            System.out.println("Connected to the SQLite database.");

            // You can perform database operations here

        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

}