package com.njdaeger.pdk.types;

public final class StringType extends ParsedType<String> {

    @Override
    public String parse(String input) {
        return input;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
