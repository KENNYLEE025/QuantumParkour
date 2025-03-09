package com.quantumparkour.config;

public enum QuantumConfig {
    MESSAGES("messages", true),
    SPAWN("spawn", false);

    private final String name;
    private final boolean loadDefaults;

    QuantumConfig(String name, boolean loadDefaults) {
        this.name = name;
        this.loadDefaults = loadDefaults;
    }

    public String getName() {
        return this.name;
    }

    public boolean shouldLoadDefaults() {
        return loadDefaults;
    }
}
