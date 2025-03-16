package com.quantumparkour;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.quantumparkour.command.CommandManager;
import com.quantumparkour.command.commands.*;
import com.quantumparkour.config.ConfigManager;
import com.quantumparkour.config.QuantumConfig;
import com.quantumparkour.listener.BlockEventListener;
import com.quantumparkour.listener.CheckpointItemListener;
import com.quantumparkour.listener.PlayerLeaveListener;
import com.quantumparkour.listener.PlayerRespawnListener;
//import com.quantumparkour.todo.QwobitManager;
//import com.quantumparkour.todo.QwobitsCommand;
import com.quantumparkour.util.PlaceholderAPIWrapper;

import java.util.List;
import java.util.function.Supplier;

public final class QuantumParkour extends JavaPlugin {
    private static QuantumParkour instance;
    private static CommandManager commandManager;
    private static ConfigManager configManager;
    private static PracManager pracManager;
    //private static QwobitManager qwobitManager;

    @Override
    public void onEnable() {
        PlaceholderAPIWrapper.init();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        instance = this;
        commandManager = new CommandManager();
        configManager = new ConfigManager();
        pracManager = new PracManager();
        //qwobitManager = new QwobitManager();
        registerEvents(
                CheckpointItemListener::new,
                PlayerLeaveListener::new,
                PlayerRespawnListener::new,
                () -> new BlockEventListener(this)
        );
        commandManager.registerCommands(
                CheckpointCommand::new,
                PracCommand::new,
                SetPracCommand::new,
                SetSpawnCommand::new,
                SpawnCommand::new,
                UnpracCommand::new
                //QwobitsCommand::new // Register the new command here
        );
        configManager.registerConfigs(QuantumConfig.values());
        Bukkit.getPluginManager().registerEvents(new BlockEventListener(this), this);
    }

    @SafeVarargs
    private void registerEvents(Supplier<Listener>... suppliers) {
        List.of(suppliers).forEach(listenerSupplier -> getServer().getPluginManager().registerEvents(listenerSupplier.get(), this));
    }

    public static QuantumParkour getPlugin() { return instance; }
    public static CommandManager getCommandManager() { return commandManager; }
    public static ConfigManager getConfigManager() { return configManager; }
    public static PracManager getPracManager() { return pracManager; }
    //public static QwobitManager getQwobitManager() { return qwobitManager; }
}