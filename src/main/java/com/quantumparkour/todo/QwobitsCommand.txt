package com.quantumparkour.todo;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QwobitsCommand implements QuantumCommand {

    @Override
    public String getName() {
        return "qwobits";
    }

    @Override
    public String getDescription() {
        return "Manage Qwobits for players.";
    }

    @Override
    public String getUsage() {
        return "/qwobits <balance/add/subtract/set> [player] [amount]";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /qwobits <balance/add/subtract/set> [player] [amount]");
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "balance" -> handleBalance(sender, args);
            case "add", "subtract", "set" -> handleQwobitModification(sender, args, subCommand);
            default -> sender.sendMessage("Unknown subcommand. Usage: /qwobits <balance/add/subtract/set>");
        }
    }

    private void handleBalance(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player player) {
                int balance = QuantumParkour.getQwobitManager().getQwobits(player);
                player.sendMessage("You have " + balance + " Qwobits.");
            } else {
                sender.sendMessage("Console must specify a player. Usage: /qwobits balance <player>");
            }
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return;
            }
            int balance = QuantumParkour.getQwobitManager().getQwobits(target);
            sender.sendMessage(target.getName() + " has " + balance + " Qwobits.");
        }
    }

    private void handleQwobitModification(CommandSender sender, String[] args, String action) {
        if (args.length != 3) {
            sender.sendMessage("Usage: /qwobits " + action + " <player> <amount>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            if (amount < 0) {
                sender.sendMessage("Amount must be a positive number.");
                return;
            }

            switch (action) {
                case "add" -> {
                    QuantumParkour.getQwobitManager().addQwobits(target, amount);
                    sender.sendMessage("Added " + amount + " Qwobits to " + target.getName() + ".");
                }
                case "set" -> {
                    QuantumParkour.getQwobitManager().setQwobits(target, amount);
                    sender.sendMessage("Set " + target.getName() + "'s Qwobits to " + amount + ".");
                }
                case "subtract" -> {
                    if (QuantumParkour.getQwobitManager().getQwobits(target) < amount) {
                        sender.sendMessage("Player does not have enough Qwobits.");
                        return;
                    }
                    QuantumParkour.getQwobitManager().reduceQwobits(target, amount);
                    sender.sendMessage("Subtracted " + amount + " Qwobits from " + target.getName() + ".");
                }
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid amount. Please enter a valid number.");
        }
    }

    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("balance", "add", "subtract", "set");
        } else if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 3 && !args[0].equalsIgnoreCase("balance")) {
            return List.of("<amount>");
        }
        return List.of();
    }
}
