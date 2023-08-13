package ru.freen.bio.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySQL {
    public static HikariDataSource dataSource;
    public static final ExecutorService executorService = Executors.newFixedThreadPool(2);


    public static void connect(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        dataSource = new HikariDataSource(config);
    }

    public static CompletableFuture<Void> createPlayersTable() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                String createTableQuery = "CREATE TABLE IF NOT EXISTS players (" +
                        "  nickname VARCHAR(255) NOT NULL," +
                        "  biography VARCHAR(60) NOT NULL," +
                        "  PRIMARY KEY (nickname)" +
                        ")";
                statement.executeUpdate(createTableQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        },executorService);
    }

    public static CompletableFuture<Boolean> playerExists(String nickname) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "SELECT nickname FROM players WHERE nickname = ?")) {

                preparedStatement.setString(1, nickname);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }, executorService);
    }

    public static CompletableFuture<Void> addPlayer(String nickname, String biography) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "INSERT INTO players (nickname, biography) VALUES (?, ?)")) {

                preparedStatement.setString(1, nickname);
                preparedStatement.setString(2, biography);

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }, executorService);
    }

    public static CompletableFuture<Void> updatePlayerBiography(String nickname, String newBiography) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "UPDATE players SET biography = ? WHERE nickname = ?")) {

                preparedStatement.setString(1, newBiography);
                preparedStatement.setString(2, nickname);

                int rowsUpdated = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        },executorService);
    }

    public static CompletableFuture<String> getPlayerBiography(String nickname) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "SELECT biography FROM players WHERE nickname = ?")) {

                preparedStatement.setString(1, nickname);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("biography");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        },executorService);
    }

    public static void closeConnectionPool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
