package com.njdaeger.pdk.command;

import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.flag.Flag;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDKCommand {

    private CommandExecutor commandExecutor = null;
    private final Map<String, Flag<?>> flags;
    private TabExecutor tabExecutor = null;
    private final String[] aliases;
    private String[] permissions = new String[0];
    private String description = "";
    private String usage = "";
    private int minArgs = -1;
    private int maxArgs = -1;

    /**
     * Creates a new PDKCommand object
     *
     * @param nameAndAliases The name and aliases of this command
     */
    public PDKCommand(String[] nameAndAliases) {
        Validate.notEmpty(nameAndAliases, "You must specify a name for the command.");
        this.aliases = nameAndAliases;
        this.flags = new HashMap<>();
    }

    /**
     * Get the name of this command
     *
     * @return The name of this command
     */
    public String getName() {
        return aliases[0];
    }

    /**
     * Gets the possible flags for this command
     *
     * @return The commands possible flags
     */
    public List<Flag<?>> getFlags() {
        return new ArrayList<>(flags.values());
    }

    /**
     * Adds a flag to this command
     *
     * @param flag The flag to add
     * @param <T> The type of flag being added
     */
    public <T extends Flag<?>> void addFlag(T flag) {
        flags.put(flag.getIndicator(), flag);
    }

    /**
     * Removes a flag from this command
     *
     * @param flag The flag to remove
     * @param <T> The type of flag to remove
     */
    public <T extends Flag<?>> void removeFlag(T flag) {
        flags.remove(flag.getIndicator());
    }

    /**
     * Checks if this command can have a particular flag
     *
     * @param flag The flag to check
     * @return True if this command can have the particular flag
     */
    public boolean hasFlag(String flag) {
        return flags.containsKey(flag);
    }

    /**
     * Checks if this command can have any flags at all
     *
     * @return True if this command can have flags, false otherwise.
     */
    public boolean hasFlags() {
        return !flags.isEmpty();
    }

    /**
     * Get the tab executor for this command
     *
     * @return The command tab executor
     */
    public TabExecutor getTabExecutor() {
        return tabExecutor;
    }

    /**
     * Set the tab executor for this command
     *
     * @param tabExecutor The command tab executor
     */
    public void setTabExecutor(TabExecutor tabExecutor) {
        this.tabExecutor = tabExecutor;
    }

    /**
     * Get the command executor for this command
     *
     * @return The command executor
     */
    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    /**
     * Set the command executor for this command
     *
     * @param commandExecutor The command executor
     */
    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    /**
     * Sets the description of the command
     *
     * @param description The description of the command
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the description of this command
     *
     * @return The description of this command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the usage of this command
     *
     * @return The command usage
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Sets the usage of this command
     *
     * @param usage THe usage of the command
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Gets the aliases of this command
     *
     * @return The command aliases
     */
    public String[] getAliases() {
        return aliases.length > 1 ? Arrays.copyOfRange(aliases, 1, aliases.length) : aliases;
    }

    /**
     * Gets the permissions required for this command
     *
     * @return The required permissions for this command
     */
    public String[] getPermissions() {
        return permissions;
    }

    /**
     * Sets the permissions required for this command
     *
     * @param permissions The required permissions for this command
     */
    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    /**
     * Gets the minimum args for this command
     *
     * @return The minimum amount of args allowed to run this command
     */
    public int getMinArgs() {
        return minArgs;
    }

    /**
     * Sets the minimum args for this command
     *
     * @param minArgs The minimum amount of args allowed to run this command
     */
    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    /**
     * Gets the maximum args for this command
     *
     * @return The maximum amount of args allowed to run this command
     */
    public int getMaxArgs() {
        return maxArgs;
    }

    /**
     * Sets the maximum args for this command.
     *
     * @param maxArgs The maximum amount of args allowed to run this command
     */
    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    /**
     * Registers this command for a plugin
     *
     * @param plugin The plugin to register this command for
     */
    public void register(Plugin plugin) {
        CommandRegistration.registerCommand(plugin, this);
    }

    /**
     * Checks to see if the command passes the minimum argument check
     *
     * @param context The command context
     * @throws PDKCommandException If there arent enough arguments supplied to run this command
     */
    public void minimumCheck(CommandContext context) throws PDKCommandException {
        if (context.getLength() < minArgs && minArgs > -1) context.notEnoughArgs();
    }

    /**
     * Checks to see if the command passes the maximum argument check
     *
     * @param context The command context
     * @throws PDKCommandException If there are too many arguments supplied to run this command
     */
    public void maximumCheck(CommandContext context) throws PDKCommandException {
        if (context.getLength() > maxArgs && maxArgs > -1) context.tooManyArgs();
    }

    /**
     * Checks to see if the sender has permission to run this command
     *
     * @param context The command context
     * @throws PDKCommandException If the sender does not have permission to run this command
     */
    public void permissionCheck(CommandContext context) throws PDKCommandException {
        if (permissions != null && !context.hasAnyPermission(permissions)) context.noPermission();
    }

    public void execute(CommandContext context) {
        try {
            permissionCheck(context);

            if (hasFlags() && context.hasArgs()) ArgumentParser.parseArguments(context, false);

            minimumCheck(context);
            maximumCheck(context);

            if (commandExecutor != null) commandExecutor.execute(context);
        } catch (PDKCommandException e) {
            e.showError(context.getSender());
        }
    }

    public List<String> complete(TabContext context) {
        try {
            //if (hasFlags() && context.hasArgs()) ArgumentParser.parseArguments(context, true);

            if (context.getCurrent() == null) return computePossible(context.currentPossibleCompletions(), context, false);
            boolean completingFlags = false;
            for (Flag<?> flag : flags.values()) {
                //if (context.hasFlag(flag.getIndicator())) continue;
                if (flag.hasArgument()) {
                    if (flag.hasSplitter()) {
                        if (context.getCurrent().startsWith(flag.getRawFlag()) || context.isPrevious(flag.getRawFlag())) {
                            flag.complete(context);
                            completingFlags = true;
                        }
                    } else {
                        if (context.isPrevious(flag.getRawFlag())) {
                            flag.complete(context);
                            completingFlags = true;
                        }
                    }
                }
            }
            if (hasFlags() && context.hasArgs()) ArgumentParser.parseArguments(context, true);
            if (tabExecutor != null && !completingFlags) tabExecutor.complete(context);
            return computePossible(context.currentPossibleCompletions(), context, completingFlags);

        } catch (PDKCommandException e) {
            e.showError(context.getSender());
        }
        return computePossible(context.currentPossibleCompletions(), context, false);
    }

    private List<String> computePossible(List<String> currentPossible, TabContext context, boolean completingFlags) {
        if (hasFlags() && !completingFlags) {
            for (String flag : flags.keySet()) {
                if (!context.hasFlag(flag)) currentPossible.add(flags.get(flag).getRawFlag());
            }
        }
        List<String> possible = new ArrayList<>();
        List<String> fuzzyPossible = new ArrayList<>();
        for (String completion : currentPossible) {
            if (context.getCurrent() == null || context.getCurrent().isEmpty()) {
                return currentPossible;
            }
            if (completion.toLowerCase().startsWith(context.getCurrent().toLowerCase())) {
                possible.add(completion);
            } else if (completion.toLowerCase().contains(context.getCurrent().toLowerCase())) {
                fuzzyPossible.add(completion);
            }
        }
        possible.addAll(fuzzyPossible);
        return possible;
    }

}
