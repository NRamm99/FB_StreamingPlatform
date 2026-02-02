package com.streaming.fb_streamingplatform.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig() {
        Properties props = new Properties();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "Mangler db.properties i src/main/resources. " +
                                "Kopiér db.properties.example -> db.properties og udfyld."
                );
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse db.properties", e);
        }

        this.url = require(props, "db.url");
        this.user = require(props, "db.user");
        this.password = require(props, "db.password");
    }

    private String require(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("db.properties mangler: " + key);
        }
        return v.trim();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}