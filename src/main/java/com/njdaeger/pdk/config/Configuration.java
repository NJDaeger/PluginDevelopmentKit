package com.njdaeger.pdk.config;

import org.bukkit.plugin.Plugin;

public abstract class Configuration<T extends Configuration<T>> implements IConfig {

    private final String configName;
    private final ConfigType<T> type;
    private Plugin plugin;

    public Configuration(Plugin plugin, ConfigType<T> type, String configName) {
        this.type = type;
        this.plugin = plugin;
        this.configName = configName;
    }

    @Override
    public ConfigType<?> getType() {
        return type;
    }

    @Override
    public String getName() {
        return configName;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
