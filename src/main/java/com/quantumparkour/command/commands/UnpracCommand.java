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

//----------------------------------------------------------------------------------------------------------------------
public class UnpracCommand implements QuantumCommand
{
    //---------------------------------------------------------------------------------------------
    @Override
    public String getName()
    {
        return "unprac";
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public String getDescription()
    {
        return "Disables practice mode";
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public String getUsage()
    {
        return "/unprac";
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (!(sender instanceof Player player))
        {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        removePracticeCoordinates(player);
        removePracticeItems(player);
    }

    //---------------------------------------------------------------------------------------------
    private void removePracticeCoordinates(Player player)
    {
        Location pracLocation = QuantumParkour.getPracManager().getPracLocation(player);
        if (pracLocation == null)
        {
            player.sendRichMessage(PlaceholderAPIWrapper.setPlaceholders(player, QuantumParkour.getConfigManager().getConfig(QuantumConfigs.MESSAGES).getString("not-in-prac")));
            return;
        }

        player.teleport(pracLocation);
        QuantumParkour.getPracManager().removePracLocation(player);
        QuantumParkour.getPracManager().removeCheckpointLocation(player);
    }

    //---------------------------------------------------------------------------------------------
    private void removePracticeItems(Player player)
    {
        ItemStack[] inventoryContents = player.getInventory().getContents();
        for (ItemStack item : inventoryContents)
        {
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (item.getType() == Material.DIAMOND && "§6Set Practice Checkpoint".equals(meta.getDisplayName()))
            {
                player.getInventory().remove(item);
            }
            if (item.getType() == Material.RED_DYE && "§6Practice Checkpoint".equals(meta.getDisplayName()))
            {
                player.getInventory().remove(item);
            }
        }

        player.sendRichMessage(PlaceholderAPIWrapper.setPlaceholders(player, QuantumParkour.getConfigManager().getConfig(QuantumConfigs.MESSAGES).getString("prac-disabled")));
    }
}