package com.quantumparkour.level;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.config.QuantumConfigs;

import java.util.*;

public class LevelManager {
    private final List<Level> levels = new ArrayList<>();

    public List<Level> getLevels() {
        return Collections.unmodifiableList(this.levels);
    }

    public Level getLevel(String name) {
        return levels.stream()
                .filter(level -> level.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void addLevel(Level level) {
        this.levels.add(level);
        QuantumParkour.getConfigManager().getConfig(QuantumConfigs.LEVELS).set(level.getName(), level);
        QuantumParkour.getConfigManager().saveConfig(QuantumConfigs.LEVELS);
    }

    public void deleteLevel(Level level) {
        this.levels.remove(level);
        QuantumParkour.getConfigManager().getConfig(QuantumConfigs.LEVELS).set(level.getName(), null);
        QuantumParkour.getConfigManager().saveConfig(QuantumConfigs.LEVELS);
    }

    public void loadLevels(QuantumConfigs config) {
        QuantumParkour.getConfigManager().getConfig(config).getKeys(true).forEach(key -> {
            Level level = (Level) QuantumParkour.getConfigManager().getConfig(config).get(key);
            level.setName(key);
            this.addLevel(level);
        });
    }
}
