package app;

import java.sql.Connection;
import java.sql.DriverManager;

public class dbConnection {

    public Connection dbLink;

    public Connection connect()
    {
        String dbName ="plannerDB";
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASSWORD");
        String url = System.getenv("DB_URL") + dbName;

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

