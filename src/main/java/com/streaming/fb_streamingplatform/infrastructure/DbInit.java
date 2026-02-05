package com.streaming.fb_streamingplatform.infrastructure;

import java.sql.*;
import java.util.List;

public class DbInit {

    private static final String DB_NAME = "streaming_tjeneste_db";

    public static void init(DatabaseConfig config) {
        createDatabaseIfMissing(config);
        createTablesIfMissing(config);
        seedDefaultsIfNeeded(config);
    }

    // -------- DATABASE --------

    private static void createDatabaseIfMissing(DatabaseConfig config) {
        String serverUrl = extractServerUrl(config);

        try (Connection conn = DriverManager.getConnection(
                serverUrl,
                getUser(config),
                getPassword(config)
        );
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);

        } catch (SQLException e) {
            throw new RuntimeException("DB init failed (create database)", e);
        }
    }

    // -------- TABLES --------

    private static void createTablesIfMissing(DatabaseConfig config) {
        try (Connection conn = config.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    name VARCHAR(100) NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS movies (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(150) NOT NULL,
                    rating DECIMAL(3,1) NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS favorites (
                    user_id INT NOT NULL,
                    movie_id INT NOT NULL,
                    PRIMARY KEY (user_id, movie_id),
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
                )
            """);

        } catch (SQLException e) {
            throw new RuntimeException("DB init failed (create tables)", e);
        }
    }

    // -------- SEED DATA --------

    private static void seedDefaultsIfNeeded(DatabaseConfig config) {
        try (Connection conn = config.getConnection()) {
            conn.setAutoCommit(false);

            if (count(conn, "users") < 8) seedUsers(conn);
            if (count(conn, "movies") < 30) seedMovies(conn);
            if (count(conn, "favorites") == 0) seedFavorites(conn);

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("DB init failed (seed data)", e);
        }
    }

    private static int count(Connection conn, String table) throws SQLException {
        try (PreparedStatement ps =
                     conn.prepareStatement("SELECT COUNT(*) FROM " + table);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    // -------- USERS --------

    private static void seedUsers(Connection conn) throws SQLException {
        String sql = "INSERT IGNORE INTO users (email, name) VALUES (?, ?)";

        List<String[]> users = List.of(
                new String[]{"mads.nielsen@gmail.com", "Mads Nielsen"},
                new String[]{"filipa.jensen@hotmail.com", "Filipa Jensen"},
                new String[]{"emil.hansen@outlook.com", "Emil Hansen"},
                new String[]{"freja.larsen@gmail.com", "Freja Larsen"},
                new String[]{"oliver.pedersen@yahoo.com", "Oliver Pedersen"},
                new String[]{"ida.sorensen@gmail.com", "Ida Sørensen"},
                new String[]{"mathias.andersen@live.dk", "Mathias Andersen"},
                new String[]{"noah.kristensen@gmail.com", "Noah Kristensen"}
        );

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] u : users) {
                ps.setString(1, u[0]);
                ps.setString(2, u[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }


    // -------- MOVIES --------

    private static void seedMovies(Connection conn) throws SQLException {
        String sql = "INSERT INTO movies (title, rating) VALUES (?, ?)";

        // Ratings are realistic IMDb-like values with 1 decimal
        List<Object[]> movies = List.of(
                new Object[]{"The Matrix (1999)", 8.7},
                new Object[]{"Parasite (2019)", 8.5},
                new Object[]{"The Godfather Part II (1974)", 9.0},
                new Object[]{"Gladiator (2000)", 8.5},
                new Object[]{"Spirited Away (2001)", 8.6},
                new Object[]{"The Dark Knight (2008)", 9.0},
                new Object[]{"Forrest Gump (1994)", 8.8},
                new Object[]{"The Silence of the Lambs (1991)", 8.6},
                new Object[]{"The Lord of the Rings: The Two Towers (2002)", 8.8},
                new Object[]{"Whiplash (2014)", 8.5},
                new Object[]{"The Godfather (1972)", 9.2},
                new Object[]{"Interstellar (2014)", 8.6},
                new Object[]{"City of God (2002)", 8.6},
                new Object[]{"Saving Private Ryan (1998)", 8.6},
                new Object[]{"The Green Mile (1999)", 8.5},
                new Object[]{"Pulp Fiction (1994)", 8.9},
                new Object[]{"The Departed (2006)", 8.5},
                new Object[]{"Schindler's List (1993)", 9.0},
                new Object[]{"Fight Club (1999)", 8.8},
                new Object[]{"The Shawshank Redemption (1994)", 9.3},
                new Object[]{"Goodfellas (1990)", 8.7},
                new Object[]{"The Lord of the Rings: The Fellowship of the Ring (2001)", 8.9},
                new Object[]{"One Flew Over the Cuckoo's Nest (1975)", 8.7},
                new Object[]{"Inception (2010)", 8.8},
                new Object[]{"Seven Samurai (1954)", 8.6},
                new Object[]{"Star Wars: Episode V - The Empire Strikes Back (1980)", 8.7},
                new Object[]{"The Lord of the Rings: The Return of the King (2003)", 9.0},
                new Object[]{"The Good, the Bad and the Ugly (1966)", 8.8},
                new Object[]{"12 Angry Men (1957)", 9.0},
                new Object[]{"Django Unchained (2012)", 8.4},
                new Object[]{"The Prestige (2006)", 8.5},
                new Object[]{"Alien (1979)", 8.5},
                new Object[]{"The Lion King (1994)", 8.5},
                new Object[]{"Back to the Future (1985)", 8.5}
        );

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] m : movies) {
                ps.setString(1, (String) m[0]);
                ps.setDouble(2, (Double) m[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }


    // -------- FAVORITES --------

    private static void seedFavorites(Connection conn) throws SQLException {
        String sql = """
        INSERT IGNORE INTO favorites (user_id, movie_id)
        SELECT u.id, m.id
        FROM users u, movies m
        WHERE u.email = ? AND m.title = ?
    """;

        // Mix: some users have favorites, some have none
        // (e.g. Sofie + Mathias have no favorites)
        List<String[]> favs = List.of(
                // Mads (lots)
                new String[]{"mads.nielsen@gmail.com", "The Dark Knight (2008)"},
                new String[]{"mads.nielsen@gmail.com", "Inception (2010)"},
                new String[]{"mads.nielsen@gmail.com", "Interstellar (2014)"},

                // Emil (a couple)
                new String[]{"emil.hansen@outlook.com", "The Matrix (1999)"},
                new String[]{"emil.hansen@outlook.com", "Fight Club (1999)"},

                // Freja (classics)
                new String[]{"freja.larsen@gmail.com", "The Godfather (1972)"},
                new String[]{"freja.larsen@gmail.com", "Goodfellas (1990)"},

                // Oliver (war/drama)
                new String[]{"oliver.pedersen@yahoo.com", "Saving Private Ryan (1998)"},
                new String[]{"oliver.pedersen@yahoo.com", "Schindler's List (1993)"},

                // Ida (international)
                new String[]{"ida.sorensen@gmail.com", "Parasite (2019)"},
                new String[]{"ida.sorensen@gmail.com", "Spirited Away (2001)"},
                new String[]{"ida.sorensen@gmail.com", "City of God (2002)"},

                // Noah (a few)
                new String[]{"noah.kristensen@gmail.com", "Pulp Fiction (1994)"},
                new String[]{"noah.kristensen@gmail.com", "The Departed (2006)"}
        );

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] f : favs) {
                ps.setString(1, f[0]);
                ps.setString(2, f[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }


    // -------- HELPERS --------

    private static String extractServerUrl(DatabaseConfig config) {
        // jdbc:mysql://localhost:3306/streaming_tjeneste_db
        String fullUrl = getUrl(config);
        int slash = fullUrl.lastIndexOf('/');
        return fullUrl.substring(0, slash + 1);
    }

    // Reflection-safe access (no changes to DatabaseConfig needed)
    private static String getUrl(DatabaseConfig c) {
        try {
            var f = DatabaseConfig.class.getDeclaredField("url");
            f.setAccessible(true);
            return (String) f.get(c);
        } catch (Exception e) {
            throw new RuntimeException("Could not read db.url", e);
        }
    }

    private static String getUser(DatabaseConfig c) {
        try {
            var f = DatabaseConfig.class.getDeclaredField("user");
            f.setAccessible(true);
            return (String) f.get(c);
        } catch (Exception e) {
            throw new RuntimeException("Could not read db.user", e);
        }
    }

    private static String getPassword(DatabaseConfig c) {
        try {
            var f = DatabaseConfig.class.getDeclaredField("password");
            f.setAccessible(true);
            return (String) f.get(c);
        } catch (Exception e) {
            throw new RuntimeException("Could not read db.password", e);
        }
    }
}
