package com.quantumparkour.player;

import com.quantumparkour.level.Level;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerStats {

    private final String uuid;
    private int balance = 0;
    private Level currentLevel = null;

    private boolean isPrac;
    private Location pracOrigin;
    private Location pracCheckpoint;

    PlayerStats(Player player) {
        this.uuid = player.getUniqueId().toString();
        //get stats from db
    }

    public String getUuid() {
        return uuid;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

}
