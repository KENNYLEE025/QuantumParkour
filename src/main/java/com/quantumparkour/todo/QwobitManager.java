package com.quantumparkour.todo;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QwobitManager {
    private final Map<UUID, Integer> qwobitBalances = new HashMap<>();

    public int getQwobits(Player player) {
        return qwobitBalances.getOrDefault(player.getUniqueId(), 0);
    }

    public void addQwobits(Player player, int amount) {
        int currentBalance = getQwobits(player);
        qwobitBalances.put(player.getUniqueId(), currentBalance + amount);
    }

    public void reduceQwobits(Player player, int amount) {
        int currentBalance = getQwobits(player);
        if (currentBalance >= amount) {
            qwobitBalances.put(player.getUniqueId(), currentBalance - amount);
        } else {
            // Handle case where player does not have enough Qwobits
            player.sendMessage("You do not have enough Qwobits.");
        }
    }

    public void setQwobits(Player player, int amount) {
        qwobitBalances.put(player.getUniqueId(), amount);
    }
}