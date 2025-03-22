package com.njdaeger.pdk.command.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.njdaeger.pdk.command.brigadier.flags.FlagMap;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class CommandContextImpl implements ICommandContext {

    private final CommandContext<CommandSourceStack> baseContext;
    private final Map<String, Object> argumentMapping;
    private final Map<Integer, String> argumentIndexMapping;
    private final FlagMap flagMap;

    public CommandContextImpl(CommandContext<CommandSourceStack> baseContext) {
        this.baseContext = baseContext;
        this.argumentIndexMapping = new HashMap<>();
        try {
            var decfield = baseContext.getClass().getDeclaredField("arguments");
            decfield.setAccessible(true);
            var args = (Map<String, ParsedArgument<CommandSourceStack, ?>>) decfield.get(baseContext);
            argumentMapping = args.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getResult()));

            var input = baseContext.getInput().trim();
            //we want to trim out all the parser arguments from the input string and replace them with
            int startIndex = input.indexOf(' ');
            if (startIndex != -1) {
                var argList = new ArrayList<String>();
                var parsedMap = args.values().stream().collect(Collectors.toMap(p -> p.getRange().getStart(), p -> p));
                var builder = new StringBuilder();
                for (var i = startIndex; i < input.length(); i++) {
                    if (parsedMap.containsKey(i)) {
                        var parsed = parsedMap.get(i);
                        argList.add(parsed.getRange().toString());
                        i = parsed.getRange().getEnd() - 1; //skip to the end of the parsed argument
                        builder = new StringBuilder(); //reset the builder
                    } else if (input.charAt(i) != ' ') {
                        builder.append(input.charAt(i));
                    } else {
                        if (!builder.isEmpty()) {
                            argList.add(builder.toString());
                            builder = new StringBuilder();
                        }
                    }
                }
                for (var i = 0; i < argList.size(); i++) {
                    argumentIndexMapping.put(i, argList.get(i));
                }
            }

            flagMap = hasTypedAs("flags", FlagMap.class) ? getTyped("flags", FlagMap.class) : new FlagMap();
        } catch (Exception e) {
            throw new RuntimeException("There was an error getting the arguments from the command context.", e);
        }

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
        return argumentIndexMapping.values().toArray(new String[0]);
    }

    @Override
    public boolean hasTyped(String argName) {
        if (argName == null) throw new IllegalArgumentException("The argument name cannot be null.");
        return argumentMapping.containsKey(argName);
    }

    @Override
    public @Nullable Object getTyped(String argName) {
        if (argName == null) throw new IllegalArgumentException("The argument name cannot be null.");
        return argumentMapping.get(argName);
    }

    @Override
    public <T> @NotNull T getTyped(String argName, Class<T> type) {
        if (argName == null) throw new IllegalArgumentException("The argument name cannot be null.");
        if (type == null) throw new IllegalArgumentException("The argument type cannot be null.");
        try {
            return type.cast(argumentMapping.get(argName));
        } catch (Exception e) {
            throw new IllegalArgumentException("There was an error getting the argument from the command context. Do you have the argument set to the correct argument type in your command definition?", e);
        }
    }
}
