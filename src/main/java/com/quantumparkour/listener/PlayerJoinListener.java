package com.quantumparkour.listener;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.database.QuantumDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener
{
    //---------------------------------------------------------------------------------------------
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String username = player.getName();
        syncPlayerData(uuid, username);

        QuantumParkour.getPlayerManager().add(player);
        addPlayerToNoCollisionTeam(player);
    }

    // Helper functions
    //---------------------------------------------------------------------------------------------
    private void syncPlayerData(String uuid, String username)
    {
        // TODO: FIND A FIX TO ENSURE THAT THE SQL STATEMENTS ARE EASIER TO WRITE AS WE WILL ADD NEW STATISTICS WHICH
        //       WHICH WILL BE A LOT MORE HARDER MAINTAINING THIS
        String selectQuery = "SELECT Username FROM Players WHERE UUID = ?";
        String updateQuery = "UPDATE Players SET Username = ? WHERE UUID = ?";
        String insertQuery = "INSERT INTO Players (UUID, Username, Coins, Rank) VALUES (?, ?, ? ,?)";

        try (Connection connection = QuantumDatabase.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery))
        {
            selectStatement.setString(1, uuid);

            try (ResultSet resultSet = selectStatement.executeQuery())
            {
                if (!resultSet.next())
                {
                    insertPlayer(connection, insertQuery, uuid, username);
                    return;
                }

                String storedUsername = resultSet.getString("Username");

                if (storedUsername.equals(username))    return;

                updateUsername(connection, updateQuery, uuid, username);
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
    }

    //---------------------------------------------------------------------------------------------
    private void insertPlayer(Connection connection, String query, String uuid, String username) throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, uuid);
            statement.setString(2, username);
            statement.setInt(3, 0);
            statement.setString(4, "Default");
            statement.executeUpdate();
        }
    }

    //---------------------------------------------------------------------------------------------
    private void updateUsername(Connection connection, String query, String uuid, String username)  throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, uuid);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    //---------------------------------------------------------------------------------------------
    private void addPlayerToNoCollisionTeam(Player player)
    {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("noCollision");

        if (team == null) {
            team = scoreboard.registerNewTeam("noCollision");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }

        team.addEntry(player.getName());
    }
}