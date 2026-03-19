package com.quantumparkour.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//----------------------------------------------------------------------------------------------------------------------
public class QuantumDatabase
{
    //------------------------------------------------------------------------------------------------------------------
    private static final String DATABASE_URL = "jdbc:sqlite:plugins/QuantumParkour/quantumparkour.db";
    private static Connection m_connection;

    //------------------------------------------------------------------------------------------------------------------
    public static void initialize()
    {
        try
        {
            m_connection = DriverManager.getConnection(DATABASE_URL);
            createTables();

            System.out.println("[QuantumParkour] Database initialized successfully.");

            String databasePath = new java.io.File(DATABASE_URL.replace("jdbc:sqlite:", "")).getAbsolutePath();
            System.out.println("[QuantumParkour] Database file located at: " + databasePath);
        }
        catch (SQLException exception)
        {
            System.err.println("[QuantumParkour] Failed to initialize database: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    //TODO: Add more stats for player information
    //------------------------------------------------------------------------------------------------------------------
    private static void createTables() {
        String createPlayersTable = "CREATE TABLE IF NOT EXISTS Players (" +
                "UUID TEXT PRIMARY KEY, " +
                "Username VARCHAR(16) NOT NULL, " +
                "Coins INT NOT NULL, " + // TODO: Replace Coins with Qwobits when implemented
                "Rank VARCHAR(16) NOT NULL" +
                ");";
        try (Statement statement = m_connection.createStatement())
        {
            statement.execute(createPlayersTable);
        }
        catch (SQLException exception)
        {
            System.err.println("[QuantumParkour] Error creating tables: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static Connection getConnection() throws SQLException {
        if (m_connection == null || m_connection.isClosed())
        {
            m_connection = DriverManager.getConnection(DATABASE_URL);
        }
        return m_connection;
    }

    //------------------------------------------------------------------------------------------------------------------
    public static void closeConnection() {
        try
        {
            if (m_connection != null && !m_connection.isClosed())
            {
                m_connection.close();
                System.out.println("[QuantumParkour] Database connection closed.");
            }
        }
        catch (SQLException exception)
        {
            System.err.println("[QuantumParkour] Error closing database connection: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}