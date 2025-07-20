package com.njdaeger.pdk.command.brigadier.builder;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.arguments.defaults.EnumArgument;
import com.njdaeger.pdk.command.brigadier.arguments.defaults.FloatArgument;
import com.njdaeger.pdk.command.brigadier.arguments.defaults.GreedyStringArgument;
import com.njdaeger.pdk.command.brigadier.arguments.defaults.IntegerArgument;
import com.njdaeger.pdk.command.brigadier.arguments.defaults.LongArgument;
import com.njdaeger.pdk.command.brigadier.arguments.defaults.PlayerArgument;
import com.njdaeger.pdk.command.brigadier.arguments.defaults.QuotedStringArgument;
import com.njdaeger.pdk.command.brigadier.arguments.defaults.StringArgument;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

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

    //region StringArgument

    public static StringArgument string(Function<ICommandContext, Collection<String>> suggestions, Message defaultTooltipMessage) {
        return new StringArgument(suggestions, defaultTooltipMessage);
    }

    public static StringArgument string(Function<ICommandContext, Map<String, Message>> suggestions) {
        return new StringArgument(suggestions);
    }

    public static StringArgument string(Message defaultTooltipMessage) {
        return new StringArgument(defaultTooltipMessage);
    }

    public static StringArgument string() {
        return new StringArgument(() -> "A string of text.");
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
        return new IntegerArgument(min, max, i -> () -> "Number '" + i + "' must be between " + min + " and " + max, () -> "A number.");
    }

    public static IntegerArgument integer(Message defaultTooltipMessage) {
        return new IntegerArgument(defaultTooltipMessage);
    }

    public static IntegerArgument integer() {
        return new IntegerArgument(() -> "A number.");
    }

    //endregion

    //region LongArgument

    public static LongArgument longArg(Function<ICommandContext, Collection<Long>> suggestions, Message defaultTooltipMessage) {
        return new LongArgument(suggestions, defaultTooltipMessage);
    }

    public static LongArgument longArg(Function<ICommandContext, Map<Long, Message>> suggestions) {
        return new LongArgument(suggestions);
    }

    public static LongArgument longArg(long min, long max, Function<Long, Message> outOfBoundsMessage, Message defaultTooltipMessage) {
        return new LongArgument(min, max, outOfBoundsMessage, defaultTooltipMessage);
    }

    public static LongArgument longArg(long min, Message defaultTooltipMessage) {
        return new LongArgument(min, Long.MAX_VALUE, l -> new LiteralMessage("Number must be greater than " + min), defaultTooltipMessage);
    }

    public static LongArgument longArg(long min, long max) {
        return new LongArgument(min, max, l -> () -> "Number '" + l + "' must be between " + min + " and " + max, () -> "A number.");
    }

    public static LongArgument longArg(Message defaultTooltipMessage) {
        return new LongArgument(defaultTooltipMessage);
    }

    public static LongArgument longArg() {
        return new LongArgument(() -> "A number.");
    }

    //endregion

    //region FloatArgument

    public static FloatArgument floatArg(Function<ICommandContext, Collection<Float>> suggestions, Message defaultTooltipMessage) {
        return new FloatArgument(suggestions, defaultTooltipMessage);
    }

    public static FloatArgument floatArg(Function<ICommandContext, Map<Float, Message>> suggestions) {
        return new FloatArgument(suggestions);
    }

    public static FloatArgument floatArg(float min, float max, Function<Float, Message> outOfBoundsMessage, Message defaultTooltipMessage) {
        return new FloatArgument(min, max, outOfBoundsMessage, defaultTooltipMessage);
    }

    public static FloatArgument floatArg(float min, Message defaultTooltipMessage) {
        return new FloatArgument(min, Integer.MAX_VALUE, f -> new LiteralMessage("Number must be greater than " + min), defaultTooltipMessage);
    }

    public static FloatArgument floatArg(float min, float max) {
        return new FloatArgument(min, max, f -> () -> "Number '" + f + "' must be between " + min + " and " + max, () -> "A number.");
    }

    public static FloatArgument floatArg(Message defaultTooltipMessage) {
        return new FloatArgument(defaultTooltipMessage);
    }

    public static FloatArgument floatArg() {
        return new FloatArgument(() -> "A number.");
    }

    //endregion

    //region PlayerArguments

    public static PlayerArgument player() {
        return new PlayerArgument(() -> "A player.");
    }

    public static PlayerArgument player(Message defaultTooltipMessage) {
        return new PlayerArgument(defaultTooltipMessage);
    }

    public static PlayerArgument player(Predicate<Player> filterBy) {
        return new PlayerArgument(filterBy, () -> "A player.");
    }

    public static PlayerArgument player(Predicate<Player> filterBy, Message defaultTooltipMessage) {
        return new PlayerArgument(filterBy, defaultTooltipMessage);
    }

    public static PlayerArgument player(Function<ICommandContext, Map<Player, Message>> suggestions) {
        return new PlayerArgument(suggestions);
    }

    public static PlayerArgument player(Function<ICommandContext, Collection<Player>> suggestions, Message defaultTooltipMessage) {
        return new PlayerArgument(suggestions, defaultTooltipMessage);
    }

    //endregion

    //region EnumArgument

    public static <T extends Enum<T>> EnumArgument<T> enumArg(Class<T> enumClass) {
        return new EnumArgument<>(enumClass, () -> "Any constant of the enum " + enumClass.getSimpleName() + ".");
    }

    public static <T extends Enum<T>> EnumArgument<T> enumArg(Class<T> enumClass, Message defaultTooltipMessage) {
        return new EnumArgument<>(enumClass, defaultTooltipMessage);
    }

    public static <T extends Enum<T>> EnumArgument<T> enumArg(Class<T> enumClass, Function<ICommandContext, Map<T, Message>> suggestions) {
        return new EnumArgument<>(enumClass, suggestions);
    }

    public static <T extends Enum<T>> EnumArgument<T> enumArg(Class<T> enumClass, Function<ICommandContext, Collection<T>> suggestions, Message defaultTooltipMessage) {
        return new EnumArgument<>(enumClass, suggestions, defaultTooltipMessage);
    }

    //endregion

}
