package com.njdaeger.pdk.config;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Provides an easy, read-only, configuration for the
 * @param <C>
 */
public class SmartConfig<C extends IConfig> {

    private IConfig config;
    private final String path;
    private final Plugin plugin;
    private final ConfigType<C> type;
    private final Map<String, Object> configKeys;

    public SmartConfig(Plugin plugin, ConfigType<C> type, String path) {
        this.config = type.createNew(plugin, path);
        this.configKeys = new HashMap<>();
        this.type = type;
        this.plugin = plugin;
        this.path = path;
    }

    public void reload() {
        configKeys.clear();
        this.config = type.createNew(plugin, path);
    }

    protected <T> T get(String key, T defVal, Predicate<T> restrict) {
        T val;
        if (!configKeys.containsKey(key)) {
            T entry = (T) config.getValueAs(path, defVal.getClass());
            configKeys.put(key, val = (restrict.test(entry) ? entry : defVal));
        } else val = (T) configKeys.get(key);
        return val == null ? defVal : val;
    }

    protected <T> T get(String key, T defVal) {
        T val;
        if (!configKeys.containsKey(key)) {
            T entry = (T) config.getValueAs(path, defVal.getClass());
            configKeys.put(key, val = entry);
        } else val = (T) configKeys.get(key);
        return val == null ? defVal : val;
    }

}

