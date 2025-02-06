package com.njdaeger.pdk.command.brigadier.nodes;

import com.njdaeger.pdk.command.brigadier.flags.IPdkCommandFlag;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IPdkRootNode extends IPdkCommandNode {

    /**
     * Gets the names of the command. [0] is the primary name.
     *
     * @return The names of the command.
     */
    @NotNull
    List<String> getAliases();

    /**
     * Gets the flags of the command.
     * @return The flags of the command.
     */
    @NotNull
    List<IPdkCommandFlag<?>> getFlags();

    /**
     * Gets the description of the command.
     * @return The description of the command.
     */
    @Nullable
    String getDescription();

    /**
     * Registers the command with the plugin.
     * @param plugin The plugin to register the command with.
     */
    void register(Plugin plugin);
}
