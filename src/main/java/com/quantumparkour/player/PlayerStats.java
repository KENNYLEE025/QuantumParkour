package com.quantumparkour.player;

import org.bukkit.entity.Player;

public class PlayerStats {

    private final String uuid;
    private int balance;
    //private Level currentLevel;

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
