package com.njdaeger.pdk.command;

import com.njdaeger.pdk.command.exception.PDKCommandException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TabContext extends CommandContext {

    private final List<String> possible;

    public TabContext(Plugin plugin, PDKCommand command, CommandSender sender, String alias, String[] args) {
        super(plugin, command, sender, alias, args);
        this.possible = new ArrayList<>();
    }

    /**
     * Gets the previous argument from the current index. If the current index is
     *
     * @return The previous argument
     */
    public String getPrevious() {
        return getLength() <= 1 ? null : argAt(getLength() - 2);
    }

    /**
     * Checks if the previous arg matches a specific regex pattern.
     *
     * @param pattern The pattern to match
     * @return True if the pattern matches, false if the previous arg is null or if the pattern doesn't match.
     */
    public boolean isPrevious(Pattern pattern) {
        return getPrevious() != null && getPrevious().matches(pattern.pattern());
    }

    /**
     * Checks if the previous arg matches a specific string. Not case sensitive.
     *
     * @param previous The previous text to match
     * @return True if it matches, false if the previous is null or if it doesn't match.
     */
    public boolean isPrevious(String previous) {
        return isPrevious(true, previous);
    }

    /**
     * Checks if the previous arg matches a specific string. This can be set to be case sensitive
     *
     * @param ignoreCase True ignores case, false does not.
     * @param previous The previous text to match
     * @return True if it matches, false if the previous is null or if it doesn't match
     */
    public boolean isPrevious(boolean ignoreCase, String previous) {
        return getPrevious() != null && (ignoreCase ? getPrevious().equalsIgnoreCase(previous) : getPrevious().equals(previous));
    }

    /**
     * Gets the current argument being typed
     *
     * @return The current arg being typed
     */
    public String getCurrent() {
        return isLength(0) ? null : argAt(getLength() - 1).trim();
    }

    /**
     * Checks if the current argument being typed matches the specific pattern.
     *
     * @param pattern The pattern to match
     * @return True if it matches, false if the current arg is null or if the pattern doesnt match
     */
    public boolean isCurrent(Pattern pattern) {
        return getCurrent() != null && getCurrent().matches(pattern.pattern());
    }

    /**
     * Checks if the current argument being typed matches the specific string. This is not case sensitive
     *
     * @param current The text to match
     * @return True if it matches, false if the current is null or if it doesnt match
     */
    public boolean isCurrent(String current) {
        return isCurrent(true, current);
    }

    /**
     * Checks if the current argument being typed matches the specific string. This can be set to be case sensitive
     *
     * @param ignoreCase True ignores case, false does not.
     * @param current The text to match
     * @return True if it matches, false if the current arg is null or if it doesnt match
     */
    public boolean isCurrent(boolean ignoreCase, String current) {
        return getCurrent() != null && (ignoreCase ? getCurrent().equalsIgnoreCase(current) : getCurrent().equals(current));
    }

    /**
     * Creates a player completion list at the current index.
     */
    public void playerCompletion() {
        possible.clear();
        possible.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
    }

    /**
     * Creates a player completion list at the specified index.
     *
     * @param index The index to create the completions
     */
    public void playerCompletionAt(int index) {
        if (isLength(index + 1)) playerCompletion();
    }

    /**
     * Creates a player completion list after the previous argument matches the given pattern.
     *
     * @param pattern The pattern to match
     */
    public void playerCompletionAfter(Pattern pattern) {
        if (isPrevious(pattern)) playerCompletion();
    }

    /**
     * Creates a player completion list after the previous argument matches the given string. This is not case
     * sensitive.
     *
     * @param previousArg The previous arg to match
     */
    public void playerCompletionAfter(String previousArg) {
        if (isPrevious(previousArg)) playerCompletion();
    }

    /**
     * Creates a player completion list after the previous argument matches the given string. The string can be case
     * sensitive.
     *
     * @param ignoreCase Whether to ignore case or not
     * @param previousArg The previous arg to match
     */
    public void playerCompletionAfter(boolean ignoreCase, String previousArg) {
        if (isPrevious(ignoreCase, previousArg)) playerCompletion();
    }

    /**
     * Creates a player completion list only if the predicate is true.
     *
     * @param predicate The predicate to test
     */
    public void playerCompletionIf(Predicate<TabContext> predicate) {
        if (predicate.test(this)) playerCompletion();
    }

    /**
     * Creates a completion list from the given arguments at the current index
     *
     * @param completions The arguments to add to the completion list
     */
    public void completion(String... completions) {
        possible.clear();
        possible.addAll(Arrays.asList(completions));
    }

    /**
     * Creates a completion list from the given arguments at the specified index.
     *
     * @param index The index to create the completions
     * @param completions The arguments to add to the completion list.
     */
    public void completionAt(int index, String... completions) {
        if (isLength(index+1)) completion(completions);
    }

    /**
     * Creates a completion list from the given arguments after the previous argument matches the specified pattern.
     *
     * @param pattern The pattern needed to match
     * @param completions The arguments to add to the completion list.
     */
    public void completionAfter(Pattern pattern, String... completions) {
        if (isPrevious(pattern)) completion(completions);
    }

    /**
     * Creates a completion list from the given arguments after the previous argument matches the specified string. This
     * is not case sensitive
     *
     * @param previousArg The previous arg to match
     * @param completions The arguments to add to the completion list.
     */
    public void completionAfter(String previousArg, String... completions) {
        if (isPrevious(previousArg)) completion(completions);
    }

    /**
     * Creates a completion list from the given arguments after the previous argument matches the specified string. This
     * can be set to case sensitive.
     *
     * @param ignoreCase Whether to ignore case or not.
     * @param previousArg The previous arg needed to be matched
     * @param completions The arguments to add to the completion list.
     */
    public void completionAfter(boolean ignoreCase, String previousArg, String... completions) {
        if (isPrevious(ignoreCase, previousArg)) completion(completions);
    }

    /**
     * Creates a completion list from the given arguments after the predicate given is true.
     *
     * @param predicate The predicate to test
     * @param completions The arguments to add to the completion list.
     */
    public void completionIf(Predicate<TabContext> predicate, String... completions) {
        if (predicate.test(this)) completion(completions);
    }

    /**
     * Creates a completion list from the given function
     *
     * @param function The function to apply
     */
    public void completion(Function<TabContext, List<String>> function) {
        possible.clear();
        possible.addAll(function.apply(this));
    }

    /**
     * Creates a completion list from the given function at the specified index.
     *
     * @param index The index to use the completions
     * @param function The function to apply
     */
    public void completionAt(int index, Function<TabContext, List<String>> function) {
        if (isLength(index+1)) completion(function);
    }

    /**
     * Creates a completion list from the given function after the previous argument matches the given regex
     *
     * @param pattern The pattern to match
     * @param function The function to apply
     */
    public void completionAfter(Pattern pattern, Function<TabContext, List<String>> function) {
        if (isPrevious(pattern)) completion(function);
    }

    /**
     * Creates a completion list from the given function after the previous argument matches the given string. This is
     * not case sensitive
     *
     * @param previousArg The previous arg to match
     * @param function The function to apply
     */
    public void completionAfter(String previousArg, Function<TabContext, List<String>> function) {
        if (isPrevious(previousArg)) completion(function);
    }

    /**
     * Creates a completion list from the given function after the previous argument matches the given string. This can
     * be set to case sensitive
     *
     * @param ignoreCase Whether to ignore case or not
     * @param previousArg The previous arg to match
     * @param function The function to apply
     */
    public void completionAfter(boolean ignoreCase, String previousArg, Function<TabContext, List<String>> function) {
        if (isPrevious(ignoreCase, previousArg)) completion(function);
    }

    /**
     * Creates a completion list from the given function after the given predicate is true.
     *
     * @param predicate The predicate to match
     * @param function The function to apply
     */
    public void completionIf(Predicate<TabContext> predicate, Function<TabContext, List<String>> function) {
        if (predicate.test(this)) completion(function);
    }

    /**
     * Runs a sub completion of this tab completion at the current index
     *
     * @param executor The tab executor (typically method reference)
     * @throws PDKCommandException If the completion was unsuccessful.
     */
    public void subCompletion(TabExecutor executor) throws PDKCommandException {
        executor.complete(this);
    }

    /**
     * Runs a sub completion of this tab completion at the specified index if the current index matches it.
     *
     * @param index The index needed to run this completion
     * @param executor The tab executor (typically method reference)
     * @return True if the sender type matched, false otherwise.
     * @throws PDKCommandException If the completion was unsuccessful.
     */
    public boolean subCompletionAt(int index, TabExecutor executor) throws PDKCommandException {
        if (isLength(index+1)) {
            subCompletion(executor);
            return true;
        }
        return false;
    }

    /**
     * Runs a sub completion of this tab completion at the specified index if the previous argument matches the given
     * pattern
     *
     * @param index The index needed to be able to run this completion
     * @param pattern The pattern needed to be matched
     * @param executor The tab executor (typically method reference)
     * @return True if the pattern matched the previous arg and the index matched the current index, false otherwise.
     * @throws PDKCommandException If the completion was unsuccessful
     */
    public boolean subCompletionAt(int index, Pattern pattern, TabExecutor executor) throws PDKCommandException {
        if (isLength(index+1) && isPrevious(pattern)) {
            subCompletion(executor);
            return true;
        }
        return false;
    }

    /**
     * Runs a sub completion of this tab completion at the specified index if the previous argument matches the given
     * string. This is not case sensitive
     *
     * @param index The index needed to be able to run this completion
     * @param previousArg The previous argument to match
     * @param executor The tab executor (typically method reference)
     * @return True if the string given matched the previous arg and the index matched the current index, false
     *         otherwise.
     * @throws PDKCommandException If the completion was unsuccessful
     */
    public boolean subCompletionAt(int index, String previousArg, TabExecutor executor) throws PDKCommandException {
        return subCompletionAt(index, true, previousArg, executor);
    }

    /**
     * Runs a sub completion of this tab completion at the specified index if the previous argument matches the given
     * string. This is able to be case sensitive
     *
     * @param index The index needed to be able to run this completion
     * @param ignoreCase Whether this is case sensitive or not.
     * @param previousArg The previous argument to match
     * @param executor The tab executor (typically method reference)
     * @return True if the string given matched the previous arg and the index matched the current index, false
     *         otherwise.
     * @throws PDKCommandException If the completion was unsuccessful
     */
    public boolean subCompletionAt(int index, boolean ignoreCase, String previousArg, TabExecutor executor) throws PDKCommandException {
        if (isLength(index+1) && isPrevious(ignoreCase, previousArg)) {
            subCompletion(executor);
            return true;
        }
        return false;
    }

    /**
     * Runs a sub completion of this tab completion at the specified index if the given predicate is true.
     *
     * @param predicate The predicate to test
     * @param executor The tab executor (typically method reference)
     * @return True if the predicate was true, false otherwise.
     * @throws PDKCommandException If the completion was unsuccessful.
     */
    public boolean subCompletionIf(Predicate<TabContext> predicate, TabExecutor executor) throws PDKCommandException {
        if (predicate.test(this)) {
            subCompletion(executor);
            return true;
        }
        return false;
    }

    //Get the current completions
    List<String> currentPossibleCompletions() {
        return possible;
    }

}
