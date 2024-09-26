package org.example.db;

import org.example.entity.exception.NullParamException;
import org.example.repository.exception.DataBaseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Load db's properties form file in resources folder.
 */
public class ConfigLoader {
    private static final Properties properties = new Properties();

    public ConfigLoader(String path) {
        if (path == null)
            throw new NullParamException();

        try (InputStream inputStream = ConfigLoader.class.getResourceAsStream(path)) {
            if (inputStream == null)
                throw new DataBaseException(String.format("Fail to load file: %s", path));

            properties.load(inputStream);
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
