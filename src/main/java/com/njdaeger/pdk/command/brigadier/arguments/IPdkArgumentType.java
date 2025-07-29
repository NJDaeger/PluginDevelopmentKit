package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.IContextGenerator;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface IPdkArgumentType<CUSTOM, NATIVE> extends CustomArgumentType<CUSTOM, NATIVE> {

    /**
     * Returns the plugin that registered the command being used
     * @return The plugin that registered the command.
     */
    Plugin getPlugin();

    /**
     * Sets the context generator supplier
     * @param plugin The plugin that registered the command.
     * @param generator The context generator to use for this argument type.
     */
    void setContextGenerator(Plugin plugin, IContextGenerator<?> generator);

    /**
     * Returns a list of basic suggestions. All tooltips will be the default tooltip message provided by {@link #getDefaultTooltipMessage()}.
     * @param commandContext The command context.
     * @return A list of basic suggestions.
     */
    List<CUSTOM> listBasicSuggestions(ICommandContext commandContext);

    /**
     * Returns a map of a suggestion and a tooltip message.
     * @param commandContext The command context.
     * @return A map of suggestions and their tooltip messages.
     */
    Map<CUSTOM, Message> listSuggestions(ICommandContext commandContext);

    /**
     * Returns the default tooltip message for this argument type.
     * @return The default tooltip message.
     */
    Message getDefaultTooltipMessage();

    /**
     * Converts the custom argument type to the native argument type. This is used when creating completions, it will convert an argument on the fly to the native type.
     * @param custom The custom argument type.
     * @return The native argument type.
     */
    NATIVE convertToNative(CUSTOM custom);

    /**
     * Converts the native argument type to the custom argument type for command execution.
     *
     * @param source The command sender. Note: This may be null in some instances.
     * @param nativeType The native argument type.
     * @param reader The stringreader used to parse the command.
     * @return The custom argument type.
     * @throws CommandSyntaxException If the conversion from the native to the custom fails.
     */
    CUSTOM convertToCustom(@Nullable CommandSender source, NATIVE nativeType, StringReader reader) throws CommandSyntaxException;

    @Override
    default <S> CUSTOM parse(StringReader reader, S source) throws CommandSyntaxException {
        return this.convertToCustom(((CommandSourceStack)source).getSender(), getNativeType().parse(reader), reader);
    }

    @Override
    default @NotNull CUSTOM parse(@NotNull StringReader reader) throws CommandSyntaxException {
        return convertToCustom(null, getNativeType().parse(reader), reader);
    }
}
