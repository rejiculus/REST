package org.example.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManagerImp implements ConnectionManager, Closeable {
    private HikariDataSource dataSource;

    public ConnectionManagerImp(String url, String username, String password, int maxPoolSize) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);         // URL подключения к базе данных
        config.setUsername(username);       // Имя пользователя для подключения
        config.setPassword(password);       // Пароль для подключения
        config.setMaximumPoolSize(maxPoolSize); // Максимальный размер пула соединений

        // Дополнительные настройки HikariCP можно добавить по необходимости:
        config.setConnectionTimeout(30000);  // Время ожидания подключения в миллисекундах
        config.setIdleTimeout(600000);       // Время простоя до закрытия соединения
        config.setMaxLifetime(1800000);      // Максимальная продолжительность жизни соединения
        config.setDriverClassName("org.postgresql.Driver");

        this.dataSource = new HikariDataSource(config);
    }

    /**
     * Конструктор с импортом настроек из файла
     */
    public ConnectionManagerImp() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DatabaseConfig.getDbUrl());       // URL подключения к базе данных
        config.setUsername(DatabaseConfig.getDbUsername()); // Имя пользователя для подключения
        config.setPassword(DatabaseConfig.getDbPassword()); // Пароль для подключения
        config.setMaximumPoolSize(10);                      // Максимальный размер пула соединений

        // Дополнительные настройки HikariCP можно добавить по необходимости:
        config.setConnectionTimeout(30000);  // Время ожидания подключения в миллисекундах
        config.setIdleTimeout(600000);       // Время простоя до закрытия соединения
        config.setMaxLifetime(1800000);      // Максимальная продолжительность жизни соединения
        config.setDriverClassName("org.postgresql.Driver");

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }

    }
}
