package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.flags.FlagFieldArgumentType;
import com.njdaeger.pdk.command.brigadier.arguments.defaults.GreedyStringArgument;
import com.njdaeger.pdk.command.brigadier.flags.IPdkCommandFlag;
import com.njdaeger.pdk.command.brigadier.CommandContextImpl;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.exception.PermissionDeniedException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PdkRootNode extends PdkCommandNode implements IPdkRootNode {

    private final List<IPdkCommandFlag<?>> flags;
    private final String description;
    private final List<String> aliases;

    public PdkRootNode(ICommandExecutor executor, List<IPdkCommandNode> arguments, List<IPdkCommandFlag<?>> flags, String description, String permission, ArgumentBuilder<CommandSourceStack, ?> baseNode, String[] aliases) {
        super(executor, arguments, permission, baseNode);
        this.flags = flags;
        this.aliases = List.of(aliases);
        this.description = description;
    }

    @Override
    public @NotNull List<String> getAliases() {
        return aliases;
    }

    @Override
    public @NotNull List<IPdkCommandFlag<?>> getFlags() {
        return flags;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void register(Plugin plugin) {

        var rootNode = getBaseNode();

        if (getExecutor() != null) {
            if (flags.isEmpty()) rootNode.executes(commandExecutionWrapper(getPermission(), getExecutor()));
            else rootNode.then(Commands.argument("flags", new FlagFieldArgumentType(flags)).executes(commandExecutionWrapper(getPermission(), getExecutor())))
                    .executes(commandExecutionWrapper(getPermission(), getExecutor()));
        }
        
        getArguments().forEach(arg -> addArgument(rootNode, arg));
        
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, e -> e.registrar().register((LiteralCommandNode<CommandSourceStack>) rootNode.build(), getDescription(), getAliases()));
    }

    private void addArgument(ArgumentBuilder<CommandSourceStack, ?> parentArgument, IPdkCommandNode newArgument) {
        final ArgumentBuilder<CommandSourceStack, ?> arg = newArgument.getBaseNode();

        newArgument.getArguments().forEach(childArg -> {
            if (childArg instanceof IPdkTypedNode<?> tArg && tArg.getArgumentType() instanceof FlagFieldArgumentType) return;
            else addArgument(arg, childArg);
        });

        //if the new argument is executable,
        if (newArgument.getExecutor() != null) {
            //flags are not compatible with greedy string arguments
            if (flags.isEmpty() || (newArgument instanceof IPdkTypedNode<?> tArg && (tArg.getArgumentType() instanceof GreedyStringArgument || (tArg.getArgumentType() instanceof StringArgumentType stArg && stArg.getType() == StringArgumentType.StringType.GREEDY_PHRASE))))
                arg.executes(commandExecutionWrapper(newArgument.getPermission(), newArgument.getExecutor()));
            else arg.then(Commands.argument("flags", new FlagFieldArgumentType(flags)).executes(commandExecutionWrapper(newArgument.getPermission(), newArgument.getExecutor())))
                    .executes(commandExecutionWrapper(newArgument.getPermission(), newArgument.getExecutor()));
        }

        parentArgument.then(arg);
    }

    private static Command<CommandSourceStack> commandExecutionWrapper(String permission, ICommandExecutor executor) {
        return (ctx) -> {
            try {
                if (permission != null && !ctx.getSource().getSender().hasPermission(permission)) throw new PermissionDeniedException();
                executor.execute(new CommandContextImpl(ctx));
            } catch (PDKCommandException e) {
                e.showError(ctx.getSource().getSender());
            }
            return Command.SINGLE_SUCCESS;
        };
    }
}
