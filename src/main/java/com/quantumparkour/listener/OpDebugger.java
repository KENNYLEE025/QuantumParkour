package com.quantumparkour.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OpDebugger implements Listener {

    private final JavaPlugin plugin;

    public OpDebugger(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Ensure the player is an OP in Creative mode
        if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
                Block block = event.getClickedBlock();
                Material type = block.getType();

                // Check if it's an iron trapdoor or iron door
                if (type == Material.IRON_TRAPDOOR || type == Material.IRON_DOOR) {
                    BlockData blockData = block.getBlockData();

                    // Ensure the blockData is Openable (i.e., a door/trapdoor)
                    if (blockData instanceof Openable) {
                        Openable openable = (Openable) blockData;
                        boolean isCurrentlyOpen = openable.isOpen();

                        // Toggle open state
                        openable.setOpen(!isCurrentlyOpen);
                        block.setBlockData(openable, false); // Prevents physics updates interfering

                        // Notify the player
                        player.sendMessage("Toggled " + type.name().toLowerCase().replace('_', ' ') + " to " + (!isCurrentlyOpen ? "open" : "closed"));

                        event.setCancelled(true); // Cancel the event to prevent Minecraft from overriding
                    }
                }
            }
        }
    }
}
