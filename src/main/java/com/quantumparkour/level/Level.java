package com.quantumparkour.level;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class Level implements ConfigurationSerializable {
    private String name;
    private String displayName;
    private int reward = 0;
    private Location startLocation;
    private int maxCompletions = -1;
    private boolean announceCompletion = false;
    private LevelType levelType = LevelType.DEFAULT;
    private boolean practiceable = false;
    private List<PotionEffect> potionEffects = new ArrayList<>();

    public Level(String name) {
        this.name = name;
    }

    private Level() {}

    public String getName() {
        return this.name; //returns the name
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getReward() {
        return this.reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public Location getStartLocation() {
        return this.startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public int getMaxCompletions() {
        return this.maxCompletions;
    }

    public void setMaxCompletions(int maxCompletions) {
        this.maxCompletions = maxCompletions;
    }

    public boolean isAnnounceCompletion() {
        return this.announceCompletion;
    }

    public void setAnnounceCompletion(boolean announceCompletion) {
        this.announceCompletion = announceCompletion;
    }

    public LevelType getLevelType() {
        return this.levelType;
    }

    public void setLevelType(LevelType levelType) {
        this.levelType = levelType;
    }

    public boolean isPracticeable() {
        return this.practiceable;
    }

    public void setPracticeable(boolean practiceable) {
        this.practiceable = practiceable;
    }

    public List<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }

    public void setPotionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        addIf(data, "display-name", displayName, displayName != null);
        addIf(data, "reward", reward, reward > 0);
        addIf(data, "start-location", startLocation, startLocation != null);
        addIf(data, "max-completions", maxCompletions, maxCompletions > -1);
        addIf(data, "announce-completion", announceCompletion, announceCompletion);
        addIf(data, "level-type", levelType.name(), true);
        addIf(data, "practiceable", practiceable, practiceable);
        addIf(data, "potion-effects", potionEffects, potionEffects != null && !potionEffects.isEmpty());
        return data;
    }

    private void addIf(Map<String, Object> map, String key, Object value, boolean condition) {
        if (condition) {
            map.put(key, value);
        }
    }

    @Override
    public String toString() {
        return "Level{" +
                "name='" + name + '\'' +
                ", displayName=" + displayName +
                ", reward=" + reward +
                ", startLocation=" + startLocation +
                ", maxCompletions=" + maxCompletions +
                ", announceCompletion=" + announceCompletion +
                ", levelType=" + levelType +
                ", practiceable=" + practiceable +
                ", potionEffects=" + potionEffects +
                '}';
    }

    @NotNull
    public static Level deserialize(@NotNull Map<String, Object> args) {
        Level level = new Level();

        Optional.ofNullable(args.get("display-name"))
                .map(name -> (String) name)
                .ifPresent(level::setDisplayName);

        Optional.ofNullable(args.get("reward"))
                .map(reward -> (int) reward)
                .ifPresent(level::setReward);

        Optional.ofNullable(args.get("start-location"))
                .map(loc -> (Location) loc)
                .ifPresent(level::setStartLocation);

        Optional.ofNullable(args.get("max-completions"))
                .map(max -> (int) max)
                .ifPresent(level::setMaxCompletions);

        Optional.ofNullable(args.get("announce-completion"))
                .map(announce -> (boolean) announce)
                .ifPresent(level::setAnnounceCompletion);

        Optional.ofNullable(args.get("level-type"))
                .flatMap(type -> Stream.of(LevelType.values())
                        .filter(levelType -> levelType.name().equalsIgnoreCase((String) type))
                        .findFirst())
                .ifPresent(level::setLevelType);

        Optional.ofNullable(args.get("practiceable"))
                .map(practiceable -> (boolean) practiceable)
                .ifPresent(level::setPracticeable);

        Optional.ofNullable(args.get("potion-effects"))
                .map(effects -> (List<PotionEffect>) effects)
                .ifPresent(level::setPotionEffects);

        return level;
    }
}
