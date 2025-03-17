package com.quantumparkour.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class QuantumDatabase {
    private static final String DATABASE_URL = "jdbc:sqlite:plugins/QuantumParkour/quantumparkour.db";
    private static Connection connection;

    public static void initialize() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            createTables();
            System.out.println("[QuantumParkour] Database initialized successfully.");
            String databasePath = new java.io.File(DATABASE_URL.replace("jdbc:sqlite:", "")).getAbsolutePath();
            System.out.println("[QuantumParkour] Database file located at: " + databasePath);
        } catch (SQLException e) {
            System.err.println("[QuantumParkour] Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables() {
        String createPlayersTable = "CREATE TABLE IF NOT EXISTS Players (" +
                "UUID TEXT PRIMARY KEY, " +
                "Username VARCHAR(16) NOT NULL, " +
                "Coins INT NOT NULL, " + // TODO: Replace Coins with Qwobits when implemented
                "Rank VARCHAR(16) NOT NULL" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createPlayersTable);
        } catch (SQLException e) {
            System.err.println("[QuantumParkour] Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DATABASE_URL);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[QuantumParkour] Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[QuantumParkour] Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
