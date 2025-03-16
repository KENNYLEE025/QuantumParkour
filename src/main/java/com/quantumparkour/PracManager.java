package com.quantumparkour;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PracManager implements Listener {
    private final Map<UUID, Location> pracCache = new HashMap<>();
    private final Map<UUID, Location> checkpointCache = new HashMap<>();

    public PracManager() {}

    public Location getPracLocation(Player player) {
        return pracCache.get(player.getUniqueId());
    }

    public void setPracLocation(Player player, Location location) {
        pracCache.put(player.getUniqueId(), location);
    }

    public void removePracLocation(Player player) {
        pracCache.remove(player.getUniqueId());
    }

    public Location getCheckpointLocation(Player player) {
        return checkpointCache.get(player.getUniqueId());
    }

    public void setCheckpointLocation(Player player, Location location) {
        checkpointCache.put(player.getUniqueId(), location);
    }

    public void removeCheckpointLocation(Player player) {
        checkpointCache.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null) {
            if (item.getType() == Material.RED_DYE && "§6Practice Checkpoint".equals(item.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
                Location checkpointLocation = QuantumParkour.getPracManager().getCheckpointLocation(event.getPlayer());
                if (checkpointLocation != null) {
                    event.getPlayer().teleport(checkpointLocation);
                    event.getPlayer().sendMessage("Teleported to checkpoint!");
                } else {
                    event.getPlayer().sendMessage("No checkpoint set!");
                }
            } else if (item.getType() == Material.DIAMOND && "§6Set Practice Checkpoint".equals(item.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
                QuantumParkour.getPracManager().setCheckpointLocation(event.getPlayer(), event.getPlayer().getLocation());
                event.getPlayer().sendMessage("Practice checkpoint set!");
            }
        }
    }
}
