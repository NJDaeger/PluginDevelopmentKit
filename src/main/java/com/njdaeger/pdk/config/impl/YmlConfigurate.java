package com.njdaeger.pdk.config.impl;

import com.njdaeger.pdk.config.ConfigType;
import com.njdaeger.pdk.config.IConfig;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class YmlConfigurate implements IConfig {

    private final Map<String, String> comments;
    private final YamlConfigurationLoader loader;
    private ConfigurationNode config;
    private final String configName;
    private final Plugin plugin;
    private final File file;

    public YmlConfigurate(Plugin plugin, String configName) {
        this.comments = new HashMap<>();
        this.configName = configName;
        this.plugin = plugin;

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

        this.loader = YamlConfigurationLoader.builder().path(file.toPath()).build();
        try {
            this.config = loader.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String getName() {
        return configName;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        if (deep) {
            // For deep traversal, we need to collect all keys including nested ones
            return getKeysRecursively("", config);
        } else {
            // For shallow traversal, just get the direct children's keys
            return config.childrenMap().keySet().stream()
                .map(Object::toString)
                .collect(java.util.stream.Collectors.toSet());
        }
    }

    /**
     * Helper method to recursively collect all keys in the configuration.
     *
     * @param prefix The path prefix to prepend to keys
     * @param node The node to collect keys from
     * @return A set containing all keys in this node and its children
     */
    private Set<String> getKeysRecursively(String prefix, ConfigurationNode node) {
        Set<String> keys = new java.util.HashSet<>();

        node.childrenMap().forEach((key, childNode) -> {
            String keyString = key.toString();
            String fullPath = prefix.isEmpty() ? keyString : prefix + "." + keyString;

            // Add this key
            keys.add(fullPath);

            // Add all children's keys
            if (!childNode.isMap()) {
                return;
            }

            keys.addAll(getKeysRecursively(fullPath, childNode));
        });

        return keys;
    }

    @Override
    public Object getValue(String path) {
        return config.node(getPathArray(path)).raw();
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void addEntry(String path, Object value) {
        if (!config.hasChild(getPathArray(path))) {
            try {
                config.node(getPathArray(path)).set(value);
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void addComment(String path, String comment) {
        if (comment != null && !comment.isEmpty()) {
            comments.put(path, comment);
        }
    }

    @Override
    public void setEntry(String path, Object value) {
        try {
            config.node(getPathArray(path)).set(value);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSection(String path) {
        Object value = config.node(getPathArray(path)).raw();
        return value instanceof ConfigurationNode v && v.isMap();
    }

    @Override
    public ConfigType<?> getType() {
        return ConfigType.YML;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void reload() {
        try {
            this.config = loader.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save() {
        try {
            loader.save(config);

            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            comments.forEach((key, comment) -> {
                int depth = 0;
                int lastReadLine = 0;
                String[] path = key.split("\\.");
                for (int i = 0; i < path.length; i++) {//Iterate thru each part in the key
                    for (String line : lines.subList(lastReadLine, lines.size())) {
                        if (!line.startsWith("#") && line.startsWith(createTab(depth) + path[i] + ":")) {
                            if (i == path.length-1) {
                                lines.addAll(lastReadLine, splitComment(depth, comment));
                            }
                            depth += 2;
                            break;
                        }
                        lastReadLine++;
                    }
                }

            });
            Files.write(file.toPath(), lines);

        } catch (ConfigurateException e) {
            throw new RuntimeException("Failed to save configuration to file: " + file.getAbsolutePath(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> splitComment(int depth, String comment) {
        //The +2 comes from the "# " before the comment.
        List<String> comments = new ArrayList<>();
        String[] lines = comment.split(Pattern.compile("\n", Pattern.LITERAL).pattern());
        for (String line : lines) {

            if (line.length() + depth + 2 > 120) {
                int lastCut = 0;
                while (lastCut < line.length()) {
                    comments.add(createTab(depth) + "# " + line.substring(lastCut, Math.min((lastCut + 118 - depth), line.length())));
                    lastCut += (118 - depth);
                }
            } else comments.add(createTab(depth) + "# " + line);

        }
        return comments;
    }

    private static String createTab(int depth) {
        StringBuilder builder = new StringBuilder();
        while (depth-->0) builder.append(" ");
        return builder.toString();
    }

    private Object[] getPathArray(String path) {
        return path.split("\\.");
    }
}
