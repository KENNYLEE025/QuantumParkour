package com.quantumparkour.database;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

//----------------------------------------------------------------------------------------------------------------------
public class QuantumDatabase
{
    //------------------------------------------------------------------------------------------------------------------
    private static final String DATABASE_FILE = "plugins/QuantumParkour/quantumparkour.db";
    private static HikariDataSource m_dataSource;

    //------------------------------------------------------------------------------------------------------------------
    public static void initialize()
    {
        try
        {
            File databaseFile = new File(DATABASE_FILE);
            File parentFolder = databaseFile.getParentFile();

            if (parentFolder != null && !parentFolder.exists())
            {
                parentFolder.mkdirs();
            }

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
            hikariConfig.setPoolName("QuantumParkour-Hikari");

            hikariConfig.setMaximumPoolSize(5);
            hikariConfig.setMinimumIdle(1);
            hikariConfig.setConnectionTimeout(10000);
            hikariConfig.setIdleTimeout(60000);
            hikariConfig.setMaxLifetime(0);

            m_dataSource = new HikariDataSource(hikariConfig);
            createTables();

            System.out.println("[QuantumParkour] Database initialized successfully.");
            System.out.println("[QuantumParkour] Database file located at: " + databaseFile.getAbsolutePath());
        }
        catch (Exception exception)
        {
            System.err.println("[QuantumParkour] Failed to initialize database: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    //---------------------------------------------------------------------------------------------
    public static void close()
    {
        if (m_dataSource != null && !m_dataSource.isClosed())
        {
            m_dataSource.close();
            System.out.println("[QuantumParkour] Hikari data source closed.");
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private static void createTables()
    {
       executeStatements(
               getCreatePlayersTableQuery(),
               getCreateFriendsTableQuery(),
               getCreateFriendRequestTableQuery(),
               getCreateBlockedPlayersTableQuery()
            );
    }

    //---------------------------------------------------------------------------------------------
    private static String getCreatePlayersTableQuery()
    {
        return """
            CREATE TABLE IF NOT EXISTS Players (
                UUID TEXT PRIMARY KEY NOT NULL,
                Username VARCHAR(16) NOT NULL,
                Coins INT NOT NULL,
                Rank VARCHAR(16) NOT NULL
            );
            """;
    }

    //---------------------------------------------------------------------------------------------
    private static String getCreateFriendsTableQuery()
    {
        return """
            CREATE TABLE IF NOT EXISTS Friends (
                PlayerUUID TEXT NOT NULL,
                FriendUUID TEXT NOT NULL,
                PRIMARY KEY (PlayerUUID, FriendUUID),
                FOREIGN KEY (PlayerUUID) REFERENCES Players(UUID),
                FOREIGN KEY (FriendUUID) REFERENCES Players(UUID)
            );
            """;
    }

    //---------------------------------------------------------------------------------------------
    private static String getCreateBlockedPlayersTableQuery()
    {
        return """
            CREATE TABLE IF NOT EXISTS BlockedPlayers (
                PlayerUUID TEXT NOT NULL,
                BlockedUUID TEXT NOT NULL,
                PRIMARY KEY (PlayerUUID, BlockedUUID),
                FOREIGN KEY (PlayerUUID) REFERENCES Players(UUID),
                FOREIGN KEY (BlockedUUID) REFERENCES Players(UUID)
            );
            """;
    }

    private static String getCreateFriendRequestTableQuery()
    {
        return """
        CREATE TABLE IF NOT EXISTS FriendRequests (
            SenderUUID TEXT NOT NULL,
            TargetUUID TEXT NOT NULL,
            RequestCreatedMilliseconds INTEGER NOT NULL,
            RequestExpiredMilliseconds INTEGER NOT NULL,
            PRIMARY KEY (SenderUUID, TargetUUID),
            FOREIGN KEY (SenderUUID) REFERENCES Players(UUID),
            FOREIGN KEY (TargetUUID) REFERENCES Players(UUID)
        );
        """;
    }

    //---------------------------------------------------------------------------------------------
    private static void executeStatements(String... sqlStatements)
    {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement())
        {
            for (String sqlStatement : sqlStatements)
            {
                statement.execute(sqlStatement);
            }
        }
        catch (SQLException exception)
        {
            System.err.println("[QuantumParkour] Error executing SQL statements: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    //---------------------------------------------------------------------------------------------
    public static Connection getConnection() throws SQLException
    {
        if (m_dataSource == null)
        {
            throw new IllegalStateException("Database has not been initialized. Call QuantumDatabase.initialize() first.");
        }

        return m_dataSource.getConnection();
    }
}