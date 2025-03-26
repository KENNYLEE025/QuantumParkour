package com.quantumparkour;

import com.quantumparkour.level.Level;
import com.quantumparkour.level.LevelManager;
import com.quantumparkour.listener.*;
import com.quantumparkour.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.quantumparkour.command.CommandManager;
import com.quantumparkour.command.commands.*;
import com.quantumparkour.config.ConfigManager;
import com.quantumparkour.config.QuantumConfigs;
//import com.quantumparkour.todo.PlayerJoinListener;
//import com.quantumparkour.todo.QDebugCommand;
import com.quantumparkour.database.QuantumDatabase;
import com.quantumparkour.util.PlaceholderAPIWrapper;
//import com.quantumparkour.manager.QwobitManager;

import java.util.List;
import java.util.function.Supplier;

public final class QuantumParkour extends JavaPlugin {
    private static QuantumParkour instance;
    private static CommandManager commandManager;
    private static ConfigManager configManager;
    private static LevelManager levelManager;
    private static PlayerManager playerManager;
    private static PracManager pracManager;
    //private static QwobitManager qwobitManager;

    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass(Level.class);
    }

    @Override
    public void onEnable() {
        PlaceholderAPIWrapper.init();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        instance = this;
        commandManager = new CommandManager();
        configManager = new ConfigManager();
        levelManager = new LevelManager();
        playerManager = new PlayerManager();
        pracManager = new PracManager();

        //qwobitManager = new QwobitManager();
        QuantumDatabase.initialize(); // Initialize the database
        registerEvents(
                () -> new BlockEventListener(this),
                CheckpointItemListener::new,
                PlayerJoinListener::new,
                PlayerLeaveListener::new,
                PlayerRespawnListener::new,
                ServerTickEndListener::new,
                PortalListener::new
        );
        commandManager.registerCommands(
                CheckpointCommand::new,
                ConfirmCommand::new,
                LevelCommand::new,
                PracCommand::new,
                SetPracCommand::new,
                SetSpawnCommand::new,
                SpawnCommand::new,
                UnpracCommand::new,
                //QwobitsCommand::new, // Register the new command here
                QDebugCommand::new // Register the QDebugCommand here
        );
        configManager.registerConfigs(QuantumConfigs.values());
        levelManager.loadLevels(QuantumConfigs.LEVELS);
    }

    @SafeVarargs
    private void registerEvents(Supplier<Listener>... suppliers) {
        List.of(suppliers).forEach(listenerSupplier -> getServer().getPluginManager().registerEvents(listenerSupplier.get(), this));
    }

    public static QuantumParkour getPlugin() { return instance; }
    public static CommandManager getCommandManager() { return commandManager; }
    public static ConfigManager getConfigManager() { return configManager; }
    public static LevelManager getLevelManager() { return levelManager; }
    public static PlayerManager getPlayerManager() { return playerManager; }
    public static PracManager getPracManager() { return pracManager; }
    //public static QwobitManager getQwobitManager() { return qwobitManager; }
}