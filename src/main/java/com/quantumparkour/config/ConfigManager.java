package com.quantumparkour.config;

import com.google.common.base.Charsets;
import com.quantumparkour.QuantumParkour;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final Map<String, Config> configs = new HashMap<>();

    public ConfigManager() {
        Arrays.stream(QuantumConfigs.values()).forEach(this::load);
    }

    public void registerConfigs(QuantumConfigs... configs) {
        List.of(configs).forEach(this::load);
    }

    private void load(QuantumConfigs configs) {
        File file = new File(QuantumParkour.getPlugin().getDataFolder(), configs.getName() + ".yml");
        try {

            if (!file.exists()) {
                file.getParentFile().mkdirs();

                try (InputStream source = QuantumParkour.getPlugin().getResource("config/" + configs.getName() + ".yml");
                     BufferedInputStream bis = new BufferedInputStream(source);
                     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {

                    if (source != null) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }

            YamlConfiguration configuration = new YamlConfiguration();
            configuration.options().parseComments(true);
            configuration.load(file);

            if (configs.shouldLoadDefaults()) {
                try (InputStream defConfigStream = QuantumParkour.getPlugin().getResource("config/" + configs.getName() + ".yml")) {
                    if (defConfigStream != null) {
                        configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
                    }
                }
            }

            this.configs.put(configs.getName(), new Config(file, configuration));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig(QuantumConfigs configs) {
        if (this.configs.get(configs.getName()) == null) {
            Bukkit.getConsoleSender().sendRichMessage("<red>Cannot save unregistered config " + configs.getName() + ".yml");
            return;
        }
        try {
            Config config = this.configs.get(configs.getName());
            if (config == null) {
                throw new IOException();
            }
            config.configuration.save(config.file);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendRichMessage("<red>Unable to save config " + configs.getName() + ".yml");
        }
    }

    public FileConfiguration getConfig(QuantumConfigs configs) {
        if (this.configs.get(configs.getName()) == null) {
            Bukkit.getConsoleSender().sendRichMessage("<red>Cannot get unregistered config " + configs.getName() + ".yml");
        }
        return this.configs.get(configs.getName()).configuration;
    }

    public void reloadConfig(QuantumConfigs configs) {
        if (this.configs.get(configs.getName()) == null) {
            Bukkit.getConsoleSender().sendRichMessage("<red>Cannot reload unregistered config " + configs.getName() + ".yml");
            return;
        }
        this.load(configs);
    }

    private record Config(File file, FileConfiguration configuration) {}
}