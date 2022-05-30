package it.unibz.emails.server.persistence;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private final Properties properties;

    public PropertiesReader() {
        this.properties = new Properties();
        InputStream confFile = ClassLoader.getSystemResourceAsStream("database.properties");

        if (confFile == null)
            throw new IllegalArgumentException("File does not exist");
    }

    public Properties getProperties() {
        return properties;
    }
}
