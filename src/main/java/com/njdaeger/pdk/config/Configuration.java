package com.njdaeger.pdk.config;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Set;

public class Configuration implements IConfig {

    private final IConfig configFile;
    private final String configName;
    private Plugin plugin;

    public Configuration(Plugin plugin, ConfigType<?> type, String configName) {
        this.plugin = plugin;
        this.configName = configName;
        this.configFile = type.createNew(plugin, configName);
    }

    @Override
    public ConfigType<?> getType() {
        return configFile.getType();
    }

    @Override
    public File getFile() {
        return configFile.getFile();
    }

    @Override
    public void reload() {
        configFile.reload();
    }

    @Override
    public void save() {
        configFile.save();
    }

    @Override
    public String getName() {
        return configName;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return configFile.getKeys(deep);
    }

    @Override
    public Object getValue(String path) {
        return configFile.getValue(path);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void addEntry(String path, Object value) {
        configFile.addEntry(path, value);
    }

    @Override
    public void setEntry(String path, Object value) {
        configFile.setEntry(path, value);
    }

    @Override
    public boolean isSection(String path) {
        return configFile.isSection(path);
    }

}
