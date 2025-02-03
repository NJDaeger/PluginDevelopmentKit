package com.njdaeger.pdk.command.brigadier.impl;

import com.mojang.brigadier.context.CommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class CommandContextImpl implements ICommandContext {

    private final CommandContext<CommandSourceStack> baseContext;

    public CommandContextImpl(CommandContext<CommandSourceStack> baseContext) {
        this.baseContext = baseContext;
    }

    /*private String[] getArgsWithNoFlags() {
        var noFlagArgs = new ArrayList<String>();
        var splitArgs = getRawCommandArgs();
        var isCurrentAFlagValue = false;
        for (var i = 0; i < splitArgs.length; i++) {
            var cur = splitArgs[i];
            if (cur.startsWith("-")) {
                var flagOpt = commandRoot.getFlags().stream().filter(flag -> flag.getName().equals(cur.substring(1))).findFirst();
                if (flagOpt.isPresent() && !flagOpt.get().isBooleanFlag()) {
                    isCurrentAFlagValue = true;
                }
                i++;
                continue;
            } else if (isCurrentAFlagValue) {
                isCurrentAFlagValue = false;
                i++;
                continue;
            }
            noFlagArgs.add(splitArgs[i]);
        }
        return noFlagArgs.toArray(new String[0]);
    }

    @Override
    public boolean hasFlag(String flag) {
        return false;
    }

    @Override
    public <T> @Nullable T getFlag(String flag) {
        return null;
    }

    @Override
    public <T> @Nullable T getFlagOrDefault(String flag, T defaultValue) {
        return defaultValue;
    }*/

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
    public @Nullable String getTyped(String argName) {
        return getTyped(argName, String.class);
    }

    @Override
    public <T> @Nullable T getTyped(String argName, Class<T> type) {
        try {
            return baseContext.getArgument(argName, type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
