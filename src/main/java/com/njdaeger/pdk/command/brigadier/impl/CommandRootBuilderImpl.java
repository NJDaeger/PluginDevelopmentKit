package com.njdaeger.pdk.command.brigadier.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.brigadier.IBuiltCommand;
import com.njdaeger.pdk.command.brigadier.ICommandArgument;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.ICommandFlag;
import com.njdaeger.pdk.command.brigadier.ICommandRootBuilder;
import com.njdaeger.pdk.command.brigadier.ITypedArgument;
import com.njdaeger.pdk.command.brigadier.arguments.FlagFieldArgumentType;
import com.njdaeger.pdk.command.brigadier.arguments.types.GreedyStringArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static com.njdaeger.pdk.command.brigadier.impl.ExecutionHelpers.rootCommandExecution;

@SuppressWarnings("UnstableApiUsage")
public class CommandRootBuilderImpl implements ICommandRootBuilder {

    private final String[] aliases;
    private String description;
    private String permission;
    private final List<ICommandFlag<?>> flags;
    private ICommandExecutor defaultExecutor;
    private ICommandExecutor rootExecutor;
    private final List<ICommandArgument> arguments;

    public CommandRootBuilderImpl(String... aliases) {
        this.aliases = aliases;
        this.flags = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.defaultExecutor = (ctx) -> {
            ctx.send("There is no default command executor defined.");
        };
    }

    @Override
    public ICommandRootBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public ICommandRootBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    @Override
    public ICommandRootBuilder flag(String flagName, String tooltipMessage) {
        flags.add(new CommandFlagImpl<>(flagName, tooltipMessage, null));
        return this;
    }

    @Override
    public <T> ICommandRootBuilder flag(String flagName, String tooltipMessage, ArgumentType<T> flagType) {
        flags.add(new CommandFlagImpl<>(flagName, tooltipMessage, flagType));
        return this;
    }

    @Override
    public ICommandRootBuilder defaultExecutor(ICommandExecutor executor) {
        this.defaultExecutor = executor;
        return this;
    }

    @Override
    public ICommandRootBuilder canExecute() {
        this.rootExecutor = (ctx) -> defaultExecutor.execute(ctx);
        return this;
    }

    @Override
    public ICommandRootBuilder canExecute(ICommandExecutor executor) {
        this.rootExecutor = executor;
        return this;
    }

    @Override
    public ICommandRootBuilder then(ICommandArgument argument) {
        argument.setDefaultExecutor(() -> defaultExecutor);
        arguments.add(argument);
        return this;
    }

    @Override
    public IBuiltCommand build(Plugin plugin) {

        var rootCommand =  new CommandRootImpl(plugin, aliases, description, permission, flags, defaultExecutor, arguments);
        var literalNodeBase = Commands.literal(aliases[0]);
        if (rootExecutor != null) {
            literalNodeBase.then(ICommandArgument.of("flags", new FlagFieldArgumentType(flags)).build().executes(rootCommandExecution(permission, rootExecutor))).executes(rootCommandExecution(permission, rootExecutor));
        }

        arguments.forEach(arg -> addArgument(literalNodeBase, arg));



        return new BuiltCommandImpl(rootCommand, literalNodeBase.build());
    }

    private void addArgument(ArgumentBuilder<CommandSourceStack, ?> argBuilder, ICommandArgument argument) {
        final ArgumentBuilder<CommandSourceStack, ?> arg = argument.build();

        argument.getArguments().forEach(innerArg -> {
            if (innerArg instanceof ITypedArgument<?> tArg && tArg.getType() instanceof FlagFieldArgumentType) return;
            addArgument(arg, innerArg);
        });

        if (argument.getExecutor() != null) {
            //flags are not compatible with greedy string arguments
            if (argument instanceof ITypedArgument<?> tArg && (tArg.getType() instanceof GreedyStringArgument || (tArg.getType() instanceof StringArgumentType stArg && stArg.getType() == StringArgumentType.StringType.GREEDY_PHRASE))) {
                argBuilder.then(arg.executes(rootCommandExecution(permission, argument.getExecutor())));
            }
            else arg.then(ICommandArgument.of("flags", new FlagFieldArgumentType(flags)).build().executes(rootCommandExecution(permission, argument.getExecutor()))).executes(rootCommandExecution(permission, argument.getExecutor()));
        }

        argBuilder.then(arg);
    }

}
