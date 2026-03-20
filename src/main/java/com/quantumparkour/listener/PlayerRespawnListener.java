package com.quantumparkour.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.config.QuantumConfigs;

public class PlayerRespawnListener implements Listener
{

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Location location = QuantumParkour.getConfigManager().getConfig(QuantumConfigs.SPAWN).getLocation("spawn");
        if (location != null)
        {
            event.setRespawnLocation(location);
        }
    }
}
