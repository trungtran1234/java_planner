package app;

import java.sql.Connection;
import java.sql.DriverManager;

public class dbConnection {

    public Connection dbLink;

    public Connection getConnection()
    {
        String dbName ="db";
        String dbUser ="trungtran1234";

        String dbPass ="";
        String url = "jdbc:mysql://localhost/" + dbName;

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbLink = DriverManager.getConnection(url, dbUser, dbPass);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return dbLink;
    }
}


