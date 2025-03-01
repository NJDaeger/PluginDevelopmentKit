package com.njdaeger.pdk.command.brigadier.flags;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.arguments.BasePdkArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FlagFieldArgumentType extends BasePdkArgumentType<FlagMap, String> {

    private static final DynamicCommandExceptionType UNKNOWN_FLAG = new DynamicCommandExceptionType(o -> () -> "Unknown flag: " + o.toString());
    private static final DynamicCommandExceptionType MISSING_ARGUMENT = new DynamicCommandExceptionType(o -> () -> "Usage for flag " + o.toString() + " is -" + o + " <value>");

    private final List<IPdkCommandFlag<?>> flags;

    public FlagFieldArgumentType(List<IPdkCommandFlag<?>> flags) {
        this.flags = flags;
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var input = builder.getRemaining().trim();
        var splitInput = input.split(" ");
        var currentFlagIndex = -1;
        IPdkCommandFlag<?> currentFlag = null;
        for (var i = splitInput.length - 1; i >= 0; i--) {
            var foundFlag = getFlag(splitInput[i]);
            if (foundFlag != null) {
                currentFlagIndex = i;
                currentFlag = foundFlag;
                break;
            }
        }

        if (currentFlag == null || currentFlag.isBooleanFlag() || (currentFlagIndex < splitInput.length - 1 && builder.getRemaining().endsWith(" "))) {
            var newBuilder = builder.createOffset(builder.getStart() + builder.getRemaining().length());
            var currentWord = splitInput[splitInput.length - 1];
            getUnusedFlags(context).forEach(flag -> newBuilder.suggest((currentWord.startsWith("-") ? "" : "-") + flag.getName(), flag.getTooltipAsMessage()));
            return newBuilder.buildFuture();
        }

        var valueStart = input.indexOf('-' + currentFlag.getName());
        while (valueStart < input.length() && Character.isWhitespace(input.charAt(valueStart))) valueStart++;

        var newBuilder = builder.createOffset(builder.getStart() + valueStart + currentFlag.getName().length() + 2);
        return currentFlag.getType().listSuggestions(context, newBuilder);
    }

    @Override
    public FlagMap convertToCustom(String nativeType, StringReader reader) throws CommandSyntaxException {
        return parse(reader);
    }

    @Override
    public @NotNull FlagMap parse(@NotNull StringReader reader) throws CommandSyntaxException {
        var map = new FlagMap();
        while (reader.canRead()) {
            reader.skipWhitespace();
            if (reader.peek() != '-') {
                break;
            } else {
                reader.skip();
                var flagName = readFlagName(reader);
                var flag = getFlag(flagName);

                if (flag == null) throw UNKNOWN_FLAG.createWithContext(reader, flagName);

                reader.skipWhitespace();

                if (!flag.isBooleanFlag()) {
                    if (!reader.canRead()) throw MISSING_ARGUMENT.createWithContext(reader, flag.getName());
                    var flagValue = flag.getType().parse(reader);
                    map.setFlag(flag.getName(), flagValue);
                }
                else map.setFlag(flag.getName(), true);
                reader.skipWhitespace();
            }
        }
        return map;
    }

    @Override
    public String convertToNative(FlagMap aBoolean) {
        throw new UnsupportedOperationException("FlagFieldArgumentType does not support converting to native.");
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }

    private IPdkCommandFlag<?> getFlag(String flagName) {
        flagName = flagName.startsWith("-") ? flagName.substring(1) : flagName;
        for (var flag : flags) {
            if (flag.getName().equalsIgnoreCase(flagName)) {
                return flag;
            }
        }
        return null;
    }

    private String readFlagName(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }

    private <S> List<IPdkCommandFlag<?>> getUnusedFlags(CommandContext<S> context) {
        var splitArgs = context.getInput().split(" ");
        var unusedFlags = new ArrayList<>(flags);
        for (var arg : splitArgs) {
            var flg = getFlag(arg);
            if (flg != null) unusedFlags.remove(flg);
        }

        return unusedFlags;
    }
}
