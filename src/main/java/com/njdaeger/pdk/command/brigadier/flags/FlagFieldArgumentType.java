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
import com.njdaeger.pdk.utils.Pair;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FlagFieldArgumentType extends BasePdkArgumentType<FlagMap, String> {

    private static final DynamicCommandExceptionType UNKNOWN_FLAG = new DynamicCommandExceptionType(o -> () -> "Unknown flag: " + o.toString());
    private static final DynamicCommandExceptionType MISSING_ARGUMENT = new DynamicCommandExceptionType(o -> () -> "Usage for flag " + o.toString() + " is -" + o + " <value>");

    private final List<IPdkCommandFlag<?>> flags;

    public FlagFieldArgumentType(List<IPdkCommandFlag<?>> flags) {
        this.flags = flags;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

        var input = builder.getRemaining();
        var splitInput = builder.getRemaining().trim().split(" ");

        var currentWord = splitInput[splitInput.length - 1];
        var previousWord = splitInput.length > 1 ? splitInput[splitInput.length - 2] : null;
        var currentFlag = getFlag(currentWord);
        var previousFlag = previousWord != null ? getFlag(previousWord) : null;

        var additionalOffset = input.endsWith(" ") ? 0 : currentWord.length();

        if ((currentFlag == null || currentFlag.isBooleanFlag()) && (previousFlag == null || previousFlag.isBooleanFlag()) || (currentFlag == null && !previousFlag.isBooleanFlag() && input.endsWith(" "))) {
            var unusedFlags = getUnusedFlags(context).stream().map(flag -> Pair.of("-" + flag.getName(), flag.getTooltipAsMessage())).toList();
            var offset = builder.getStart() + input.length() - additionalOffset;
            var newBuilder = builder.createOffset(offset);
            unusedFlags.forEach(flag -> newBuilder.suggest(flag.getFirst(), flag.getSecond()));
            return newBuilder.buildFuture();
        }

        var newBuilder = builder.createOffset(builder.getStart() + input.length() - additionalOffset);
        var flagToComplete = currentFlag != null ? currentFlag : previousFlag;
        return flagToComplete.getType().listSuggestions(context, newBuilder);
    }

    @Override
    public FlagMap convertToCustom(CommandSender sender, String nativeType, StringReader reader) throws CommandSyntaxException {
        return parse(reader, sender);
    }

    @Override
    public @NotNull <S> FlagMap parse(@NotNull StringReader reader, S sender) throws CommandSyntaxException {
        var map = new FlagMap();
        while (reader.canRead()) {
            reader.skipWhitespace();
            if (reader.peek() != '-') {
                break;
            } else {
                reader.skip();
                var flagName = readFlagName(reader);
                var flag = getFlag("-" + flagName);

                if (flag == null) {
                    var flagNameLength = flagName.length();
                    reader.setCursor(reader.getCursor() - flagNameLength - 1);
                    throw UNKNOWN_FLAG.createWithContext(reader, flagName);
                }

                reader.skipWhitespace();

                if (!flag.isBooleanFlag()) {
                    if (!reader.canRead()) {
                        var flagNameLength = flagName.length();
                        reader.setCursor(reader.getCursor() - flagNameLength - 1);
                        throw MISSING_ARGUMENT.createWithContext(reader, flag.getName());
                    }
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
        if (!flagName.startsWith("-")) return null;
        flagName = flagName.substring(1);
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

        return unusedFlags.stream().filter(f -> !f.isHidden()).toList();
    }
}
