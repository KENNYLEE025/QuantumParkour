package com.quantumparkour.player;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerManager {

    private final HashMap<String, PlayerStats> playerStatsList = new HashMap<>();

    public void add(Player player) {
        if (playerStatsList.get(player.getUniqueId().toString()) == null) {
            playerStatsList.put(player.getUniqueId().toString(), new PlayerStats(player));
        }
    }

    public void remove(Player player) {
        //save stats to db
        playerStatsList.remove(player.getUniqueId().toString());
    }

    public PlayerStats get(Player player) {
        return playerStatsList.get(player.getUniqueId().toString());
    }

    public PlayerStats get(String uuid) {
        return playerStatsList.get(uuid);
    }

}
