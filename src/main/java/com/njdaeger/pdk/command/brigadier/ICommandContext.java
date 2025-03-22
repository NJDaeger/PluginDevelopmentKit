package com.njdaeger.pdk.command.brigadier;

import com.njdaeger.pdk.command.exception.CommandSenderTypeException;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.exception.PermissionDeniedException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

/**
 *
 */
public interface ICommandContext {

    /**
     * Check if a flag has been used in this command execution.
     * @param flag The flag to check for
     * @return True if the flag was used, false otherwise
     */
    @Contract(pure = true, value = "null -> fail")
    boolean hasFlag(String flag);

    /**
     * Get the value of a flag from this command execution
     * @param flag The flag to get the value of
     * @param <T> The type of the flag
     * @return The value of the flag, null if no flag exists.
     */
    @Nullable
    @Contract(pure = true, value = "null -> fail")
    <T> T getFlag(String flag);

    /**
     * Get the value of a flag from this command execution or return a default value if the flag does not exist
     * @param flag The flag to get the value of
     * @param defaultValue The default value to return if the flag does not exist
     * @param <T> The type of the flag
     * @return The value of the flag, or the default value if the flag does not exist
     */
    @Nullable
    @Contract(pure = true, value = "null, _ -> fail;  _, null -> null")
    <T> T getFlag(String flag, T defaultValue);

    /**
     * Get the alias of the command that was executed
     * @return The alias of the command that was executed
     */
    @NotNull
    default String getAlias() {
        return getRawArgs()[0];
    }

    /**
     * Get the sender of the command
     * @return The sender of the command
     */
    @NotNull
    CommandSender getSender();

    /**
     * Get the raw command string that was executed including all flags and the command alias.
     * @return The raw command string that was executed.
     */
    @NotNull
    String getRawInput();

    /**
     * Get the raw command string that was executed including all flags and the command alias split into an array of strings.
     * @return The raw command string that was executed split into an array of strings.
     */
    @NotNull
    default String[] getRawArgs() {
        return getRawInput().split(" ");
    }

    /**
     * Get the arguments that were passed to the command chunked by their parsed type. This will include flags as a single entry in this array.
     *
     * <p> For example, if we have defined a command like:
     * <pre>
     *     /testcommand literalArgument [typedNumericArgument] [typedQuotedStringArgument] anotherLiteral
     * </pre>
     * <p> If we pass the command in like: <code> /testcommand literalArgument 123.9 "hi there world" anotherLiteral </code>
     * <p> The result of this method would be:
     * <pre>
     *     ["literalArgument", "123.9", "hi there world", "anotherLiteral"]
     * </pre>
     * When it comes to commands with flags, the flag will be included as a single entry in this array at the end since the flag argument is technically a single argument internally.
     * @return The arguments that were passed to the command. Will return an empty array if there are no arguments.
     */
    @NotNull
    String[] getArgs();

    /**
     * Check if this command execution has any arguments with it
     * @return True if the command execution has arguments, false otherwise
     */
    default boolean hasArgs() {
        return getArgCount() > 0;
    }

    /**
     * Get the argument at the given index. This will throw an IndexOutOfBoundsException if the index is out of bounds.
     * @param index The index of the argument to get
     * @return The argument at the given index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    @NotNull
    @Contract(pure = true)
    default String argAt(int index) {
        if (!hasArgAt(index)) throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for the arguments array of length " + getArgCount());
        return getArgs()[index];
    }

    /**
     * Get the argument at the given index or return null if the index is out of bounds.
     * @param index The index of the argument to get
     * @return The argument at the given index or null if the index is out of bounds
     */
    @Nullable
    @Contract(pure = true)
    default String argAtOrNull(int index) {
        if (!hasArgAt(index)) return null;
        return getArgs()[index];
    }

    /**
     * Get the argument at the given index or return a default value if the index is out of bounds.
     * @param index The index of the argument to get
     * @param defVal The default value to return if the index is out of bounds
     * @return The argument at the given index or the default value if the index is out of bounds
     */
    @Nullable
    @Contract(pure = true, value = "_, !null -> !null")
    default String argAtOrDefault(int index, String defVal) {
        if (!hasArgAt(index)) return defVal;
        return getArgs()[index];
    }

    /**
     * Check if this command execution has an argument at the given index
     * @param index The index of the argument to check for
     * @return True if the command execution has an argument at the given index, false otherwise
     */
    default boolean hasArgAt(int index) {
        return index >= 0 && index < getArgCount();
    }

    /**
     * Get the number of arguments that were passed to the command. This includes multi spaced arguments as single array entries.
     * @return The number of arguments that were passed to the command.
     */
    @Contract(pure = true)
    default int getArgCount() {
        return getArgs().length;
    }

    /**
     * Check if the number of chunked arguments passed to the command is equal to the given length. (This is based on the results of {@link #getArgs()} and not the raw input)
     * @param length The length to check for
     * @return True if the number of arguments passed to the command is equal to the given length, false otherwise
     */
    @Contract(pure = true)
    default boolean isLength(int length) {
        return getArgCount() == length;
    }

    /**
     * Check if the number of chunked arguments passed to the command is greater than the given length. (This is based on the results of {@link #getArgs()} and not the raw input)
     * @param length The length to check for
     * @return True if the number of arguments passed to the command is greater than the given length, false otherwise
     */
    @Contract(pure = true)
    default boolean isGreater(int length) {
        return getArgCount() > length;
    }

    /**
     * Check if the number of chunked arguments passed to the command is less than the given length. (This is based on the results of {@link #getArgs()} and not the raw input)
     * @param length The length to check for
     * @return True if the number of arguments passed to the command is less than the given length, false otherwise
     */
    @Contract(pure = true)
    default boolean isLess(int length) {
        return getArgCount() < length;
    }

    /**
     * Check if the number of chunked arguments passed to the command is greater than or equal to the given length. (This is based on the results of {@link #getArgs()} and not the raw input)
     * @param length The length to check for
     * @return True if the number of arguments passed to the command is greater than or equal to the given length, false otherwise
     */
    @Contract(pure = true)
    default boolean isGreaterOrEqual(int length) {
        return getArgCount() >= length;
    }

    /**
     * Check if the number of chunked arguments passed to the command is less than or equal to the given length. (This is based on the results of {@link #getArgs()} and not the raw input)
     * @param length The length to check for
     * @return True if the number of arguments passed to the command is less than or equal to the given length, false otherwise
     */
    @Contract(pure = true)
    default boolean isLessOrEqual(int length) {
        return getArgCount() <= length;
    }

    /**
     * Join the chunked arguments passed to the command into a single string. (This is based on the results of {@link #getArgs()} and not the raw input)
     * @return All the arguments passed to the command joined into a single string
     */
    @NotNull
    default String joinArgs() {
        return String.join(" ", getArgs());
    }

    /**
     * Join the chunked arguments passed to the command into a single string starting from the given index. (This is based on the results of {@link #getArgs()} and not the raw input)
     * @param start The index to start joining from
     * @return All the arguments passed to the command joined into a single string starting from the given index
     */
    default String joinArgs(int start) {
        return joinArgs(start, getArgCount());
    }

    /**
     * Join the chunked arguments passed to the command into a single string starting from the given index and ending at the given index. (This is based on the results of {@link #getArgs()} and not the raw input)
     * @param start The index to start joining from
     * @param finish The index to end joining at
     * @return All the arguments passed to the command joined into a single string starting from the given index and ending at the given index
     */
    default String joinArgs(int start, int finish) {
        if (start < 0 || start >= getArgCount()) throw new IndexOutOfBoundsException("Start index " + start + " is out of bounds for the arguments array of length " + getArgCount() + ".");
        if (finish < 0 || finish > getArgCount()) throw new IndexOutOfBoundsException("Finish index " + finish + " is out of bounds for the arguments array of length " + getArgCount() + ".");
        if (start > finish) throw new IllegalArgumentException("Start index " + start + " cannot be greater than finish index " + finish + ".");
        return String.join(" ", List.of(getArgs()).subList(start, finish));
    }

    /**
     * Get the first argument passed to the command. This will return null if there are no arguments.
     * @return The first argument passed to the command. Will return null if there are no arguments.
     */
    @Nullable
    @Contract(pure = true)
    default String first() {
        return getArgCount() > 0 ? getArgs()[0] : null;
    }

    /**
     * Get the last argument passed to the command. This will return null if there are no arguments.
     * @return The last argument passed to the command. Will return null if there are no arguments.
     */
    @Nullable
    @Contract(pure = true)
    default String last() {
        return getArgCount() > 0 ? getArgs()[getArgCount() - 1] : null;
    }

    /**
     * Check if the command context has a typed argument
     * @param argName The name of the argument
     * @return True if the command context has the argument, false otherwise
     */
    @Contract(pure = true, value = "null -> fail")
    boolean hasTyped(String argName);

    /**
     * Check if the command context has a typed argument of the given type
     * @param argName The name of the argument
     * @param type The type of the argument to check for
     * @return True if the command context has the argument of the given name and type, false otherwise
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Contract(pure = true, value = "null, _ -> fail; _, null -> fail")
    default boolean hasTypedAs(String argName, Class<?> type) {
        if (argName == null) throw new IllegalArgumentException("Argument name cannot be null.");
        if (type == null) throw new IllegalArgumentException("Type cannot be null.");
        if (hasTyped(argName)) {
            try {
                getTyped(argName, type);
                return true;
            } catch (Exception ignore) {}
        }
        return false;
    }

    /**
     * Get a typed argument from the command context
     *
     * @param argName The name of the argument
     * @return The argument value
     */
    @Nullable
    @Contract(pure = true, value = "null -> fail")
    Object getTyped(String argName);

    /**
     * Get aa typed argument from the command context
     * @param argName The name of the argument
     * @param type The type of the argument
     * @param <T> The type of the argument
     * @return The argument value
     */
    @NotNull
    @Contract(pure = true, value = "null, _ -> fail; _, null -> fail")
    <T> T getTyped(String argName, Class<T> type);

    /**
     * Get a typed argument from the command context as a string or return a default value if the argument does not exist
     * @param argName The name of the argument
     * @param defVal The default value to return if the argument does not exist
     * @return The argument value or the default value if the argument does not exist
     */
    @SuppressWarnings("unchecked")
    @NotNull
    @Contract(pure = true, value = "null, _ -> fail; _, null -> fail")
    default <T> T getTyped(String argName, T defVal) {
        if (argName == null) throw new IllegalArgumentException("Argument name cannot be null.");
        if (defVal == null) throw new IllegalArgumentException("Default value cannot be null.");
        if (!hasTypedAs(argName, defVal.getClass())) return defVal;
        return (T) getTyped(argName, defVal.getClass());
    }

    /**
     * Get a typed argument from the command context or return a default value if the argument does not exist
     * @param argName The name of the argument
     * @param type The type of the argument
     * @param defVal The default value to return if the argument does not exist
     * @param <T> The type of the argument
     * @return The argument value or the default value if the argument does not exist
     */
    @Nullable("If the default value is null")
    @Contract(pure = true, value = "null, _, _ -> fail; _, null, _ -> fail;")
    default <T> T getTyped(String argName, Class<T> type, T defVal) {
        if (argName == null) throw new IllegalArgumentException("Argument name cannot be null.");
        if (type == null) throw new IllegalArgumentException("Type cannot be null.");
        if (!hasTypedAs(argName, type)) return defVal;
        return getTyped(argName, type);
    }

    /**
     * Check if the sender has a permission
     * @param permission The permission to check for
     * @return True if the sender has the permission, false otherwise
     */
    @Contract(pure = true, value = "null -> fail; !null -> _")
    default boolean hasPermission(String permission) {
        if (permission == null) throw new IllegalArgumentException("Permission cannot be null.");
        return getSender().hasPermission(permission);
    }

    /**
     * Check if the sender has any of the permissions
     * @param permissions The permissions to check for
     * @return True if the sender has any of the permissions, false otherwise
     */
    @Contract(pure = true, value = "null -> fail; !null -> _")
    default boolean hasAnyPermission(String... permissions) {
        if (permissions == null) throw new IllegalArgumentException("Permissions cannot be null.");
        return Stream.of(permissions).anyMatch(this::hasPermission);
    }

    /**
     * Check if the sender has all of the permissions
     * @param permissions The permissions to check for
     * @return True if the sender has all of the permissions, false otherwise
     */
    @Contract(pure = true, value = "null -> fail; !null -> _")
    default boolean hasAllPermissions(String... permissions) {
        if (permissions == null) throw new IllegalArgumentException("Permissions cannot be null.");
        return Stream.of(permissions).allMatch(this::hasPermission);
    }

    /**
     * Check if the sender is the console
     * @return True if the sender is the console, false otherwise
     */
    default boolean isConsole() {
        return getSender() instanceof ConsoleCommandSender;
    }

    /**
     * get the sender as a console sender
     * @return the sender as a console sender
     */
    @NotNull
    @Contract(pure = true)
    default ConsoleCommandSender asConsole() throws CommandSenderTypeException {
        if (isConsole()) return (ConsoleCommandSender)getSender();
        else throw new CommandSenderTypeException(getSender(), ConsoleCommandSender.class);
    }

    /**
     * get the sender as a console sender or null if the sender is not a console
     * @return the sender as a console sender or null if the sender is not a console
     */
    @Nullable
    @Contract(pure = true)
    default ConsoleCommandSender asConsoleOrNull() {
        if (isConsole()) return (ConsoleCommandSender)getSender();
        return null;
    }

    /**
     * Check if the sender is a player
     * @return True if the sender is a player, false otherwise
     */
    default boolean isPlayer() {
        return getSender() instanceof Player;
    }

    /**
     * Get the sender as a player
     * @return The sender as a player
     */
    @NotNull
    @Contract(pure = true)
    default Player asPlayer() throws CommandSenderTypeException {
        if (isPlayer()) return (Player)getSender();
        else throw new CommandSenderTypeException(getSender(), Player.class);
    }

    /**
     * Get the sender as a player or null if the sender is not a player
     * @return The sender as a player or null if the sender is not a player
     */
    @Nullable
    @Contract(pure = true)
    default Player asPlayerOrNull() {
        if (isPlayer()) return (Player)getSender();
        return null;
    }

    /**
     * Check if the sender is a block
     * @return True if the sender is a block, false otherwise
     */
    default boolean isBlock() {
        return getSender() instanceof BlockCommandSender;
    }

    /**
     * Get the sender as a block sender
     * @return The sender as a block sender
     */
    @NotNull
    @Contract(pure = true)
    default BlockCommandSender asBlock() throws CommandSenderTypeException {
        if (isBlock()) return (BlockCommandSender)getSender();
        else throw new CommandSenderTypeException(getSender(), BlockCommandSender.class);
    }

    /**
     * Get the sender as a block sender or null if the sender is not a block
     * @return The sender as a block sender or null if the sender is not a block
     */
    @Nullable
    @Contract(pure = true)
    default BlockCommandSender asBlockOrNull() {
        if (isBlock()) return (BlockCommandSender)getSender();
        return null;
    }

    /**
     * Check if the sender is an entity
     * @return True if the sender is an entity, false otherwise
     */
    default boolean isEntity() {
        return getSender() instanceof Entity;
    }

    /**
     * Get the sender as an entity
     * @return The sender as an entity
     */
    @NotNull
    @Contract(pure = true)
    default Entity asEntity() throws CommandSenderTypeException {
        if (isEntity()) return (Entity)getSender();
        else throw new CommandSenderTypeException(getSender(), Entity.class);
    }

    /**
     * Get the sender as an entity or null if the sender is not an entity
     * @return The sender as an entity or null if the sender is not an entity
     */
    @Nullable
    @Contract(pure = true)
    default Entity asEntityOrNull() {
        if (isEntity()) return (Entity)getSender();
        return null;
    }

    /**
     * Check if the sender is an instance of the given type
     * @param type The type to check for
     * @param <S> The type to check for
     * @return True if the sender is an instance of the given type, false otherwise
     */
    @Contract(pure = true, value = "null -> fail; !null -> _")
    default <S extends CommandSender> boolean is(Class<S> type) {
        if (type == null) throw new IllegalArgumentException("Type cannot be null.");
        return type.isInstance(getSender());
    }

    /**
     * Get the sender as the given type
     * @param type The type to get the sender as
     * @param <S> The type to get the sender as
     * @return The sender as the given type
     */
    @NotNull
    @Contract(pure = true, value = "null -> fail; !null -> _")
    default <S extends CommandSender> S as(Class<S> type) throws CommandSenderTypeException {
        if (type == null) throw new IllegalArgumentException("Type cannot be null.");
        if (is(type)) return type.cast(getSender());
        else throw new CommandSenderTypeException(getSender(), type);
    }

    /**
     * Get the sender as the given type or null if the sender is not of that type
     * @param type The type to get the sender as
     * @param <S> The type to get the sender as
     * @return The sender as the given type or null if the sender is not of that type
     */
    @Nullable
    @Contract(pure = true, value = "null -> fail; !null -> _")
    default <S extends CommandSender> S asOrNull(Class<S> type) {
        if (type == null) throw new IllegalArgumentException("Type cannot be null.");
        if (is(type)) return type.cast(getSender());
        return null;
    }

    /**
     * Check if the sender is locatable
     * @return True if the sender is locatable, false otherwise
     */
    default boolean isLocatable() {
        return isPlayer() || isBlock() || isEntity();
    }

    /**
     * Get the location of the sender
     * @return The location of the sender
     * @throws CommandSenderTypeException If the sender is not locatable
     */
    @NotNull
    @Contract(pure = true)
    default Location getLocation() throws CommandSenderTypeException {
        if (isPlayer()) return asPlayer().getLocation();
        if (isBlock()) return asBlock().getBlock().getLocation();
        if (isEntity()) return asEntity().getLocation();
        throw new CommandSenderTypeException("This command requires a command sender that is either a Player, Block, or Entity so a location can be determined.");
    }

    /**
     * Get the location of the sender or null if the sender is not locatable
     * @return The location of the sender or null if the sender is not locatable
     */
    @Nullable
    @Contract(pure = true)
    default Location getLocationOrNull() {
        try {
            return getLocation();
        } catch (PDKCommandException ignored) {}
        return null;
    }

    /**
     * @throws PDKCommandException due to a lack of permissions
     */
    @Contract(pure = true , value = "-> fail")
    default void noPermission() throws PDKCommandException {
        throw new PermissionDeniedException();
    }

    /**
     * @param message The message to send to the sender
     * @throws PDKCommandException due to a lack of permissions
     */
    @Contract(pure = true , value = "_ -> fail")
    default void noPermission(String message) throws PDKCommandException {
        throw new PermissionDeniedException(message);
    }

    /**
     * @param message The message to send to the sender
     * @throws PDKCommandException due to a lack of permissions
     */
    @Contract(pure = true , value = "_ -> fail")
    default void noPermission(TextComponent message) throws PDKCommandException {
        throw new PermissionDeniedException(message);
    }

    /**
     * @throws PDKCommandException due to an error
     */
    @Contract(pure = true , value = "_ -> fail")
    default void error(String message) throws PDKCommandException {
        throw new PDKCommandException(Component.text(message, NamedTextColor.RED));
    }

    /**
     * @param message The message to send to the sender
     * @throws PDKCommandException due to an error
     */
    @Contract(pure = true , value = "_ -> fail")
    default void error(TextComponent message) throws PDKCommandException {
        throw new PDKCommandException(message);
    }

    /**
     * Sends a message to the command sender
     * @param message The message to send
     */
    @Contract(pure = true , value = "null -> fail; !null -> _")
    default void send(String message) {
        if (message == null) throw new IllegalArgumentException("Message cannot be null.");
        getSender().sendMessage(message);
    }

    /**
     * Sends a component to the command sender
     * @param component The component to send
     */
    @Contract(pure = true , value = "null -> fail; !null -> _")
    default void send(Component component) {
        if (component == null) throw new IllegalArgumentException("Component cannot be null.");
        getSender().sendMessage(component);
    }

}
