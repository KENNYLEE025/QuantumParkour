package com.quantumparkour.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.quantumparkour.QuantumParkour;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Location location = QuantumParkour.getPracManager().getPracLocation(event.getPlayer());
        if (location != null) {
            event.getPlayer().teleport(location);
            QuantumParkour.getPracManager().removePracLocation(event.getPlayer());
        }
    }
}
