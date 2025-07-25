package com.njdaeger.pdk.command.brigadier;

import com.njdaeger.pdk.command.brigadier.flags.IPdkCommandFlag;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkCommandNode;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkLiteralNode;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkRootNode;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkTypedNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PdkHelpTopic<EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends HelpTopic {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('§')
            .extractUrls()
            .hexColors()
            .build();

    private final IPdkRootNode<EXECUTOR, CTX> rootNode;

    public PdkHelpTopic(String alias, IPdkRootNode<EXECUTOR, CTX> rootNode) {
        this.rootNode = rootNode;
        this.name = alias;
        this.shortText = rootNode.getDescription();
    }

    @Override
    public @NotNull String getFullText(@NotNull CommandSender forWho) {
        if (rootNode.getCustomHelpTextGenerator() != null) {
            return LEGACY_SERIALIZER.serialize(rootNode.getCustomHelpTextGenerator().apply(rootNode, forWho));
        }
        return LEGACY_SERIALIZER.serialize(createFormattedHelpText(rootNode, forWho));
    }

    @Override
    public boolean canSee(@NotNull CommandSender sender) {
        return hasPermission(rootNode, sender);
    }

    /**
     * Generates formatted help text for a command based on its structure
     *
     * @param rootNode The command's root node
     * @return A BiFunction that generates formatted help text
     */
    public TextComponent createFormattedHelpText(IPdkRootNode<EXECUTOR, CTX> rootNode, CommandSender sender) {
        TextComponent.Builder builder = Component.text();

        if (!rootNode.getPrimaryAlias().equalsIgnoreCase(getName())) {
            builder.append(Component.text("Alias for: ", NamedTextColor.GOLD, TextDecoration.BOLD))
                    .append(Component.text(rootNode.getPrimaryAlias(), NamedTextColor.WHITE))
                    .appendNewline();
        }

        // Command description
        if (rootNode.getDescription() != null) {
            builder.append(Component.text("Description: ", NamedTextColor.GOLD, TextDecoration.BOLD))
                    .append(Component.text(rootNode.getDescription(), NamedTextColor.WHITE))
                    .appendNewline();
        }

        // Usage section
        builder.append(Component.text("Usage:", NamedTextColor.GOLD, TextDecoration.BOLD))
                .appendNewline();

        generateCommandUsage(rootNode, sender, builder);

        // Add flags section if there are flags
        if (!rootNode.getFlags().isEmpty()) {
            builder.append(Component.text("Flags:", NamedTextColor.GOLD, TextDecoration.BOLD))
                    .appendNewline();

            for (IPdkCommandFlag<?> flag : rootNode.getFlags()) {
                if (flag.isHidden()) continue; // Skip hidden flags
                builder.append(Component.text("  -" + flag.getName(), NamedTextColor.AQUA))
                        .append(Component.text(": " + flag.getTooltip(), NamedTextColor.GRAY))
                        .appendNewline();
            }
        }

        return builder.build();
    }

    private void generateCommandUsage(IPdkCommandNode<EXECUTOR, CTX> node, CommandSender sender, TextComponent.Builder builder) {
        if (node instanceof IPdkRootNode<EXECUTOR, CTX> localRootNode) {
            String baseCommand = "/" + localRootNode.getPrimaryAlias();

            // For simple commands with few arguments, generate full usage patterns
            List<String> usagePatterns = buildUsagePatterns(localRootNode, sender);
            //order the patterns alphabetically
            usagePatterns.sort(String::compareToIgnoreCase);
            for (String pattern : usagePatterns) {
                builder.append(Component.text("  " + baseCommand + " ", NamedTextColor.AQUA).append(Component.text(pattern, NamedTextColor.GRAY)))
                        .appendNewline();
            }
        }
    }

    private List<String> buildUsagePatterns(IPdkRootNode<EXECUTOR, CTX> rootNode, CommandSender sender) {
        // Simplified pattern generation
        List<String> patterns = new ArrayList<>();
        buildPatterns(rootNode, "", patterns, sender);
        return patterns;
    }

    private void buildPatterns(IPdkCommandNode<EXECUTOR, CTX> node, String currentPattern, List<String> patterns, CommandSender sender) {
        // Add current pattern if node is executable
        if (node.canExecute()) {
            patterns.add(currentPattern.trim());
        }

        // Process child arguments
        for (IPdkCommandNode<EXECUTOR, CTX> arg : node.getArguments()) {
            if (!hasPermission(arg, sender)) continue;
            String argPattern = getArgumentPattern(arg);
            buildPatterns(arg, currentPattern + " " + argPattern, patterns, sender);
        }
    }

    private String getArgumentPattern(IPdkCommandNode<EXECUTOR, CTX> node) {
        if (node instanceof IPdkLiteralNode<EXECUTOR, CTX> lit) {
            return lit.getLiteral();
        } else if (node instanceof IPdkTypedNode<?, EXECUTOR, CTX> typed) {
            return "<" + typed.getArgumentName() + ">";
        } else if (node instanceof IPdkRootNode<EXECUTOR, CTX> root) {
            return root.getPrimaryAlias();
        }
        return "";
    }

    private boolean hasPermission(IPdkCommandNode<EXECUTOR, CTX> node, CommandSender sender) {
        return (node.getPermissions() == null || node.getPermissionMode() == null)
                || (node.getPermissionMode() == PermissionMode.ANY && Stream.of(node.getPermissions()).anyMatch(sender::hasPermission))
                || (node.getPermissionMode() == PermissionMode.ALL && Stream.of(node.getPermissions()).allMatch(sender::hasPermission));
    }

}
