package com.njdaeger.pdk.command.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import com.njdaeger.pdk.command.brigadier.flags.IPdkCommandFlag;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkRootNode;
import com.njdaeger.pdk.command.brigadier.flags.PdkCommandFlag;
import com.njdaeger.pdk.command.brigadier.nodes.PdkRootNode;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class PdkRootNodeBuilder<EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends BasePdkCommandNodeBuilder<IPdkRootNodeBuilder<EXECUTOR, CTX>, IPdkRootNodeBuilder<EXECUTOR, CTX>, EXECUTOR, CTX> implements IPdkRootNodeBuilder<EXECUTOR, CTX> {

    private String description;
    private final String[] aliases;
    private final List<IPdkCommandFlag<?>> commandFlags;
    private BiFunction<IPdkRootNode<EXECUTOR, CTX>, CommandSender, TextComponent> customHelpTextGenerator;

    public PdkRootNodeBuilder(String[] aliases) {
        super((ctx) -> ctx.error("There is no default command executor defined."), null);
        this.commandFlags = new ArrayList<>();
        this.aliases = aliases;
    }

    @Override
    public IPdkRootNodeBuilder<EXECUTOR, CTX> description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public IPdkRootNodeBuilder<EXECUTOR, CTX> flag(String flagName, String tooltipMessage) {
        commandFlags.add(new PdkCommandFlag<>(flagName, tooltipMessage, false));
        return this;
    }

    @Override
    public IPdkRootNodeBuilder<EXECUTOR, CTX> hiddenFlag(String flagName, String tooltipMessage) {
        commandFlags.add(new PdkCommandFlag<>(flagName, tooltipMessage, true));
        return this;
    }

    @Override
    public <T> IPdkRootNodeBuilder<EXECUTOR, CTX> flag(String flagName, String tooltipMessage, ArgumentType<T> flagType) {
        commandFlags.add(new PdkCommandFlag<>(flagName, tooltipMessage, flagType, false));
        return this;
    }

    @Override
    public <T> IPdkRootNodeBuilder<EXECUTOR, CTX> hiddenFlag(String flagName, String tooltipMessage, ArgumentType<T> flagType) {
        commandFlags.add(new PdkCommandFlag<>(flagName, tooltipMessage, flagType, true));
        return this;
    }

    @Override
    public IPdkRootNodeBuilder<EXECUTOR, CTX> defaultExecutor(EXECUTOR executor) {
        this.defaultExecutor = executor;
        return this;
    }

    @Override
    public IPdkRootNodeBuilder<EXECUTOR, CTX> helpText(BiFunction<IPdkRootNode<EXECUTOR, CTX>, CommandSender, TextComponent> componentGenerator) {
        this.customHelpTextGenerator = componentGenerator;
        return this;
    }

    @Override
    public PdkRootNodeBuilder<EXECUTOR, CTX> permission(PermissionMode permissionmode, String... permissions) {
        this.permissionMode = permissionmode;
        this.permissions = permissions;
        return this;
    }

    @Override
    public PdkRootNodeBuilder<EXECUTOR, CTX> canExecute() {
        this.commandExecutor = defaultExecutor;
        return this;
    }

    @Override
    public PdkRootNodeBuilder<EXECUTOR, CTX> canExecute(EXECUTOR commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    @Override
    @Deprecated
    public IPdkRootNodeBuilder<EXECUTOR, CTX> executes() {
        throw new UnsupportedOperationException("A root node has no parent node to default back onto. Please use the canExecute() method to make this path execute the default executor.");
    }

    @Override
    @Deprecated
    public IPdkRootNodeBuilder<EXECUTOR, CTX> executes(EXECUTOR commandExecutor) {
        throw new UnsupportedOperationException("A root node has no parent node to default back onto. Please use the canExecute(ICommandExecutor) method to make this path execute the default executor.");
    }

    @Override
    @Deprecated
    public IPdkRootNodeBuilder<EXECUTOR, CTX> end() {
        throw new UnsupportedOperationException("A root node has no parent node to default back onto. To finish building the command, use the build(Plugin) or register(Plugin) methods.");
    }

    @Override
    public IPdkRootNode<EXECUTOR, CTX> build() {
        var children = childrenNodes.stream().map(IPdkCommandNodeBuilder::build).collect(Collectors.toCollection(ArrayList::new));
        return new PdkRootNode<>(commandExecutor, children, commandFlags, description, permissionMode, permissions, Commands.literal(aliases[0]), customHelpTextGenerator, aliases);
    }
}
