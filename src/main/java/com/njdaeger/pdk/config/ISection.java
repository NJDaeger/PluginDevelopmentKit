package com.njdaeger.pdk.config;

import com.njdaeger.pdk.config.impl.Section;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ISection {

    /**
     * Gets a set of keys that are in this section.
     *
     * @param deep Whether to get the keys of the keys (and so on) in this section.
     * @return A Set of Keys.
     */
    default Set<String> getKeys(boolean deep) {

        Set<String> keys = getConfig().getKeys(true).stream().filter(k -> k.startsWith(getCurrentPath()) && k.contains(".")).collect(Collectors.toSet());
        if (deep) {
            return keys;
        } else {
            return keys.stream().map(k -> k.replaceFirst(getCurrentPath() + "\\.", "")).map(k -> k.substring(0, (!k.contains(".") ? k.length() : k.indexOf(".")))).collect(Collectors.toSet());
        }
    }

    /**
     * Gets a section from within this section.
     *
     * @param path The path from the start of this section. <p>Note: dont use the string provided originally to get this current section</p>
     * @return A section of the configuration.
     */
    default ISection getSection(String path) {
        String base = getCurrentPath().isEmpty() ? getCurrentPath() : getCurrentPath() + ".";
        if (!isSection(path)) {
            return null;
        }
        else return new Section(base + path, getConfig());
    }

    /**
     * Gets a list of sections from a section specified. Section specified must be contained within the current section
     *
     * @param name The path to get a list of sections from.
     * @return A list of sections from a config section
     */
    default List<ISection> getSections(final String name) {
        if (!hasSection(name)) return null;
        ISection s = getSection(name);
        return s.getKeys(false).stream().filter(s::hasSection).map(s::getSection).collect(Collectors.toList());
    }

    /**
     * Gets a list of sections currently in this section
     *
     * @return A list of sections from a config section
     */
    default List<ISection> getSections() {
        return getKeys(false).stream().filter(this::hasSection).map(this::getSection).collect(Collectors.toList());
    }

    /**
     * Checks if this section contains the given section
     * @param path The section to look for
     * @return True if the section exists, false otherwise.
     */
    default boolean hasSection(String path) {
        return getSection((getCurrentPath().isEmpty() ? path : getCurrentPath() + "." + path)) != null;
    }

    /**
     * Check if the specified path is a section. This is relative to the current section.
     * If you are in section <strong>examplePath</strong> and this section contains other sections named <strong>first</strong>
     * (path being examplePath.first), <strong>second</strong> (path being examplePath.second), and <strong>third</strong>
     * (path being examplePath.third), you can just call <code>isSection("first")</code> rather than putting the
     * current path within the input string.
     *
     * @param path The path to check.
     * @return True if the path is a section, false otherwise.
     */
    default boolean isSection(String path) {
        return getConfig().isSection((getCurrentPath().isEmpty() ? path : getCurrentPath() + "." + path));
    }

    /**
     * Gets a value from a config entry.
     *
     * @param path The path in the configuration.
     * @return A string from the specified path.
     */
    default String getString(String path) {
        return getValueAs(path, String.class);
    }

    /**
     * Gets a value from a config entry.
     *
     * @param path The path in the configuration.
     * @return A double from the specified path.
     */
    default double getDouble(String path) {
        return getValueAs(path, Double.class);
    }

    /**
     * Gets a value from a config entry.
     *
     * @param path The path in the configuration.
     * @return An integer from the specified path.
     */
    default int getInt(String path) {
        return getValueAs(path, Integer.class);
    }

    /**
     * Gets a value from a config entry.
     *
     * @param path The path in the configuration.
     * @return A long from the specified path.
     */
    default long getLong(String path) {
        return getValueAs(path, Long.class);
    }

    /**
     * Gets a value from a config entry.
     *
     * @param path The path in the configuration.
     * @return A boolean from the specified path.
     */
    default boolean getBoolean(String path) {
        return getValueAs(path, Boolean.class);
    }

    /**
     * Gets a value from a config entry.
     *
     * @param path The path in the configuration.
     * @return A list from the specified path.
     */
    default List<?> getList(String path) {
        Object v = getValue(path);
        return v instanceof List ? (List<?>)v : null;
    }

    /**
     * Gets a value from a config entry.
     *
     * @param path The path in the configuration.
     * @return A flat from the specified path.
     */
    default float getFloat(String path) {
        return getValueAs(path, Float.class);
    }

    /**
     * Gets a specific type of list.
     *
     * @param path The path to the list
     * @param type The type of list to return
     * @param <E>  The list type
     * @return The list
     */
    @SuppressWarnings( {"unchecked", "unused"} )
    default <E> List<E> getList(String path, Class<E> type) {
        List<?> list = getList(path);

        if (list == null) {
            return new ArrayList<>();
        }

        List<E> r = new ArrayList<>();

        for (Object o : list) {
            if (type.isInstance(o)) r.add((E)o);
        }

        return r;
    }

    /**
     * Gets a List of Strings from a path in this config.
     *
     * @param path The path to get the strings from.
     * @return A list from the specified path.
     */
    default List<String> getStringList(String path) {
        return getList(path, String.class);
    }

    /**
     * Gets all the keys which have the matching value
     * @param value The value needing to be matched
     * @return The entries found in this search
     */
    default Collection<String> getKeysFromValue(Object value) {
        return getKeys(true).stream().filter(k -> getValue(k).equals(value)).collect(Collectors.toList());
    }

    /**
     * Gets a value from a config entry.
     *
     * @param path The path in the configuration relative to where you currently are in the configuration.
     * @return An object from the specified path.
     */
    default Object getValue(String path) {
        String base = getCurrentPath().isEmpty() ? getCurrentPath() : getCurrentPath() + ".";
        return getConfig().getValue(base + path);
    }

    /**
     * Check whether a section contains a path or a value to the given path. This is
     * equivalent to running ISection{@link #contains(String, boolean)} )} with exact
     * being set to false
     *
     * @param path The path to look for
     * @return True if the path exists, false otherwise.
     */
    default boolean contains(String path) {
        return contains(path, false);
    }

    /**
     * The name of the section.
     *
     * @return The name of the section.
     */
    default String getName() {
        return getCurrentPath().substring(!getCurrentPath().contains(".") ? 0 : getCurrentPath().lastIndexOf(".") + 1);
    }

    /**
     * Get the entry provided as a certain type.
     * @param path Path to the entry
     * @param type The type class wanting to be returned
     * @param <T> The return type
     * @return Null if the entry doesn't exist, the entry as the provided type otherwise.
     */
    @SuppressWarnings( "unchecked" )
    default <T> T getValueAs(String path, Class<T> type) {
        if (getValue(path) == null) return null;

        try {
            if (type.equals(Long.class)) {
                return (T)Long.valueOf(getValue(path).toString());
            }
            if (type.equals(Byte.class)) {
                return (T)Byte.valueOf(getValue(path).toString());
            }
            if (type.equals(Float.class)) {
                return (T)Float.valueOf(getValue(path).toString());
            }
            if (type.equals(Short.class)) {
                return (T)Short.valueOf(getValue(path).toString());
            }
            if (type.equals(Double.class)) {
                return (T)Double.valueOf(getValue(path).toString());
            }
            if (type.equals(Integer.class)) {
                return (T)Integer.valueOf(getValue(path).toString());
            }
            if (type.equals(Boolean.class)) {
                return (T)Boolean.valueOf(getValue(path).toString());
            }
        } catch (NumberFormatException e) {
            getPlugin().getLogger().warning("Could not parse the value of '" + path + getValue(path).toString() + "'. Value is not applicable to '" + type.getSimpleName() + "'.");
            return null;
        }
        if (type.isEnum()) {
            try {
                return (T)Enum.valueOf(type.asSubclass(Enum.class), getValue(path).toString().toUpperCase());
            }
            catch (IllegalArgumentException | NullPointerException e) {
                getPlugin().getLogger().warning("Could not parse the value of '" + path + getValue(path).toString() + "'. Value is not a constant in the enum '" + type.getSimpleName() + "'.");
                return null;
            }
        }
        return ((T)getValue(path));
    }

    /**
     * Checks if this configuration contains a specified path relative to the current section.
     *
     * @param path  The path to look for
     * @param exact If true, the path must lead to an entry. If false, the path can be either an entry or a section.
     * @return True if the path exists.
     */
    default boolean contains(String path, boolean exact) {
        if (exact && !isSection(path)) return getValue(path) != null;
        else return isSection(path) || getValue(path) != null;
    }

    /**
     * Gets the parent section to the current section.
     *
     * @return The previous section, returns null if no previous section exists.
     */
    default ISection getParent() {
        if (!getCurrentPath().contains(".")) return null;
        else return new Section(getCurrentPath().substring(0, getCurrentPath().lastIndexOf(".")), getConfig());
    }

    /**
     * Get the current configuration type
     * @return The configuration type
     */
    default ConfigType getType() {
        return getConfig().getType();
    }

    /**
     * Gets the host plugin.
     *
     * @return The host plugin.
     */
    default Plugin getPlugin() {
        return getConfig().getPlugin();
    }

    /**
     * Adds a new entry to the current config relative to the current section.
     *
     * @param path  The path in the config.
     * @param value The value to set this entry to.
     */
    default void addEntry(String path, Object value) {
        getConfig().addEntry((getCurrentPath().isEmpty() ? path : getCurrentPath() + "." + path), value);
    }

    /**
     * Adds a new entry to the current config. If the
     * config already has a value at the path location
     * it will be updated with the new value supplied
     * from this method.
     *
     * @param path  The path in the config.
     * @param value The value to set the path to.
     */
    default void setEntry(String path, Object value) {
        getConfig().setEntry((getCurrentPath().isEmpty() ? path : getCurrentPath() + "." + path), value);
    }

    /**
     * Adds a comment on top of a specific section in the configuration.
     * @param path The path to the section the comment is on
     * @param comment The comment to set.
     */
    default void addComment(String path, String comment) {
        getConfig().addComment(getCurrentPath().isEmpty() ? path : getCurrentPath() + "." + path, comment);
    }

    /**
     * Gets the current path of the configuration section.
     *
     * @return The current path
     */
    String getCurrentPath();

    /**
     * Gets the base configuration of this section.
     *
     * @return The current configuration
     */
    IConfig getConfig();

}
