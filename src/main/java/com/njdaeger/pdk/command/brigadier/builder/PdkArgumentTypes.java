package com.njdaeger.pdk.command.brigadier.builder;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.arguments.GreedyStringArgument;
import com.njdaeger.pdk.command.brigadier.arguments.IntegerArgument;
import com.njdaeger.pdk.command.brigadier.arguments.QuotedStringArgument;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PdkArgumentTypes {

    //region QuotedStringArgument

    public static QuotedStringArgument quotedString(boolean allowEmpty, Function<ICommandContext, Collection<String>> suggestions, Message defaultTooltipMessage) {
        return new QuotedStringArgument(allowEmpty, suggestions, defaultTooltipMessage);

    }

    public static QuotedStringArgument quotedString(boolean allowEmpty, Function<ICommandContext, Map<String, Message>> suggestions) {
        return new QuotedStringArgument(allowEmpty, suggestions);
    }

    public static QuotedStringArgument quotedString(boolean allowEmpty, Message defaultTooltipMessage) {
        return new QuotedStringArgument(allowEmpty, defaultTooltipMessage);
    }

    public static QuotedStringArgument quotedString() {
        return quotedString(true, () -> "Any phrase surrounded by quotes. It CAN have spaces.");
    }

    //endregion

    //region GreedyStringArgument

    public static GreedyStringArgument greedyString(Function<ICommandContext, List<String>> suggestions, Message defaultTooltipMessage) {
        return new GreedyStringArgument(suggestions, defaultTooltipMessage);
    }

    public static GreedyStringArgument greedyString(Function<ICommandContext, Map<String, Message>> suggestions) {
        return new GreedyStringArgument(suggestions);
    }

    public static GreedyStringArgument greedyString(Message defaultTooltipMessage) {
        return new GreedyStringArgument(defaultTooltipMessage);
    }

    public static GreedyStringArgument greedyString() {
        return new GreedyStringArgument(() -> "A string of text.");
    }

    //endregion

    //region IntegerArgument

    public static IntegerArgument integer(Function<ICommandContext, Collection<Integer>> suggestions, Message defaultTooltipMessage) {
        return new IntegerArgument(suggestions, defaultTooltipMessage);
    }

    public static IntegerArgument integer(Function<ICommandContext, Map<Integer, Message>> suggestions) {
        return new IntegerArgument(suggestions);
    }

    public static IntegerArgument integer(int min, int max, Function<Integer, Message> outOfBoundsMessage, Message defaultTooltipMessage) {
        return new IntegerArgument(min, max, outOfBoundsMessage, defaultTooltipMessage);
    }

    public static IntegerArgument integer(int min, Message defaultTooltipMessage) {
        return new IntegerArgument(min, Integer.MAX_VALUE, i -> new LiteralMessage("Number must be greater than " + min), defaultTooltipMessage);
    }

    public static IntegerArgument integer(int min, int max) {
        return new IntegerArgument(min, max, i -> new LiteralMessage("Number must be between " + min + " and " + max), () -> "A number.");
    }

    public static IntegerArgument integer(Message defaultTooltipMessage) {
        return new IntegerArgument(defaultTooltipMessage);
    }

    public static IntegerArgument integer() {
        return new IntegerArgument(() -> "A number.");
    }

    //endregion

}
