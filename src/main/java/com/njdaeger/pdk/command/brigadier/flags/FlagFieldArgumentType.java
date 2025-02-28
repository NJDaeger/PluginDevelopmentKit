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
import com.njdaeger.pdk.command.flag.Flag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
        var splitInput = builder.getRemaining().trim().split(" ");
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

        System.out.println("CurrentFlagIndex: " + currentFlagIndex);
        System.out.println("CurrentFlag: " + currentFlag);
        System.out.println("CurrentInput: '" + builder.getRemaining() + "'");
        System.out.println("SplitInput: " + Arrays.toString(splitInput));
        System.out.println("RemainingInput: '" + builder.getInput() + "'");

        var inputAfterFlag = String.join(" ", Arrays.asList(splitInput).subList(currentFlagIndex + 1, splitInput.length));
        var currentWord = splitInput[splitInput.length - 1];
        System.out.println("CurrentWord: " + currentWord);
        System.out.println("RemainingInput: " + inputAfterFlag);

        if (currentWord.startsWith("-") && !builder.getRemaining().endsWith(" ")) {
            getUnusedFlags(context).forEach(flag -> builder.suggest("-" + flag.getName(), flag.getTooltipAsMessage()));
            return builder.buildFuture();
        }

//        var currentWord = splitInput[splitInput.length - 1];
//        var previousWord = splitInput.length > 1 ? splitInput[splitInput.length - 2] : null;

        //push the completion cursor to the start of the current word
        var completingAt = builder.getStart() + inputAfterFlag.length() +
                (currentFlag == null
                    ? 0
                    : builder.getRemaining().lastIndexOf("-" + currentFlag.getName()) + currentFlag.getName().length() + 1);
        var newBuilder = builder.createOffset(completingAt);

        if (currentFlag == null || currentFlag.isBooleanFlag() || (!inputAfterFlag.isBlank() && builder.getRemaining().endsWith(" "))) {
            getUnusedFlags(context).forEach(flag -> newBuilder.suggest("-" + flag.getName(), flag.getTooltipAsMessage()));
            return newBuilder.buildFuture();
        }

        System.out.println("CompletingAt: " + (completingAt + (builder.getRemaining().endsWith(" ") ? 0 : 1)));

        return currentFlag.getType().listSuggestions(context, newBuilder.createOffset(completingAt + (builder.getRemaining().endsWith(" ") ? 0 : 1)));
//
//        //attempt to get the current flag
////        var currentFlag2 = getFlag(currentWord.trim());
//
//        //if the current flag wasnt found from the current word, attempt to get the flag from the previous word if possible
//        //if the current input ends with a space, we assume they are not trying to complete the flag data anymore, and we should suggest all currently possible flags
//        if (currentFlag == null && previousWord != null && !builder.getRemaining().endsWith(" ")) {
//            currentFlag2 = getFlag(previousWord.trim());
//        }
//
//        if (currentFlag2 == null || currentFlag2.isBooleanFlag()) {
//            getUnusedFlags(context).forEach(flag -> newBuilder.suggest("-" + flag.getName(), flag.getTooltipAsMessage()));
//            return newBuilder.buildFuture();
//        }
//
//        //return currentFlag.getType().listSuggestions(context, newBuilder);
//
//        //find the current flag by looking backwards until we find a flag
//        var currentFlagIndex = -1;
//        IPdkCommandFlag<?> currentFlag;
//        for (var i = splitInput.length - 1; i >= 0; i--) {
//            var foundFlag = getFlag(splitInput[i]);
//            if (foundFlag != null) {
//                currentFlagIndex = i;
//                currentFlag = foundFlag;
//                break;
//            }
//        }
//
//        //merge the remaining input after the flag into a single string
//        var remainingInput = String.join(" ", Arrays.asList(splitInput).subList(currentFlagIndex + 1, splitInput.length));
//
//        var

    }

    @Override
    public FlagMap convertToCustom(String nativeType, StringReader reader) throws CommandSyntaxException {
//        var map = new FlagMap();
////        var splitArgs = nativeType.split(" ");
////        for (var flag : flags) {
////            for (var i = 0; i < splitArgs.length; i++) {
////                var currentArg = splitArgs[i];
////                if (!currentArg.equalsIgnoreCase("-" + flag.getName())) continue;
////
////                if (!flag.isBooleanFlag()) {
////                    if (splitArgs.length <= i + 1) {
////                        throw FLAG_NOT_BOOLEAN.createWithContext(reader, flag.getName(), flag.getType().);
////                        throw new CommandSyntaxException(null, () -> "Flag -" + flag.getName() + " requires a value.");
////                    }
////                    var flagValue = splitArgs[i + 1];
////                    map.setFlag(flag.getName(), flag.getType().parse(new StringReader(flagValue)));
////                }
////                else map.setFlag(flag.getName(), true);
////            }
////        }
//
//        System.out.println(reader.getString());
//        System.out.println(reader.getRemainingLength());
//        System.out.println(reader.canRead());
//        System.out.println(reader.getCursor());
//
//        while (reader.getRemainingLength() > 0) {
//            var currentArg = reader.readString();
//            System.out.println("CurrentArg: " + currentArg);
//            if (currentArg.isBlank()) continue;
//            var flag = getFlag(currentArg);
//            if (flag == null) throw UNKNOWN_FLAG.createWithContext(reader, currentArg);
//            if (!flag.isBooleanFlag()) {
//                if (!reader.canRead()) throw MISSING_ARGUMENT.createWithContext(reader, flag.getName());
//                var flagValue = flag.getType().parse(reader);
//                System.out.println("FlagValue: " + flagValue);
//                map.setFlag(flag.getName(), flagValue);
//            }
//            else map.setFlag(flag.getName(), true);
//        }
//
//        return map;
        return parse(reader);
    }

    @Override
    public @NotNull FlagMap parse(@NotNull StringReader reader) throws CommandSyntaxException {
        var map = new FlagMap();
        while (reader.canRead()) {
            var currentArg = reader.readString();
            if (currentArg.isBlank()) {
                reader.skip();
                continue;
            }
            System.out.println("CurrentArg: " + currentArg);
            var flag = getFlag(currentArg);
            if (flag == null) throw UNKNOWN_FLAG.createWithContext(reader, currentArg);
            if (!flag.isBooleanFlag()) {
                if (!reader.canRead()) throw MISSING_ARGUMENT.createWithContext(reader, flag.getName());
                var nextArg = reader.readString();
                System.out.println("PreParse: " + reader.getCursor());
                var flagValue = flag.getType().parse(reader);
                System.out.println("PostParse: " + reader.getCursor());
                System.out.println("NextArg: " + nextArg);
                System.out.println("FlagValue: " + flagValue);
                map.setFlag(flag.getName(), flagValue);
            }
            else map.setFlag(flag.getName(), true);
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
