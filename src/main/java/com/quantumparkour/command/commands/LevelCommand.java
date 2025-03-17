package com.quantumparkour.command.commands;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.level.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
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
            case "create" -> {
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
            case "delete" -> {
                if (args.length < 2) {
                    sender.sendRichMessage("<red>Not enough arguments.");
                    return;
                }
                Level level = QuantumParkour.getLevelManager().getLevel(args[1].toLowerCase());
                if (level == null) {
                    sender.sendRichMessage("<red>'" + args[1].toLowerCase() + "' is not a level.");
                    return;
                }
                String code = generateCode();
                sender.sendRichMessage("Are you sure you want to delete level '" + level.getName() + "'?");
                sender.sendRichMessage("Confirm by typing: /confirm " + code);
            }
            case "info" -> {
                System.out.println(3);
            }
            case "list" -> {
                sender.sendRichMessage("Loaded levels:");
                QuantumParkour.getLevelManager().getLevels()
                        .stream()
                        .map(level -> " • <click:run_command:'/say hi'>" + level.getName() + "</click>")
                        .forEach(sender::sendRichMessage);
            }
            case "settings" -> {
                System.out.println(5);
            }
            default -> sender.sendRichMessage("<red>" + this.getUsage());
        }
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

    private String generateCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            int randomIndex = random.nextInt(characters.length());
            builder.append(characters.charAt(randomIndex));
        }
        return builder.toString();
    }
}