package com.njdaeger.pdk.command.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.njdaeger.pdk.command.brigadier.flags.FlagMap;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class CommandContextImpl implements ICommandContext {

    private final CommandContext<CommandSourceStack> baseContext;
    private final Map<String, Object> argumentMapping;
    private final FlagMap flagMap;

    public CommandContextImpl(CommandContext<CommandSourceStack> baseContext) {
        this.baseContext = baseContext;
        try {
            var decfield = baseContext.getClass().getDeclaredField("arguments");
            decfield.setAccessible(true);
            var args = (Map<String, ParsedArgument<CommandSourceStack, ?>>) decfield.get(baseContext);
            argumentMapping = args.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getResult()));
        } catch (Exception e) {
            throw new RuntimeException("There was an error getting the arguments from the command context.", e);
        }
        flagMap = hasTypedAs("flags", FlagMap.class) ? getTyped("flags", FlagMap.class) : new FlagMap();
    }

    @Override
    public boolean hasFlag(String flag) {
        return flagMap.hasFlag(flag);
    }

    @Override
    public <T> @Nullable T getFlag(String flag) {
        return flagMap.getFlag(flag);
    }

    @Override
    public <T> @Nullable T getFlag(String flag, T defaultValue) {
        return flagMap.getFlag(flag, defaultValue);
    }

    @Override
    public @NotNull CommandSender getSender() {
        return baseContext.getSource().getSender();
    }

    @Override
    public @NotNull String getRawCommandString() {
        return baseContext.getInput();
    }

    @Override
    public @NotNull String[] getArgs() {
        return baseContext.getInput().split(" ");
    }

    @Override
    public boolean hasTyped(String argName) {
        return argumentMapping.containsKey(argName);
    }

    @Override
    public Object getTyped(String argName) {
        return argumentMapping.get(argName);
    }

    @Override
    public <T> @Nullable T getTyped(String argName, Class<T> type) {
        try {
            return baseContext.getArgument(argName, type);
        } catch (Exception e) {
            return null;
        }
    }
}
