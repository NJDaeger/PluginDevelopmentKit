package com.njdaeger.pdk.types;

import com.njdaeger.pdk.command.exception.ArgumentParseException;

public final class IntegerType extends ParsedType<Integer> {

    @Override
    public Integer parse(String input) throws ArgumentParseException {
        int parsed;
        try {
            parsed = Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            throw new ArgumentParseException("Integer argument unable to be parsed. Input: " + input);
        }
        return parsed;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
