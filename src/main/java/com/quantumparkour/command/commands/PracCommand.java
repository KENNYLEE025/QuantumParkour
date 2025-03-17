package com.quantumparkour.command.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.config.QuantumConfigs;
import com.quantumparkour.util.PlaceholderAPIWrapper;

public class PracCommand implements QuantumCommand {

    @Override
    public String getName() {
        return "prac";
    }

    @Override
    public String getDescription() {
        return "Enables practice mode";
    }

    @Override
    public String getUsage() {
        return "/prac";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }
        if (!player.isOnGround()) {
            player.sendRichMessage(PlaceholderAPIWrapper.setPlaceholders(player, QuantumParkour.getConfigManager().getConfig(QuantumConfigs.MESSAGES).getString("must-be-on-ground")));
            return;
        }
        Location pracLocation = QuantumParkour.getPracManager().getPracLocation(player);
        if (pracLocation != null) {
            player.sendRichMessage(PlaceholderAPIWrapper.setPlaceholders(player, QuantumParkour.getConfigManager().getConfig(QuantumConfigs.MESSAGES).getString("prac-already-enabled")));
            return;
        }
        QuantumParkour.getPracManager().setPracLocation(player, player.getLocation());
        QuantumParkour.getPracManager().setCheckpointLocation(player, player.getLocation()); // Ensure checkpoint is set
        player.sendRichMessage(PlaceholderAPIWrapper.setPlaceholders(player, QuantumParkour.getConfigManager().getConfig(QuantumConfigs.MESSAGES).getString("prac-enabled")));

        // Add items to player's inventory
        ItemStack checkpointItem = new ItemStack(Material.RED_DYE);
        ItemMeta checkpointMeta = checkpointItem.getItemMeta();
        if (checkpointMeta != null) {
            checkpointMeta.setDisplayName("§6Practice Checkpoint");
            checkpointItem.setItemMeta(checkpointMeta);
        }
        player.getInventory().addItem(checkpointItem);

        ItemStack setPracItem = new ItemStack(Material.DIAMOND);
        ItemMeta setPracMeta = setPracItem.getItemMeta();
        if (setPracMeta != null) {
            setPracMeta.setDisplayName("§6Set Practice Checkpoint");
            setPracItem.setItemMeta(setPracMeta);
        }
        player.getInventory().addItem(setPracItem);
    }
}
