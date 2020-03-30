package com.njdaeger.pdk.command.smartbuilder;

import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.PDKCommand;
import com.njdaeger.pdk.command.TabContext;
import com.njdaeger.pdk.command.TabExecutor;

public class SmartCommandBuilder {
    
    public static SmartCommandBuilder create(String... command) {
        return null;
    }
    
    private final PDKCommand command;
    private CommandChain chain;
    private int depth;
    
    private SmartCommandBuilder(String... aliases) {
        this.command = new PDKCommand(aliases);
        this.chain = new CommandChain();
    }
    
    public SmartCommandBuilder literal(String... literals) {
        //chain.
        return this;
    }
    
    public PDKCommand build() {
        command.setCommandExecutor(this::execute);
        command.setTabExecutor(this::tabComplete);
        return command;
    }
    
    private void tabComplete(TabContext context) {
    
    }
    
    private void execute(CommandContext context) {
    
    }
    
}
