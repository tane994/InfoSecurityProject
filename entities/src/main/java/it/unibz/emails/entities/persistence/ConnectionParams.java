package it.unibz.emails.entities.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionParams {
        private final String dbms;
        private final String host;
        private final String port;
        private final String name;
        private final String username;
        private final String password;

    public ConnectionParams(String dbms, String host, String port, String name, String username, String password) {
        this.dbms = dbms;
        this.host = host;
        this.port = port;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public static ConnectionParams fromConfig() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream confFile = classLoader.getResourceAsStream("database.properties");
        Properties prop = new Properties();
        try {
            if (confFile==null) throw new IllegalStateException("Configuration file not found");

            prop.load(confFile);
            return new ConnectionParams(
                    prop.getProperty("dbms", "postgresql"),
                    prop.getProperty("host", "localhost"),
                    prop.getProperty("port", "5432"),
                    prop.getProperty("name"),
                    prop.getProperty("username"),
                    prop.getProperty("password")
            );
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Cannot load the database configuration file", e);
        }
    }

    public String dbms() {
        return dbms;
    }

    public String host() {
        return host;
    }

    public String port() {
        return port;
    }

    public String name() {
        return name;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}
