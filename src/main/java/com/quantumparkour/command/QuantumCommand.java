package com.quantumparkour.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface QuantumCommand {
    String getName();

    default String getDescription() {
        return null;
    }

    default String getUsage() {
        return null;
    }

    default List<String> getAliases() {
        return null;
    }

    default String getPermission() {
        return null;
    }

    void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

    default List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
