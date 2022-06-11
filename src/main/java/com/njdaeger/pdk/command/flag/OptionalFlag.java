package com.njdaeger.pdk.command.flag;

import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.TabContext;
import com.njdaeger.pdk.command.exception.PDKCommandException;

import java.util.function.Predicate;

public class OptionalFlag extends Flag<Boolean> {

    public OptionalFlag(Predicate<TabContext> onlyAllowWhen, String description, String usage, String alias) {
        super(onlyAllowWhen, description, usage, alias);
    }

    public OptionalFlag(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    @Override
    public Boolean parse(CommandContext context, String argument) {
        return true;
    }

    @Override
    public void complete(TabContext context) throws PDKCommandException {

    }
}
