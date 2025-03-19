package com.quantumparkour.command.commands;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.level.Level;
import com.quantumparkour.util.SafeConfirm;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LevelCommand implements QuantumCommand {
    @Override
    public String getName() {
        return "level";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendRichMessage("<red>Not enough arguments.");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "create" -> create(sender, args);
            case "delete" -> delete(sender, args);
            case "info" -> info(sender, args);
            case "list" -> list(sender, args);
            case "settings" -> settings(sender, args);
            default -> sender.sendRichMessage("<red>" + this.getUsage());
        }
    }

    private void create(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendRichMessage("<red>Not enough arguments.");
            return;
        }
        if (QuantumParkour.getLevelManager().getLevels()
                .stream()
                .map(Level::getName)
                .anyMatch(name -> name.equalsIgnoreCase(args[1]))) {
            sender.sendRichMessage("<red>A level with name '" + args[1].toLowerCase() + "' already exists.");
            return;
        }
        Level level = new Level(args[1].toLowerCase());
        QuantumParkour.getLevelManager().addLevel(level);
        sender.sendRichMessage("Created level '" + args[1].toLowerCase() + "'.");
    }

    private void delete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendRichMessage("<red>Not enough arguments.");
            return;
        }
        Level level = QuantumParkour.getLevelManager().getLevel(args[1].toLowerCase());
        if (level == null) {
            sender.sendRichMessage("<red>'" + args[1].toLowerCase() + "' is not a level.");
            return;
        }
        sender.sendRichMessage("Are you sure you want to delete level '" + level.getName() + "'?");
        SafeConfirm.create(sender, 500, sender1 -> {
            QuantumParkour.getLevelManager().deleteLevel(level);
            sender1.sendRichMessage("Deleted level '" + args[1].toLowerCase() + "'.");
        });
    }

    private void info(CommandSender sender, String[] args) {
        sender.sendRichMessage("info");
    }

    private void list(CommandSender sender, String[] args) {
        sender.sendRichMessage("Loaded levels:");
        QuantumParkour.getLevelManager().getLevels()
                .stream()
                .map(level -> " • <click:run_command:'/say hi'>" + level.getName() + "</click>")
                .forEach(sender::sendRichMessage);
    }

    private void settings(CommandSender sender, String[] args) {
        sender.sendRichMessage("settings");
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("create", "delete", "info", "list", "settings");
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete")
                    || args[0].equalsIgnoreCase("info")
                    || args[0].equalsIgnoreCase("settings")) {
                return QuantumParkour.getLevelManager().getLevels()
                        .stream()
                        .map(Level::getName)
                        .collect(Collectors.toList());
            }
        }
        return null;
    }
}