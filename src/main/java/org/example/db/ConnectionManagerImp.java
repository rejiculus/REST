package org.example.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManagerImp implements ConnectionManager {
    private final HikariDataSource dataSource;

    /**
     * Конструктор с импортом настроек из файла
     */
    public ConnectionManagerImp(ConfigLoader configLoader) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(configLoader.getProperty("db.url"));
        config.setUsername(configLoader.getProperty("db.username"));
        config.setPassword(configLoader.getProperty("db.password"));
        config.setMaximumPoolSize(10);

        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setDriverClassName("org.postgresql.Driver");

        this.dataSource = new HikariDataSource(config);
    }

    /**
     * Конструктор с заданием настроек
     */
    public ConnectionManagerImp(String url, String username, String password) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);

        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setDriverClassName("org.postgresql.Driver");

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }

    }
}
