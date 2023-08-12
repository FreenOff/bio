package ru.freen.bio.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class MySQL {
    public static HikariDataSource dataSource;

    public static void connect(String jdbcUrl, String username, String password){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        dataSource = new HikariDataSource(config);
    }

    public static void createPlayersTable() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS players (" +
                    "  nickname VARCHAR(255) NOT NULL," +
                    "  biography VARCHAR(60) NOT NULL," +
                    "  PRIMARY KEY (nickname)" +
                    ")";
            statement.executeUpdate(createTableQuery);
            System.out.println("Таблица 'players' создана успешно.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean playerExists(String nickname) {
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
    }

    public static void addPlayer(String nickname, String biography) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO players (nickname, biography) VALUES (?, ?)")) {

            preparedStatement.setString(1, nickname);
            preparedStatement.setString(2, biography);

            preparedStatement.executeUpdate();
            System.out.println("Игрок добавлен в базу данных.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayerBiography(String nickname, String newBiography) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE players SET biography = ? WHERE nickname = ?")) {

            preparedStatement.setString(1, newBiography);
            preparedStatement.setString(2, nickname);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Biography updated for player: " + nickname);
            } else {
                System.out.println("Player not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getPlayerBiography(String nickname) {
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

        return null; // Если игрок с указанным ником не найден
    }

    public static void closeConnectionPool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
