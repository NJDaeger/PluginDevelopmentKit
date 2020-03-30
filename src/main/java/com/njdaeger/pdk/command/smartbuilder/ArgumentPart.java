package com.njdaeger.pdk.command.smartbuilder;

import com.njdaeger.pdk.types.ParsedType;

public class ArgumentPart<R, T extends ParsedType<R>> implements Part {
    
    public final Class<T> typeParser;
    public final String argumentName;
    
    public ArgumentPart(String argumentName, Class<T> typeParser) {
        this.argumentName = argumentName;
        this.typeParser = typeParser;
    }
    
}
