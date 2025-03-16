package com.quantumparkour.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.quantumparkour.QuantumParkour;

public class CheckpointItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null) {
            if (item.getType() == Material.RED_DYE && "§6Practice Checkpoint".equals(item.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
                if (QuantumParkour.getPracManager().getPracLocation(player) == null) {
                    player.sendMessage("You are not in practice mode!");
                    return;
                }
                Location checkpointLocation = QuantumParkour.getPracManager().getCheckpointLocation(player);
                if (checkpointLocation != null) {
                    player.teleport(checkpointLocation);
                    player.sendMessage("Teleported to checkpoint!");
                } else {
                    player.sendMessage("No checkpoint set!");
                }
            } else if (item.getType() == Material.DIAMOND && "§6Set Practice Checkpoint".equals(item.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
                if (QuantumParkour.getPracManager().getPracLocation(player) == null) {
                    player.sendMessage("You are not in practice mode!");
                    return;
                }
                QuantumParkour.getPracManager().setCheckpointLocation(player, player.getLocation());
                player.sendMessage("Practice checkpoint set!");
            }
        }
    }
}
