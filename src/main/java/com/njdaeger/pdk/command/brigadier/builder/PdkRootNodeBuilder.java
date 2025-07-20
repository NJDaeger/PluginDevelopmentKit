package com.njdaeger.pdk.command.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
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

public class PdkRootNodeBuilder extends BasePdkCommandNodeBuilder<IPdkRootNodeBuilder, IPdkRootNodeBuilder> implements IPdkRootNodeBuilder {

    private String description;
    private final String[] aliases;
    private final List<IPdkCommandFlag<?>> commandFlags;
    private BiFunction<IPdkRootNode, CommandSender, TextComponent> customHelpTextGenerator;

    public PdkRootNodeBuilder(String[] aliases) {
        super((ctx) -> ctx.error("There is no default command executor defined."), null);
        this.commandFlags = new ArrayList<>();
        this.aliases = aliases;
    }

    @Override
    public IPdkRootNodeBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public IPdkRootNodeBuilder flag(String flagName, String tooltipMessage) {
        commandFlags.add(new PdkCommandFlag<>(flagName, tooltipMessage, false));
        return this;
    }

    @Override
    public IPdkRootNodeBuilder hiddenFlag(String flagName, String tooltipMessage) {
        commandFlags.add(new PdkCommandFlag<>(flagName, tooltipMessage, true));
        return this;
    }

    @Override
    public <T> IPdkRootNodeBuilder flag(String flagName, String tooltipMessage, ArgumentType<T> flagType) {
        commandFlags.add(new PdkCommandFlag<>(flagName, tooltipMessage, flagType, false));
        return this;
    }

    @Override
    public <T> IPdkRootNodeBuilder hiddenFlag(String flagName, String tooltipMessage, ArgumentType<T> flagType) {
        commandFlags.add(new PdkCommandFlag<>(flagName, tooltipMessage, flagType, true));
        return this;
    }

    @Override
    public IPdkRootNodeBuilder defaultExecutor(ICommandExecutor executor) {
        this.defaultExecutor = executor;
        return this;
    }

    @Override
    public IPdkRootNodeBuilder helpText(BiFunction<IPdkRootNode, CommandSender, TextComponent> componentGenerator) {
        this.customHelpTextGenerator = componentGenerator;
        return this;
    }

    @Override
    public PdkRootNodeBuilder permission(PermissionMode permissionmode, String... permissions) {
        this.permissionMode = permissionmode;
        this.permissions = permissions;
        return this;
    }

    @Override
    public PdkRootNodeBuilder canExecute() {
        this.commandExecutor = defaultExecutor;
        return this;
    }

    @Override
    public PdkRootNodeBuilder canExecute(ICommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    @Override
    @Deprecated
    public IPdkRootNodeBuilder executes() {
        throw new UnsupportedOperationException("A root node has no parent node to default back onto. Please use the canExecute() method to make this path execute the default executor.");
    }

    @Override
    @Deprecated
    public IPdkRootNodeBuilder executes(ICommandExecutor commandExecutor) {
        throw new UnsupportedOperationException("A root node has no parent node to default back onto. Please use the canExecute(ICommandExecutor) method to make this path execute the default executor.");
    }

    @Override
    @Deprecated
    public IPdkRootNodeBuilder end() {
        throw new UnsupportedOperationException("A root node has no parent node to default back onto. To finish building the command, use the build(Plugin) or register(Plugin) methods.");
    }

    @Override
    public IPdkRootNode build() {
        var children = childrenNodes.stream().map(IPdkCommandNodeBuilder::build).collect(Collectors.toCollection(ArrayList::new));
        return new PdkRootNode(commandExecutor, children, commandFlags, description, permissionMode, permissions, Commands.literal(aliases[0]), customHelpTextGenerator, aliases);
    }
}
