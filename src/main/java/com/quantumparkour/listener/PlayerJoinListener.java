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

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String username = player.getName();

        // Add or update player in the database
        try (Connection connection = QuantumDatabase.getConnection()) {
            // Check if the player already exists in the database
            String query = "SELECT * FROM Players WHERE UUID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Player exists, update the username if it has changed
                        if (!resultSet.getString("Username").equals(username)) {
                            String updateQuery = "UPDATE Players SET Username = ? WHERE UUID = ?";
                            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                                updateStatement.setString(1, username);
                                updateStatement.setString(2, uuid);
                                updateStatement.executeUpdate();
                            }
                        }
                    } else {
                        // Player does not exist, insert them into the database
                        String insertQuery = "INSERT INTO Players (UUID, Username, Coins, Rank) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                            insertStatement.setString(1, uuid);
                            insertStatement.setString(2, username);
                            insertStatement.setInt(3, 0); // Default Coins value
                            insertStatement.setString(4, "Default"); // Default Rank value
                            insertStatement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Existing functionality
        QuantumParkour.getPlayerManager().add(player);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("noCollision");

        if (team == null) {
            team = scoreboard.registerNewTeam("noCollision");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }

        team.addEntry(player.getName());
    }
}