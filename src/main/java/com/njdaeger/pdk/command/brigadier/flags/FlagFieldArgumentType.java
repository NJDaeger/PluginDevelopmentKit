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
        var input = builder.getRemaining();
        var splitInput = input.split(" ");
        var currentWord = splitInput[splitInput.length - 1];
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

        System.out.println("Current flag: " + currentFlag);
        System.out.println("Current flag index: " + currentFlagIndex);
        System.out.println("Current word: " + currentWord);
        System.out.println("Input: '" + input + "'");
        System.out.println("Remaining: '" + builder.getRemaining() + "'");

        if (
                currentFlag == null //if there is no current flag
                        || currentFlag.isBooleanFlag() //or the current flag is a boolean flag
                        || (builder.getRemaining().endsWith(" ") && currentFlagIndex < splitInput.length - 1) //or the current flag already has its value written
                        || (currentWord.startsWith("-") && !builder.getRemaining().endsWith(" ")) //or the current word starts with a dash
        ) {
            var unusedFlags = getUnusedFlags(context);
            var startsWithDash = currentWord.startsWith("-");
            var currentWordWithoutDash = startsWithDash ? currentWord.substring(1) : currentWord;
            var offset = builder.getStart() + builder.getRemaining().length();
            //attempt to find flags that start with the current word
            var possibleFlagSuggestions = unusedFlags.stream()
                    .filter(flag -> flag.getName().toLowerCase().startsWith(currentWordWithoutDash.toLowerCase()))
                    .map(flag -> Pair.of((startsWithDash ? "" : "-") + flag.getName().substring(currentWordWithoutDash.length()), flag.getTooltipAsMessage()))
                    .toList();
            //if the current word doesnt start with a dash and the current word isnt a value for a flag, start the offset at the beginning of the current word
            System.out.println("StartsWithDash: " + startsWithDash);
            System.out.println("CurrentFlagIndex: " + currentFlagIndex);
            System.out.println("SplitInput.length - 1: " + (splitInput.length - 1));
            if (!startsWithDash && splitInput.length - 1 == currentFlagIndex) offset -= currentWord.length();

            //if none of those, attempt to find flags that contain the current word
            if (possibleFlagSuggestions.isEmpty()) {
                System.out.println("No flags start with the current word '" + currentWord + "'");
                possibleFlagSuggestions = unusedFlags.stream()
                        .filter(flag -> flag.getName().toLowerCase().contains(currentWordWithoutDash.toLowerCase()))
                        .map(flag -> Pair.of("-" + flag.getName(), flag.getTooltipAsMessage()))
                        .toList();
                //start the offset at the beginning of the current word
                if (!possibleFlagSuggestions.isEmpty()) offset -= currentWord.length();
            }
            if (possibleFlagSuggestions.isEmpty()) {
                System.out.println("No flags contain the current word '" + currentWord + "'");
                possibleFlagSuggestions = unusedFlags.stream()
                        .map(flag -> Pair.of("-" + flag.getName(), flag.getTooltipAsMessage()))
                        .toList();

                if (!possibleFlagSuggestions.isEmpty() && startsWithDash) offset -= currentWord.length();
            }
            var newBuilder = builder.createOffset(offset);
            possibleFlagSuggestions.forEach(flag -> newBuilder.suggest(flag.getFirst(), flag.getSecond()));
            return newBuilder.buildFuture();
        }

        var valueStart = input.indexOf('-' + currentFlag.getName());
        System.out.println("Value start: " + valueStart);
        while (valueStart < input.length() && Character.isWhitespace(input.charAt(valueStart))) valueStart++;

        var offset = builder.getStart() + valueStart + currentFlag.getName().length();
        System.out.println("Offset: " + offset);
        var newBuilder = builder.createOffset(builder.getStart() + valueStart + currentWord.length() + 1);
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
