package com.quantumparkour.command.commands;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.database.QuantumDatabase;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QDebugCommand implements QuantumCommand {

    @Override
    public String getName() {
        return "qdebug";
    }

    @Override
    public String getDescription() {
        return "Debug the database and player information.";
    }

    @Override
    public String getUsage() {
        return "/qdebug <player>";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /qdebug <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        System.out.println("[QuantumParkour] QDebug: " + target);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return;
        }

        String uuid = target.getUniqueId().toString();
        try (Connection connection = QuantumDatabase.getConnection()) {
            String query = "SELECT * FROM Players WHERE UUID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String username = resultSet.getString("Username");
                        int coins = resultSet.getInt("Coins");
                        String rank = resultSet.getString("Rank");
                        sender.sendMessage("Player Info:");
                        sender.sendMessage("UUID: " + uuid);
                        sender.sendMessage("Username: " + username);
                        sender.sendMessage("Coins: " + coins);
                        sender.sendMessage("Rank: " + rank);
                    } else {
                        sender.sendMessage("Player not found in the database.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("An error occurred while accessing the database.");
        }
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }
}