package com.njdaeger.pdk.command.brigadier.builder;

public class CommandBuilder {

    public static IPdkRootNodeBuilder of(String... aliases) {
        return new PdkRootNodeBuilder(aliases);
    }

}
