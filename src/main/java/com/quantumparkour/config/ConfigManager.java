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

//----------------------------------------------------------------------------------------------------------------------
public class ConfigManager {
    private final Map<String, Config> m_configs = new HashMap<>();

    //---------------------------------------------------------------------------------------------
    public ConfigManager()
    {
        Arrays.stream(QuantumConfigs.values()).forEach(this::load);
    }

    //---------------------------------------------------------------------------------------------
    public void registerConfigs(QuantumConfigs... configs)
    {
        List.of(configs).forEach(this::load);
    }

    //---------------------------------------------------------------------------------------------
    public void saveConfig(QuantumConfigs configType)
    {
        Config config = getRegisteredConfig(configType, "save");
        if (config == null)
        {
            return;
        }

        try
        {
            config.configuration().save(config.file());
        }
        catch (IOException exception)
        {
            Bukkit.getConsoleSender().sendRichMessage("<red>Unable to save config " + configType.getName() + ".yml");
        }
    }

    //---------------------------------------------------------------------------------------------
    public FileConfiguration getConfig(QuantumConfigs configType)
    {
        Config config = getRegisteredConfig(configType, "get");
        if (config == null)
        {
            return null;
        }
        return config.configuration();
    }

    //---------------------------------------------------------------------------------------------
    public void reloadConfig(QuantumConfigs configType)
    {
        if (!m_configs.containsKey(configType.getName()))
        {
            Bukkit.getConsoleSender().sendRichMessage("<red>Cannot reload unregistered config " + configType.getName() + ".yml");
            return;
        }

        load(configType);
    }

    //---------------------------------------------------------------------------------------------
    private void load(QuantumConfigs configType)
    {
        File file = getConfigFile(configType);

        try
        {
            ensureConfigFileExists(configType, file);

            YamlConfiguration configuration = loadYamlConfiguration(file);
            applyDefaultsIfNeeded(configType, configuration);

            m_configs.put(configType.getName(), new Config(file, configuration));
        }
        catch (IOException | InvalidConfigurationException exception)
        {
            exception.printStackTrace();
        }
    }

    //---------------------------------------------------------------------------------------------
    private File getConfigFile(QuantumConfigs configType)
    {
        return new File(QuantumParkour.getPlugin().getDataFolder(), configType.getName() + ".yml");
    }

    //---------------------------------------------------------------------------------------------
    private void ensureConfigFileExists(QuantumConfigs configType, File file) throws IOException
    {
        if (file.exists())
        {
            return;
        }

        File parent = file.getParentFile();
        if (parent != null)
        {
            parent.mkdirs();
        }

        copyDefaultConfigToFile(configType, file);
    }

    //---------------------------------------------------------------------------------------------
    private void copyDefaultConfigToFile(QuantumConfigs configType, File file) throws IOException
    {
        try (InputStream source = QuantumParkour.getPlugin().getResource("config/" + configType.getName() + ".yml"))
        {
            if (source == null)
            {
                return;
            }

            try (BufferedInputStream bis = new BufferedInputStream(source);
                 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file)))
            {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = bis.read(buffer)) != -1)
                {
                    bos.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    //---------------------------------------------------------------------------------------------
    private YamlConfiguration loadYamlConfiguration(File file) throws IOException, InvalidConfigurationException
    {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().parseComments(true);
        configuration.load(file);
        return configuration;
    }

    //---------------------------------------------------------------------------------------------
    private void applyDefaultsIfNeeded(QuantumConfigs configType, YamlConfiguration configuration)
    {
        if (!configType.shouldLoadDefaults())
        {
            return;
        }

        try (InputStream defaultConfigStream = QuantumParkour.getPlugin().getResource("config/" + configType.getName() + ".yml"))
        {
            if (defaultConfigStream == null)
            {
                return;
            }

            InputStreamReader reader = new InputStreamReader(defaultConfigStream, Charsets.UTF_8);
            configuration.setDefaults(YamlConfiguration.loadConfiguration(reader));
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    //---------------------------------------------------------------------------------------------
    private Config getRegisteredConfig(QuantumConfigs configType, String action)
    {
        Config config = m_configs.get(configType.getName());
        if (config != null)
        {
            return config;
        }

        Bukkit.getConsoleSender().sendRichMessage("<red>Cannot " + action + " unregistered config " + configType.getName() + ".yml");
        return null;
    }

    //---------------------------------------------------------------------------------------------
    private record Config(File file, FileConfiguration configuration) {}
}