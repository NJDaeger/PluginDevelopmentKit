package com.njdaeger.pdk.command.smartbuilder;

import java.util.Arrays;
import java.util.List;

public class LiteralPart implements Part {
    
    private final String[] literals;
    
    public LiteralPart(String... literals) {
        this.literals = literals;
    }
    
    public String[] getLiterals() {
        return literals;
    }
    
    public List<String> getLiteralList() {
        return Arrays.asList(literals);
    }
    
}
