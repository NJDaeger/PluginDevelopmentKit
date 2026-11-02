package com.njdaeger.pdk.config;

import com.njdaeger.pdk.config.impl.YmlConfig;
import com.njdaeger.pdk.config.impl.YmlConfigurate;
import org.bukkit.plugin.Plugin;

import java.util.function.BiFunction;

public final class ConfigType<T extends IConfig> {

    public static final ConfigType<YmlConfig> YML = new ConfigType<>(YmlConfig.class, YmlConfig::new);
    public static final ConfigType<YmlConfigurate> YML_CONFIGURATE = new ConfigType<>(YmlConfigurate.class, YmlConfigurate::new);


    private final Class<T> type;
    private final BiFunction<Plugin, String, T> createNew;

    public ConfigType(Class<T> configType, BiFunction<Plugin, String, T> createNew) {
        this.type = configType;
        this.createNew = createNew;
    }

    public Class<T> getConfigType() {
        return type;
    }

    public IConfig createNew(Plugin plugin, String path) {
        return createNew.apply(plugin, path);
    }

}
