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

public class CommandManager
{

    public static final Plugin m_plugin = QuantumParkour.getPlugin();

    //---------------------------------------------------------------------------------------------
    public void registerCommand(QuantumCommand command)
    {
        if (command == null)
        {
            return;
        }

        PluginCommand pluginCommand = createPluginCommand(command.getName());
        if (pluginCommand == null)  return;

        applyMetadata(pluginCommand, command);
        applyHandlers(pluginCommand, command);

        Bukkit.getCommandMap().register(m_plugin.getName(), pluginCommand);
    }

    //---------------------------------------------------------------------------------------------
    @SafeVarargs
    public final void registerCommands(Supplier<QuantumCommand>... suppliers)
    {
        for (Supplier<QuantumCommand> supplier : suppliers)
        {
            registerCommand(supplier.get());
        }
    }

    //---------------------------------------------------------------------------------------------
    public PluginCommand createPluginCommand(String commandName)
    {
        try
        {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(commandName, m_plugin);
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception)
        {
            m_plugin.getLogger().warning("Unable to register command " + commandName);
            return null;
        }
    }

    //---------------------------------------------------------------------------------------------
    private void applyMetadata(PluginCommand pluginCommand, QuantumCommand command)
    {
        if (command.getDescription() != null)
        {
            pluginCommand.setDescription(command.getDescription());
        }

        if (command.getUsage() != null)
        {
            pluginCommand.setUsage(command.getUsage());
        }

        if (command.getPermission() != null)
        {
            pluginCommand.setPermission(command.getPermission());
        }

        pluginCommand.setAliases(parseAliases(command));
    }

    //---------------------------------------------------------------------------------------------
    private void applyHandlers(PluginCommand pluginCommand, QuantumCommand command)
    {
        pluginCommand.setExecutor((sender, bukkitCommand, label, args) ->
        {
            command.execute(sender, bukkitCommand, label, args);
            return true;
        });

        pluginCommand.setTabCompleter((sender, bukkitCommand, label, args) ->
        {
            List<String> suggestions = command.getTabCompletions(sender, bukkitCommand, label, args);
            if (suggestions == null || args.length == 0)
            {
                return Collections.emptyList();
            }

            return StringUtil.copyPartialMatches(args[args.length - 1], suggestions, new ArrayList<>());
        });
    }

    //---------------------------------------------------------------------------------------------
    private List<String> parseAliases(QuantumCommand command)
    {
        List<String> aliases = command.getAliases();
        if (aliases == null)
        {
            return Collections.emptyList();
        }

        List<String> validAliases = new ArrayList<>();
        for (String alias : aliases)
        {
            if (alias == null)
            {
                continue;
            }

            if (alias.contains(":"))
            {
                m_plugin.getLogger().severe("Could not load alias " + alias + " for plugin " + m_plugin.getName() + ": Illegal Characters");
                continue;
            }

            validAliases.add(alias);
        }

        return validAliases;
    }
}
