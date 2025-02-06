package com.njdaeger.pdk.command.brigadier.flags;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.arguments.BasePdkArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FlagFieldArgumentType extends BasePdkArgumentType<FlagMap, String> {

    private final List<IPdkCommandFlag<?>> flags;

    public FlagFieldArgumentType(List<IPdkCommandFlag<?>> flags) {
        this.flags = flags;
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var splitInput = builder.getRemaining().trim().split(" ");
        var currentWord = splitInput[splitInput.length - 1];
        var previousWord = splitInput.length > 1 ? splitInput[splitInput.length - 2] : null;

        //push the completion cursor to the start of the current word
        var completingAt = builder.getStart() + builder.getRemaining().lastIndexOf(" ") + 1;
        var newBuilder = builder.createOffset(completingAt);

        //attempt to get the current flag
        var currentFlag = getFlag(currentWord.trim());

        //if the current flag wasnt found from the current word, attempt to get the flag from the previous word if possible
        //if the current input ends with a space, we assume they are not trying to complete the flag data anymore, and we should suggest all currently possible flags
        if (currentFlag == null && previousWord != null && !builder.getRemaining().endsWith(" ")) {
            currentFlag = getFlag(previousWord.trim());
        }

        if (currentFlag == null || currentFlag.isBooleanFlag()) {
            getUnusedFlags(context).forEach(flag -> newBuilder.suggest("-" + flag.getName(), flag.getTooltipAsMessage()));
            return newBuilder.buildFuture();
        }

        return currentFlag.getType().listSuggestions(context, newBuilder);
    }

    @Override
    public FlagMap convertToCustom(String nativeType) throws CommandSyntaxException {
        var map = new FlagMap();
        var splitArgs = nativeType.split(" ");
        for (var flag : flags) {
            for (var i = 0; i < splitArgs.length; i++) {
                var currentArg = splitArgs[i];
                if (!currentArg.equalsIgnoreCase("-" + flag.getName())) continue;

                if (!flag.isBooleanFlag()) {
                    if (splitArgs.length <= i + 1) {
                        throw new CommandSyntaxException(null, () -> "Flag -" + flag.getName() + " requires a value.");
                    }
                    var flagValue = splitArgs[i + 1];
                    map.setFlag(flag.getName(), flag.getType().parse(new StringReader(flagValue)));
                }
                else map.setFlag(flag.getName(), true);
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
