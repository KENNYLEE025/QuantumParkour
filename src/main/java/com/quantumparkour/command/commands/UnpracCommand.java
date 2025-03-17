package com.quantumparkour.command.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.config.QuantumConfigs;
import com.quantumparkour.util.PlaceholderAPIWrapper;

public class UnpracCommand implements QuantumCommand {

    @Override
    public String getName() {
        return "unprac";
    }

    @Override
    public String getDescription() {
        return "Disables practice mode";
    }

    @Override
    public String getUsage() {
        return "/unprac";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }
        Location pracLocation = QuantumParkour.getPracManager().getPracLocation(player);
        if (pracLocation == null) {
            player.sendRichMessage(PlaceholderAPIWrapper.setPlaceholders(player, QuantumParkour.getConfigManager().getConfig(QuantumConfigs.MESSAGES).getString("not-in-prac")));
            return;
        }
        player.teleport(pracLocation);
        QuantumParkour.getPracManager().removePracLocation(player);
        QuantumParkour.getPracManager().removeCheckpointLocation(player);

        // Remove the specific diamond item from the player's inventory
        ItemStack[] inventoryContents = player.getInventory().getContents();
        for (ItemStack item : inventoryContents) {
            if (item != null && item.getType() == Material.DIAMOND) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && "§6Set Practice Checkpoint".equals(meta.getDisplayName())) {
                    player.getInventory().remove(item);
                    break;
                }
            }
        }

        player.sendRichMessage(PlaceholderAPIWrapper.setPlaceholders(player, QuantumParkour.getConfigManager().getConfig(QuantumConfigs.MESSAGES).getString("prac-disabled")));
    }
}