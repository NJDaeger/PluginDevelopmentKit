package com.njdaeger.pdk.command.exception;

import com.njdaeger.pdk.PDKException;
import com.njdaeger.pdk.command.PDKCommand;
import org.bukkit.command.CommandSender;

public class PDKCommandException extends Exception {

    private boolean quiet;

    public PDKCommandException(String message, boolean quiet) {
        super(message, null, true, false);
        this.quiet = quiet;
    }

    public PDKCommandException(String message) {
        this(message, false);
    }

    public PDKCommandException() {
        this(null, true);
    }

    /**
     * Shows the error message to the command sender whenever an error arises
     * @param sender The sender to send the message to
     */
    public void showError(CommandSender sender) {
        if (!quiet) sender.sendMessage(getMessage());
    }


    /*
    
    Builder.create("testresult")
        .literal("pass", "p").finishOr(this::pass).argument("player", PlayerArg.class).finish(this::passPlayer).next()
        .literal("fail", "f").finishOr(this::fail).argument("player", PlayerArg.class).finish(this::failPlayer).build();
    Generates: /testresult pass|fail [player]
    
    Builder.create("pass", "p")
        .finishOr(this::pass).argument("player", PlayerArg.class).finish(this::passPlayer).build();
    
    Builder.create("addperm")
        .literal("user", "u").argument("user", UserArg.class).argument("permission", StringListArg.class).flag(new WorldFlag()).finish(this::addUserPerm).next()
        .literal("group", "g").argument("group", GroupArg.class).argument("permission", StringListArg.class).flag(new WorldFlag()).finish(this.addGroupPerm).build()
        .completeArg("permission", this::permissionCompleter);
    
    Builder.create("execute")
        .literal("align").argument("axes", AxisArg.class).finish(this::align).next()
        .literal("anchored").literal("eyes", "feet").finish(this::anchored).next()
        .literal("as", "at").argument("targets", TargetsArg.class).finish(this::asOrAt).next()
        .literal("facing").argument("pos", PositionArg.class).finishOr(this::facingPosition).literal("entity").argument("targets", TargetsArg.class).literal("eyes", "feet").finish(this::facingEntity).next()
        .literal("in").argument("dimension", DimensionArg.class).finish(this::inDimension).next()
        .literal("store").literal("result", "success")
            .literalOr("block").argument("targetPos", TargetPosition.class).argument("path", Path.class).literal("byte", "short", "int", "long", "float", "double").argument("scale", ScaleArg.class).finish(this::storeBlock).next()
            .literalOr("bossbar").argument("id", IdArg.class).literal("max", "value").finish(this::executeBossbar).next()
            .literalOr("entity").argument("target", TargetArg.class).argument("path", Path.class).literal("byte", "short", "int", "long", "float", "double").argument("scale", ScaleArg.class).finish(this::storeEntity).next()
            .literalOr("score").argument("targets", TargetsArg.class).argument("objective", ObjectiveArg.class).finish(this::executeScore).next()
            .next()
        .literal()
        
        
        
    literal(String...) -> literal argument
    argument(String, Class) -> Replace with value of type Class
    finish(CommandExecutor) -> marks the last step of a usage chain
    finishOr(CommandExecutor) -> allows an optional breakpoint of the command chain
    next() -> go to the most recent
    
     */
}
