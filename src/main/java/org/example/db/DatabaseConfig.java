package org.example.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    private DatabaseConfig(){}

    static {
        loadFromFile("D:\\Projects\\Java\\REST\\db.properties");
    }

    public static void loadFromFile(String src) {
        try (FileInputStream fis = new FileInputStream(src)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDbUrl() {

        return properties.getProperty("db.url");
    }

    public static void setDbUrl(String url) {
        properties.setProperty("db.url", url);
    }

    public static String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public static void setUsername(String username) {
        properties.setProperty("db.username", username);
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public static void setPassword(String password) {
        properties.setProperty("db.password", password);
    }
}
