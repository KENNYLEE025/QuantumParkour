package com.quantumparkour.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//----------------------------------------------------------------------------------------------------------------------
public final class FriendDBManager
{
    //------------------------------------------------------------------------------------------------------------------
    private static final long FRIEND_REQUEST_DURATION_MILLISECONDS = 60_000L;

    //------------------------------------------------------------------------------------------------------------------
    private FriendDBManager()
    {
    }

    //------------------------------------------------------------------------------------------------------------------
    public static boolean sendFriendRequest(UUID senderUUID, UUID targetUUID)
    {
        if (senderUUID == null || targetUUID == null)
        {
            return false;
        }

        if (senderUUID.equals(targetUUID))
        {
            return false;
        }

        if (areFriends(senderUUID, targetUUID))
        {
            return false;
        }

        if (isBlocked(senderUUID, targetUUID) || isBlocked(targetUUID, senderUUID))
        {
            return false;
        }

        String sql = """
            INSERT OR REPLACE INTO FriendRequests
            (SenderUUID, TargetUUID, RequestCreatedMilliseconds, RequestExpiredMilliseconds)
            VALUES (?, ?, ?, ?)
            """;

        long currentTimeMilliseconds = System.currentTimeMillis();
        long expiresAtMilliseconds = currentTimeMilliseconds + FRIEND_REQUEST_DURATION_MILLISECONDS;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, senderUUID.toString());
            statement.setString(2, targetUUID.toString());
            statement.setLong(3, currentTimeMilliseconds);
            statement.setLong(4, expiresAtMilliseconds);

            return statement.executeUpdate() > 0;
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return false;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static boolean hasValidFriendRequest(UUID senderUUID, UUID targetUUID)
    {
        if (senderUUID == null || targetUUID == null)
        {
            return false;
        }

        String sql = """
            SELECT 1
            FROM FriendRequests
            WHERE SenderUUID = ? AND TargetUUID = ? AND RequestExpiredMilliseconds > ?
            LIMIT 1
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, senderUUID.toString());
            statement.setString(2, targetUUID.toString());
            statement.setLong(3, System.currentTimeMillis());

            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return false;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static boolean areFriends(UUID playerUUID, UUID targetUUID)
    {
        if (playerUUID == null || targetUUID == null)
        {
            return false;
        }

        String sql = """
            SELECT 1
            FROM Friends
            WHERE PlayerUUID = ? AND FriendUUID = ?
            LIMIT 1
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, targetUUID.toString());

            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return false;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static boolean isBlocked(UUID playerUUID, UUID targetUUID)
    {
        if (playerUUID == null || targetUUID == null)
        {
            return false;
        }

        String sql = """
            SELECT 1
            FROM BlockedPlayers
            WHERE PlayerUUID = ? AND BlockedUUID = ?
            LIMIT 1
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, targetUUID.toString());

            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return false;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static boolean acceptFriendRequest(UUID playerUUID, UUID targetUUID)
    {
        if (playerUUID == null || targetUUID == null)
        {
            return false;
        }

        if (playerUUID.equals(targetUUID))
        {
            return false;
        }

        if (isBlocked(playerUUID, targetUUID) || isBlocked(targetUUID, playerUUID))
        {
            return false;
        }

        Connection connection = null;

        try
        {
            connection = QuantumDatabase.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement deleteRequestStatement = connection.prepareStatement("""
                    DELETE FROM FriendRequests
                    WHERE SenderUUID = ? AND TargetUUID = ? AND RequestExpiredMilliseconds > ?
                    """);
                 PreparedStatement addFriendStatement = connection.prepareStatement("""
                    INSERT OR IGNORE INTO Friends (PlayerUUID, FriendUUID)
                    VALUES (?, ?)
                    """))
            {
                long currentTimeMillis = System.currentTimeMillis();

                // targetUUID sent the request to playerUUID, and playerUUID is accepting it
                deleteRequestStatement.setString(1, targetUUID.toString());
                deleteRequestStatement.setString(2, playerUUID.toString());
                deleteRequestStatement.setLong(3, currentTimeMillis);

                int deletedRows = deleteRequestStatement.executeUpdate();
                if (deletedRows == 0)
                {
                    connection.rollback();
                    return false;
                }

                addFriendStatement.setString(1, playerUUID.toString());
                addFriendStatement.setString(2, targetUUID.toString());
                addFriendStatement.executeUpdate();

                addFriendStatement.setString(1, targetUUID.toString());
                addFriendStatement.setString(2, playerUUID.toString());
                addFriendStatement.executeUpdate();

                connection.commit();
                return true;
            }
            catch (SQLException exception)
            {
                connection.rollback();
                exception.printStackTrace();
                return false;
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return false;
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.setAutoCommit(true);
                    connection.close();
                }
                catch (SQLException exception)
                {
                    exception.printStackTrace();
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static boolean rejectFriendRequest(UUID playerUUID, UUID targetUUID)
    {
        if (playerUUID == null || targetUUID == null)
        {
            return false;
        }

        String sql = """
            DELETE FROM FriendRequests
            WHERE SenderUUID = ? AND TargetUUID = ?
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            // targetUUID sent the request to playerUUID
            statement.setString(1, targetUUID.toString());
            statement.setString(2, playerUUID.toString());

            return statement.executeUpdate() > 0;
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return false;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static boolean removeFriend(UUID playerUUID, UUID targetUUID)
    {
        if (playerUUID == null || targetUUID == null)
        {
            return false;
        }

        Connection connection = null;

        try
        {
            connection = QuantumDatabase.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement("""
                    DELETE FROM Friends
                    WHERE (PlayerUUID = ? AND FriendUUID = ?)
                       OR (PlayerUUID = ? AND FriendUUID = ?)
                    """))
            {
                statement.setString(1, playerUUID.toString());
                statement.setString(2, targetUUID.toString());
                statement.setString(3, targetUUID.toString());
                statement.setString(4, playerUUID.toString());

                int affectedRows = statement.executeUpdate();
                connection.commit();
                return affectedRows > 0;
            }
            catch (SQLException exception)
            {
                connection.rollback();
                exception.printStackTrace();
                return false;
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return false;
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.setAutoCommit(true);
                    connection.close();
                }
                catch (SQLException exception)
                {
                    exception.printStackTrace();
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static boolean blockPlayer(UUID playerUUID, UUID targetUUID)
    {
        if (playerUUID == null || targetUUID == null)
        {
            return false;
        }

        if (playerUUID.equals(targetUUID))
        {
            return false;
        }

        Connection connection = null;

        try
        {
            connection = QuantumDatabase.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement insertBlockStatement = connection.prepareStatement("""
                    INSERT OR IGNORE INTO BlockedPlayers (PlayerUUID, BlockedUUID)
                    VALUES (?, ?)
                    """);
                 PreparedStatement deleteFriendsStatement = connection.prepareStatement("""
                    DELETE FROM Friends
                    WHERE (PlayerUUID = ? AND FriendUUID = ?)
                       OR (PlayerUUID = ? AND FriendUUID = ?)
                    """);
                 PreparedStatement deleteRequestsStatement = connection.prepareStatement("""
                    DELETE FROM FriendRequests
                    WHERE (SenderUUID = ? AND TargetUUID = ?)
                       OR (SenderUUID = ? AND TargetUUID = ?)
                    """))
            {
                insertBlockStatement.setString(1, playerUUID.toString());
                insertBlockStatement.setString(2, targetUUID.toString());
                insertBlockStatement.executeUpdate();

                deleteFriendsStatement.setString(1, playerUUID.toString());
                deleteFriendsStatement.setString(2, targetUUID.toString());
                deleteFriendsStatement.setString(3, targetUUID.toString());
                deleteFriendsStatement.setString(4, playerUUID.toString());
                deleteFriendsStatement.executeUpdate();

                deleteRequestsStatement.setString(1, playerUUID.toString());
                deleteRequestsStatement.setString(2, targetUUID.toString());
                deleteRequestsStatement.setString(3, targetUUID.toString());
                deleteRequestsStatement.setString(4, playerUUID.toString());
                deleteRequestsStatement.executeUpdate();

                connection.commit();
                return true;
            }
            catch (SQLException exception)
            {
                connection.rollback();
                exception.printStackTrace();
                return false;
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return false;
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.setAutoCommit(true);
                    connection.close();
                }
                catch (SQLException exception)
                {
                    exception.printStackTrace();
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static boolean unblockPlayer(UUID playerUUID, UUID targetUUID)
    {
        if (playerUUID == null || targetUUID == null)
        {
            return false;
        }

        String sql = """
            DELETE FROM BlockedPlayers
            WHERE PlayerUUID = ? AND BlockedUUID = ?
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, targetUUID.toString());

            return statement.executeUpdate() > 0;
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return false;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static int cleanupExpiredFriendRequests()
    {
        String sql = """
            DELETE FROM FriendRequests
            WHERE RequestExpiredMilliseconds <= ?
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setLong(1, System.currentTimeMillis());
            return statement.executeUpdate();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return 0;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static long getFriendRequestExpirationMillis(UUID senderUUID, UUID targetUUID)
    {
        if (senderUUID == null || targetUUID == null)
        {
            return -1L;
        }

        String sql = """
            SELECT RequestExpiredMilliseconds
            FROM FriendRequests
            WHERE SenderUUID = ? AND TargetUUID = ?
            LIMIT 1
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, senderUUID.toString());
            statement.setString(2, targetUUID.toString());

            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    return resultSet.getLong("RequestExpiredMilliseconds");
                }

                return -1L;
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            return -1L;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static List<UUID> getFriendUUIDs(UUID playerUUID)
    {
        List<UUID> friendUUIDs = new ArrayList<>();

        if (playerUUID == null)
        {
            return friendUUIDs;
        }

        String sql = """
            SELECT FriendUUID
            FROM Friends
            WHERE PlayerUUID = ?
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, playerUUID.toString());

            try (ResultSet resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    String friendUUIDString = resultSet.getString("FriendUUID");

                    try
                    {
                        friendUUIDs.add(UUID.fromString(friendUUIDString));
                    }
                    catch (IllegalArgumentException exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }

        return friendUUIDs;
    }

    //------------------------------------------------------------------------------------------------------------------
    public static List<UUID> getBlockedUUIDs(UUID playerUUID)
    {
        List<UUID> blockedUUIDs = new ArrayList<>();

        if (playerUUID == null)
        {
            return blockedUUIDs;
        }

        String sql = """
            SELECT BlockedUUID
            FROM BlockedPlayers
            WHERE PlayerUUID = ?
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, playerUUID.toString());

            try (ResultSet resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    String blockedUUIDString = resultSet.getString("BlockedUUID");

                    try
                    {
                        blockedUUIDs.add(UUID.fromString(blockedUUIDString));
                    }
                    catch (IllegalArgumentException exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }

        return blockedUUIDs;
    }

    //------------------------------------------------------------------------------------------------------------------
    public static List<UUID> getIncomingFriendRequestUUIDs(UUID playerUUID)
    {
        List<UUID> requestSenderUUIDs = new ArrayList<>();

        if (playerUUID == null)
        {
            return requestSenderUUIDs;
        }

        String sql = """
            SELECT SenderUUID
            FROM FriendRequests
            WHERE TargetUUID = ? AND RequestExpiredMilliseconds > ?
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, playerUUID.toString());
            statement.setLong(2, System.currentTimeMillis());

            try (ResultSet resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    String senderUUIDString = resultSet.getString("SenderUUID");

                    try
                    {
                        requestSenderUUIDs.add(UUID.fromString(senderUUIDString));
                    }
                    catch (IllegalArgumentException exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }

        return requestSenderUUIDs;
    }

    //------------------------------------------------------------------------------------------------------------------
    public static List<UUID> getOutgoingFriendRequestUUIDs(UUID playerUUID)
    {
        List<UUID> reqestTargetUUIDs = new ArrayList<>();

        if (playerUUID == null)
        {
            return reqestTargetUUIDs;
        }

        String sql = """
            SELECT TargetUUID
            FROM FriendRequests
            WHERE SenderUUID = ? AND RequestExpiredMilliseconds > ?
            """;

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, playerUUID.toString());
            statement.setLong(2, System.currentTimeMillis());

            try (ResultSet resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    String targetUUIDString = resultSet.getString("TargetUUID");

                    try
                    {
                        reqestTargetUUIDs.add(UUID.fromString(targetUUIDString));
                    }
                    catch (IllegalArgumentException exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }

        return reqestTargetUUIDs;
    }
}