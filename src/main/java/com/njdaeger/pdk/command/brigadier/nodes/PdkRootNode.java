package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PdkHelpTopic;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import com.njdaeger.pdk.command.brigadier.flags.FlagFieldArgumentType;
import com.njdaeger.pdk.command.brigadier.arguments.defaults.GreedyStringArgument;
import com.njdaeger.pdk.command.brigadier.flags.IPdkCommandFlag;
import com.njdaeger.pdk.command.brigadier.CommandContextImpl;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.exception.PermissionDeniedException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class PdkRootNode extends PdkCommandNode implements IPdkRootNode {

    private final List<IPdkCommandFlag<?>> flags;
    private final String description;
    private final BiFunction<IPdkRootNode, CommandSender, TextComponent> customHelpTextGenerator;
    private final List<String> aliases;

    public PdkRootNode(ICommandExecutor executor, List<IPdkCommandNode> arguments, List<IPdkCommandFlag<?>> flags, String description, PermissionMode permissionMode, String[] permissions, ArgumentBuilder<CommandSourceStack, ?> baseNode, BiFunction<IPdkRootNode, CommandSender, TextComponent> customHelpTextGenerator, String[] aliases) {
        super(executor, arguments, permissionMode, permissions, baseNode);
        this.flags = flags;
        this.aliases = List.of(aliases);
        this.description = description;
        this.customHelpTextGenerator = customHelpTextGenerator;
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
    public BiFunction<IPdkRootNode, CommandSender, TextComponent> getCustomHelpTextGenerator() {
        return customHelpTextGenerator;
    }

    @Override
    public void register(Plugin plugin) {

        var rootNode = getBaseNode();

        if (getExecutor() != null) {
            if (flags.isEmpty()) rootNode.executes(commandExecutionWrapper(getPermissionMode(), getPermissions(), getExecutor()));
            else {
                rootNode.then(Commands.literal("flags:").then(Commands.argument("flags", new FlagFieldArgumentType(flags)).executes(commandExecutionWrapper(getPermissionMode(), getPermissions(), getExecutor()))))
                        .executes(commandExecutionWrapper(getPermissionMode(), getPermissions(), getExecutor()));
//                rootNode.then(Commands.argument("flags", new FlagFieldArgumentType(flags)).executes(commandExecutionWrapper(getPermissionMode(), getPermissions(), getExecutor())))
//                        .executes(commandExecutionWrapper(getPermissionMode(), getPermissions(), getExecutor()));
            }
        }
        
        getArguments().forEach(arg -> addArgument(rootNode, arg));
        
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, e -> {
            var registrar = e.registrar();
            getAliases().forEach(alias -> Bukkit.getHelpMap().addTopic(new PdkHelpTopic(alias, this)));
            registrar.register((LiteralCommandNode<CommandSourceStack>) rootNode.build(), getDescription(), getAliases());
        });
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
                arg.executes(commandExecutionWrapper(newArgument.getPermissionMode(), newArgument.getPermissions(), newArgument.getExecutor()));
            else {
                arg.then(Commands.literal("flags:").then(Commands.argument("flags", new FlagFieldArgumentType(flags)).executes(commandExecutionWrapper(newArgument.getPermissionMode(), newArgument.getPermissions(), newArgument.getExecutor()))))
                        .executes(commandExecutionWrapper(newArgument.getPermissionMode(), newArgument.getPermissions(), newArgument.getExecutor()));

//                arg.then(Commands.argument("flags", new FlagFieldArgumentType(flags)).executes(commandExecutionWrapper(newArgument.getPermissionMode(), newArgument.getPermissions(), newArgument.getExecutor())))
//                        .executes(commandExecutionWrapper(newArgument.getPermissionMode(), newArgument.getPermissions(), newArgument.getExecutor()));
            }
        }

        parentArgument.then(arg);
    }

    private static Command<CommandSourceStack> commandExecutionWrapper(PermissionMode permissionMode, String[] permissions, ICommandExecutor executor) {
        return (ctx) -> {
            try {
                if (permissions != null
                        && permissions.length > 0
                        && permissionMode != null
                        && ((permissionMode == PermissionMode.ANY && Stream.of(permissions).noneMatch(ctx.getSource().getSender()::hasPermission))
                            || (permissionMode == PermissionMode.ALL && Stream.of(permissions).anyMatch(permission -> !ctx.getSource().getSender().hasPermission(permission)))
                        )
                    ) throw new PermissionDeniedException();
                executor.execute(new CommandContextImpl(ctx));
            } catch (PDKCommandException e) {
                e.showError(ctx.getSource().getSender());
            }
            return Command.SINGLE_SUCCESS;
        };
    }
}
