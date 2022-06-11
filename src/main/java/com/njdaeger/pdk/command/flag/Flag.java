package com.njdaeger.pdk.command.flag;

import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.TabContext;
import com.njdaeger.pdk.command.TabExecutor;
import com.njdaeger.pdk.command.exception.PDKCommandException;

import java.util.function.Predicate;

public abstract class Flag<T> implements TabExecutor {

    private final String usage;
    private final String splitter;
    private final String description;
    private final String flag;
    private final Class<T> type;
    private final boolean hasArgument;
    private final Predicate<TabContext> predicate;
    
    public Flag(Class<T> type, String description, String usage, String splitter, String aliases) {
        this.type = type;
        this.description = description;
        this.splitter = splitter;
        this.flag = aliases;
        this.usage = usage;
        this.hasArgument = true;
        this.predicate = null;
    }

    public Flag(Predicate<TabContext> onlyAllowWhen, Class<T> type, String description, String usage, String splitter, String aliases) {
        this.type = type;
        this.description = description;
        this.splitter = splitter;
        this.flag = aliases;
        this.usage = usage;
        this.hasArgument = true;
        this.predicate = onlyAllowWhen;
    }
    
    public Flag(Class<T> type, String description, String usage, String aliases) {
        this.type = type;
        this.description = description;
        this.splitter = null;
        this.flag = aliases;
        this.usage = usage;
        this.hasArgument = true;
        this.predicate = null;
    }

    public Flag(Predicate<TabContext> onlyAllowWhen, Class<T> type, String description, String usage, String aliases) {
        this.type = type;
        this.description = description;
        this.splitter = null;
        this.flag = aliases;
        this.usage = usage;
        this.hasArgument = true;
        this.predicate = onlyAllowWhen;
    }

    public Flag(String description, String usage, String aliases) {
        this.type = (Class<T>)Boolean.class;
        this.description = description;
        this.splitter = null;
        this.flag = aliases;
        this.usage = usage;
        this.hasArgument = false;
        this.predicate = null;
    }

    public Flag(Predicate<TabContext> onlyAllowWhen, String description, String usage, String aliases) {
        this.type = (Class<T>)Boolean.class;
        this.description = description;
        this.splitter = null;
        this.flag = aliases;
        this.usage = usage;
        this.hasArgument = false;
        this.predicate = onlyAllowWhen;
    }
    
    public Flag(String aliases) {
        this.type = (Class<T>)Boolean.class;
        this.description = null;
        this.usage = null;
        this.splitter = null;
        this.flag = aliases;
        this.hasArgument = false;
        this.predicate = null;
    }

    public Flag(Predicate<TabContext> onlyAllowWhen, String aliases) {
        this.type = (Class<T>)Boolean.class;
        this.description = null;
        this.usage = null;
        this.splitter = null;
        this.flag = aliases;
        this.hasArgument = false;
        this.predicate = onlyAllowWhen;
    }

   // public Flag(Class<T> type, String flag, boolean hasArgument, String splitter) {
       // this.type = type;
       // this.flag = flag;
       // this.hasArgument = hasArgument;
        /*
        
        possible formats:
                        (Class<T> argType, String flag, boolean eliminateUsed, String desc, String usage)
        -f              new Flag("description", "usage", [aliases])                  Just notifies whether the flag is present
        -f hello        new Flag(Player.class, "description", "usage", [aliases])
        -flag
        -flag hello
        f:hi            new Flag(Player.class, "description", "usage", "splitter", [aliases])
        f: hi
        flag:hi
        flag: hi
        
        
         */
    //}

    /**
     * Called when the command executor finds a flag
     * @param context The command context after removing the flag and its possible following argument from the argument array
     * @param argument The argument which was after the given flag. Or null if the flag didnt have an argument
     * @return The parsed value to be passed to the command context for the final execution run
     */
    public abstract T parse(CommandContext context, String argument) throws PDKCommandException;

    /**
     * The aliases for this flag
     * @return The aliases for this flag
     */
    public String getIndicator() {
        return flag;
    }

    /**
     * Whether this flag has an argument after it.
     * @return True if the flag is supposed to have an argument after it. If the flag does not have a type specifier, this will return false.
     */
    public boolean hasArgument() {
        return hasArgument;
    }

    /**
     * The type of data which is being returned after the flag is parsed.
     * @return The type of data the flag returns
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Returns what the splitter of this flag is if it is a split flag.
     * @return The splitter splitting the flag and the argument.
     */
    public String getSplitter() {
        return splitter;
    }

    /**
     * Whether this flag is a split flag or not.
     * @return True if the flag is a split flag, false otherwise.
     */
    public boolean hasSplitter() {
        return splitter != null;
    }

    /**
     * Get the description of this flag.
     * @return The description of this flag.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get usage information of this flag
     * @return The flag usage information
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Determines when this flag can be used in a command.
     * @return The predicate which determines when this flag can be used, or null if there is no usage restriction.
     */
    public Predicate<TabContext> getAllowWhen() {
        return predicate;
    }

    public String getRawFlag() {
        return hasSplitter() ? getIndicator() + getSplitter() : "-" + getIndicator();
    }

}
