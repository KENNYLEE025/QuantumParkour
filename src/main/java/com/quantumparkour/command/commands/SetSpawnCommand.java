package com.quantumparkour.command.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.config.QuantumConfig;

import java.util.List;

public class SetSpawnCommand implements QuantumCommand {

    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public String getDescription() {
        return "Sets the server spawn";
    }

    @Override
    public String getUsage() {
        return "/setspawn [<x> <y> <z>] [<yaw> <pitch>] [<world>]";
    }

    @Override
    public String getPermission() {
        return "7osection.admin";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }
        Location location = new Location(player.getWorld(), player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        if (args.length > 0 && args.length < 3 || args.length == 4) {
            sender.sendRichMessage("<red>Too few arguments.");
            return;
        }
        if (args.length != 0) {
            Double x = parse(args[0], player.getX());
            Double y = parse(args[1], player.getY());
            Double z = parse(args[2], player.getZ());
            if (x == null) {
                player.sendRichMessage("<red>'" + args[0] + "' is not a valid argument.");
                return;
            }
            if (y == null) {
                player.sendRichMessage("<red>'" + args[1] + "' is not a valid argument.");
                return;
            }
            if (z == null) {
                player.sendRichMessage("<red>'" + args[2] + "' is not a valid argument.");
                return;
            }
        }
        if (args.length == 5) {
            Double yaw = parse(args[3], player.getYaw());
            Double pitch = parse(args[4], player.getPitch());
            if (yaw == null) {
                player.sendRichMessage("<red>'" + args[3] + "' is not a valid yaw.");
                return;
            }
            if (pitch == null) {
                player.sendRichMessage("<red>'" + args[4] + "' is not a valid pitch.");
                return;
            }
            location.setRotation(yaw.floatValue(), pitch.floatValue());
        }
        if (args.length == 6) {
            World world = QuantumParkour.getPlugin().getServer().getWorld(args[5]);
            if (world == null) {
                player.sendRichMessage("<red>'" + args[5] + "' is not a valid world.");
                return;
            }
            location.setWorld(world);
        }
        QuantumParkour.getConfigManager().getConfig(QuantumConfig.SPAWN).set("spawn", location);
        QuantumParkour.getConfigManager().saveConfig(QuantumConfig.SPAWN);
        sender.sendRichMessage("Spawn has been set to " + location.getX() + ", " + location.getY() + ", " + location.getZ() + " in: " + location.getWorld().getName());
    }

    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<List<String>> suggestions = List.of(
                List.of("~", "~ ~", "~ ~ ~"),
                List.of("~", "~ ~"),
                List.of("~")
        );

        if (args.length >= 1 && args.length <= 5) {
            return suggestions.get(args.length - (args.length > 3 ? 3 : 1));
        }

        if (args.length == 6) {
            return QuantumParkour.getPlugin().getServer().getWorlds().stream().map(WorldInfo::getName).toList();
        }

        return null;
    }

    private Double parse(String arg, double d) {
        if (arg == null || arg.isEmpty() || arg.contains("e") || arg.contains("E")) {
            return null;
        }

        boolean isRelative = arg.startsWith("~");

        if (isRelative && arg.length() == 1) {
            return d;
        }

        double result;
        try {
            result = Double.parseDouble(isRelative ? arg.substring(1) : arg);
        } catch (NumberFormatException e) {
            return null;
        }

        if (isRelative) {
            result += d;
        }

        return result;
    }
}
