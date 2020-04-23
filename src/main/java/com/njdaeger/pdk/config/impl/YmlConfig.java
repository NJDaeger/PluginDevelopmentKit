package com.njdaeger.pdk.config.impl;

import com.njdaeger.pdk.config.ConfigType;
import com.njdaeger.pdk.config.Configuration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class YmlConfig extends Configuration<YmlConfig> {

    private YamlConfiguration config;
    private final File file;

    public YmlConfig(Plugin plugin, String configName) {
        super(plugin, ConfigType.YML, configName);

        File path;
        if (!configName.contains(File.separator)) {
            path = plugin.getDataFolder().getAbsoluteFile();
            this.file = new File(path.getAbsolutePath() + File.separator + configName + ".yml");
        }
        else {
            int last = configName.lastIndexOf(File.separator);
            String fileName = configName.substring(last + 1);
            String directory = configName.substring(0, last);
            path = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + directory);
            this.file = new File(path + File.separator + fileName + ".yml");
        }
        if (!path.exists()) path.mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);

    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    @Override
    public Object getValue(String path) {
        return config.get(path);
    }

    @Override
    public void addEntry(String path, Object value) {
        if (getValue(path) == null) config.set(path, value);
    }

    @Override
    public void setEntry(String path, Object value) {
        config.set(path, value);
    }

    @Override
    public boolean isSection(String path) {
        return getValue(path) != null && getValue(path) instanceof MemorySection;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
