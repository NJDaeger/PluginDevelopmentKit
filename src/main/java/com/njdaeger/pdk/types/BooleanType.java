package com.njdaeger.pdk.types;

import com.njdaeger.pdk.command.exception.ArgumentParseException;

public final class BooleanType extends ParsedType<Boolean> {

    @Override
    public Boolean parse(String input) throws ArgumentParseException {
        if (input == null || (!input.equalsIgnoreCase("true") && !input.equalsIgnoreCase("false"))) throw new ArgumentParseException("Boolean argument unable to be parsed. Input: " + input);
        return input.equalsIgnoreCase("true");
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}
