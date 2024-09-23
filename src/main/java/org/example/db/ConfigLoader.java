package org.example.db;

import org.example.entity.exception.NullParamException;
import org.example.repository.exception.DataBaseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    public ConfigLoader(String path) {
        if (path == null)
            throw new NullParamException();

        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public long getLongProperty(String key) {
        return Long.parseLong(properties.getProperty(key));
    }
}
