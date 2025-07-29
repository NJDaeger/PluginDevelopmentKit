package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.njdaeger.pdk.command.brigadier.AsyncCommandContextImpl;
import com.njdaeger.pdk.command.brigadier.IAsyncCommandContext;
import com.njdaeger.pdk.command.brigadier.IAsyncCommandExecutor;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.IContextGenerator;
import com.njdaeger.pdk.command.brigadier.ISyncCommandExecutor;
import com.njdaeger.pdk.command.brigadier.PdkHelpTopic;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import com.njdaeger.pdk.command.brigadier.arguments.IPdkArgumentType;
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

public class PdkRootNode<EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends PdkCommandNode<EXECUTOR, CTX> implements IPdkRootNode<EXECUTOR, CTX> {

    private final List<IPdkCommandFlag<?>> flags;
    private final String description;
    private final BiFunction<IPdkRootNode<EXECUTOR, CTX>, CommandSender, TextComponent> customHelpTextGenerator;
    private final IContextGenerator<CTX> contextGenerator;
    private final List<String> aliases;

    public PdkRootNode(EXECUTOR executor, List<IPdkCommandNode<EXECUTOR, CTX>> arguments, List<IPdkCommandFlag<?>> flags, String description, PermissionMode permissionMode, String[] permissions, ArgumentBuilder<CommandSourceStack, ?> baseNode, BiFunction<IPdkRootNode<EXECUTOR, CTX>, CommandSender, TextComponent> customHelpTextGenerator, String[] aliases, IContextGenerator<CTX> contextGenerator) {
        super(executor, arguments, permissionMode, permissions, baseNode);
        this.flags = flags;
        this.aliases = List.of(aliases);
        this.description = description;
        this.contextGenerator = contextGenerator;
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
    public BiFunction<IPdkRootNode<EXECUTOR, CTX>, CommandSender, TextComponent> getCustomHelpTextGenerator() {
        return customHelpTextGenerator;
    }

    @Override
    public void register(Plugin plugin) {

        var rootNode = getBaseNode();

        if (getExecutor() != null) {
            if (flags.isEmpty()) rootNode.executes(commandExecutionWrapper(plugin, getPermissionMode(), getPermissions(), getExecutor()));
            else {
                var ffArg = new FlagFieldArgumentType(flags);
                ffArg.setContextGenerator(plugin, contextGenerator);
                rootNode.then(Commands.literal("flags:")
                                .then(Commands.argument("flags", ffArg).executes(commandExecutionWrapper(plugin, getPermissionMode(), getPermissions(), getExecutor()))))
                        .executes(commandExecutionWrapper(plugin, getPermissionMode(), getPermissions(), getExecutor()));
            }
        }
        
        getArguments().forEach(arg -> addArgument(plugin, rootNode, arg));
        
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, e -> {
            var registrar = e.registrar();
            getAliases().forEach(alias -> Bukkit.getHelpMap().addTopic(new PdkHelpTopic<>(alias, this)));
            registrar.register((LiteralCommandNode<CommandSourceStack>) rootNode.build(), getDescription(), getAliases());
        });
    }

    private void addArgument(Plugin plugin, ArgumentBuilder<CommandSourceStack, ?> parentArgument, IPdkCommandNode<EXECUTOR, CTX> newArgument) {
        if (newArgument instanceof IPdkTypedNode<?,EXECUTOR, CTX> typedNode && typedNode.getArgumentType() instanceof IPdkArgumentType<?,?> typedArg) typedArg.setContextGenerator(plugin, contextGenerator);
        final ArgumentBuilder<CommandSourceStack, ?> arg = newArgument.getBaseNode();

        newArgument.getArguments().forEach(childArg -> {
            if (childArg instanceof IPdkTypedNode<?, EXECUTOR, CTX> tArg && tArg.getArgumentType() instanceof FlagFieldArgumentType) return;
            else addArgument(plugin, arg, childArg);
        });

        //if the new argument is executable,
        if (newArgument.getExecutor() != null) {
            //flags are not compatible with greedy string arguments
            if (flags.isEmpty() || (newArgument instanceof IPdkTypedNode<?, EXECUTOR, CTX> tArg && (tArg.getArgumentType() instanceof GreedyStringArgument || (tArg.getArgumentType() instanceof StringArgumentType stArg && stArg.getType() == StringArgumentType.StringType.GREEDY_PHRASE))))
                arg.executes(commandExecutionWrapper(plugin, newArgument.getPermissionMode(), newArgument.getPermissions(), newArgument.getExecutor()));
            else {
                arg.then(Commands.literal("flags:").then(Commands.argument("flags", new FlagFieldArgumentType(flags)).executes(commandExecutionWrapper(plugin, newArgument.getPermissionMode(), newArgument.getPermissions(), newArgument.getExecutor()))))
                        .executes(commandExecutionWrapper(plugin, newArgument.getPermissionMode(), newArgument.getPermissions(), newArgument.getExecutor()));
            }
        }

        parentArgument.then(arg);
    }

    private Command<CommandSourceStack> commandExecutionWrapper(Plugin plugin, PermissionMode permissionMode, String[] permissions, EXECUTOR executor) {
        return (ctx) -> {
            try {
                if (permissions != null && permissions.length > 0 && permissionMode != null) {
                    if (permissionMode == PermissionMode.ANY && Stream.of(permissions).noneMatch(ctx.getSource().getSender()::hasPermission)) {
                        throw new PermissionDeniedException();
                    } else if (permissionMode == PermissionMode.ALL && Stream.of(permissions).anyMatch(permission -> !ctx.getSource().getSender().hasPermission(permission))) {
                        throw new PermissionDeniedException();
                    }
                }

                if (executor instanceof ISyncCommandExecutor syncExecutor) {
                    syncExecutor.execute(contextGenerator.generateContext(plugin, ctx));
                } else if (executor instanceof IAsyncCommandExecutor asyncExecutor) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            asyncExecutor.execute((IAsyncCommandContext) contextGenerator.generateContext(plugin, ctx));
                        } catch (PDKCommandException e) {
                            e.showError(ctx.getSource().getSender());
                        }
                    });
                }

            } catch (PDKCommandException e) {
                e.showError(ctx.getSource().getSender());
                return Command.SINGLE_SUCCESS;
            }
            return Command.SINGLE_SUCCESS;
        };
    }

}
