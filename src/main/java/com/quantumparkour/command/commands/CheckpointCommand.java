package com.quantumparkour.command.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;

import java.util.List;

public class CheckpointCommand implements QuantumCommand {

    @Override
    public String getName() {
        return "checkpoint";
    }

    @Override
    public String getDescription() {
        return "Teleports you back to your last checkpoint";
    }

    @Override
    public String getUsage() {
        return "/checkpoint";
    }

    @Override
    public List<String> getAliases() {
        return List.of("cp");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("<7o Section> Only players are able to use /checkpoint");
            return;
        }
        Location checkpointLocation = QuantumParkour.getPracManager().getCheckpointLocation(player);
        if (checkpointLocation == null) {
            player.sendMessage("<7o Section> No checkpoint set");
            return;
        }
        player.teleport(checkpointLocation);
    }
}
