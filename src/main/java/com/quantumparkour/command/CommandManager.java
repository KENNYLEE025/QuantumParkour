package com.quantumparkour.command;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import com.quantumparkour.QuantumParkour;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CommandManager {

    public void registerCommand(QuantumCommand command) {
        if (command == null) {
            return;
        }

        PluginCommand pluginCommand;

        try {
            Constructor<?> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            pluginCommand = (PluginCommand) constructor.newInstance(command.getName(), QuantumParkour.getPlugin());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            QuantumParkour.getPlugin().getLogger().warning("Unable to register command " + command.getName());
            return;
        }

        if (command.getDescription() != null) {
            pluginCommand.setDescription(command.getDescription());
        }

        if (command.getUsage() != null) {
            pluginCommand.setUsage(command.getUsage());
        }

        if (command.getAliases() != null) {
            List<String> aliasList = new ArrayList<>();
            Stream.of(command.getAliases())
                    .flatMap(aliases -> aliases != null ? ((List<?>) aliases).stream() : Stream.of(aliases))
                    .map(Object::toString)
                    .forEach(alias -> {
                        if (alias.contains(":")) {
                            Bukkit.getServer().getLogger().severe("Could not load alias " + alias + " for plugin " + QuantumParkour.getPlugin().getName() + ": Illegal Characters");
                        } else {
                            aliasList.add(alias);
                        }
                    });

            pluginCommand.setAliases(aliasList);
        }

        if (command.getPermission() != null) {
            pluginCommand.setPermission(command.getPermission());
        }

        pluginCommand.setExecutor((sender, command1, label, args) -> {
            command.execute(sender, command1, label, args);
            return true;
        });
        pluginCommand.setTabCompleter((sender, command1, label, args) -> {
            List<String> suggestions = command.getTabCompletions(sender, command1, label, args);
            if (suggestions == null || args.length == 0) {
                return Collections.emptyList();
            }
            return StringUtil.copyPartialMatches(args[args.length - 1], suggestions, new ArrayList<>());
        });

        Bukkit.getCommandMap().register(QuantumParkour.getPlugin().getName(), pluginCommand);
    }

    @SafeVarargs
    public final void registerCommands(Supplier<QuantumCommand>... suppliers) {
        for (Supplier<QuantumCommand> supplier : suppliers) {
            registerCommand(supplier.get());
        }
    }
}
