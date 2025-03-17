package com.quantumparkour.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.config.QuantumConfigs;
import com.quantumparkour.util.PlaceholderAPIWrapper;

public class SetPracCommand implements QuantumCommand {

    @Override
    public String getName() {
        return "setprac";
    }

    @Override
    public String getDescription() {
        return "Sets a temporary practice checkpoint";
    }

    @Override
    public String getUsage() {
        return "/setprac";
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
        QuantumParkour.getPracManager().setCheckpointLocation(player, player.getLocation());
        player.sendRichMessage(PlaceholderAPIWrapper.setPlaceholders(player, QuantumParkour.getConfigManager().getConfig(QuantumConfigs.MESSAGES).getString("temp-prac-location-set")));
    }
}