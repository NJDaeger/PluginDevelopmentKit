package com.njdaeger.pdk.config.impl;

import com.njdaeger.pdk.config.ConfigType;
import com.njdaeger.pdk.config.IConfig;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

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

public class YmlConfig implements IConfig {

    private final Map<String, String> comments;
    private YamlConfiguration config;
    private final String configName;
    private final Plugin plugin;
    private final File file;

    public YmlConfig(Plugin plugin, String configName) {
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
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public String getName() {
        return configName;
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
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void addEntry(String path, Object value) {
        if (getValue(path) == null) config.set(path, value);
    }

    @Override
    public void addComment(String path, String comment) {
        comments.put(path, comment);
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
    public ConfigType<?> getType() {
        return ConfigType.YML;
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
            config.options().copyHeader(false);
            config.save(file);

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
        } catch (IOException e) {
            e.printStackTrace();
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
                    comments.add(createTab(depth) + "# " + line.substring(lastCut, (lastCut + 118 - depth) > line.length() ? line.length() : (lastCut + 118 - depth)));
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

}
