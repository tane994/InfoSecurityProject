package main.java.servlet;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbParams implements Serializable {
    static final String USER = "postgres";
    static final String PWD = "postgres";
    static final String DRIVER_CLASS = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/securityProject";

    public static Connection init() {
        try {
            Class.forName(DbParams.DRIVER_CLASS);

            Properties connectionProps = new Properties();
            connectionProps.put("user", DbParams.USER);
            connectionProps.put("password", DbParams.PWD);

            return DriverManager.getConnection(DbParams.DB_URL, connectionProps);

            //System.out.println("User \"" + USER + "\" connected to database.");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}