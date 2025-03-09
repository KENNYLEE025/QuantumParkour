package com.quantumparkour.command.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.config.QuantumConfig;

import java.util.List;

public class SpawnCommand implements QuantumCommand {
    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getDescription() {
        return "Teleports you to spawn";
    }

    @Override
    public String getUsage() {
        return "/spawn";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }
        Location location = QuantumParkour.getConfigManager().getConfig(QuantumConfig.SPAWN).getLocation("spawn");
        if (location != null) {
            player.teleport(location);
            return;
        }
        player.sendRichMessage("<red>No spawn location was found.");
    }
}
