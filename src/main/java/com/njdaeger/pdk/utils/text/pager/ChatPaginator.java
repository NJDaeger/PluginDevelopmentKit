package com.njdaeger.pdk.utils.text.pager;

import com.njdaeger.pdk.utils.text.TextUtils;
import com.njdaeger.pdk.utils.text.pager.components.IComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.map.MinecraftFont;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ChatPaginator<T, B> {

    public static final int EQUAL_SIGN_WIDTH = MinecraftFont.Font.getWidth("=");
    private static final int EQUAL_SIGN_COUNT = 22;

    private final BiFunction<T, B, TextComponent> lineGenerator;

    private TextColor grayColor = TextColor.color(170, 170, 170);
    private TextColor grayedOutColor = TextColor.color(179, 163, 179);
    private TextColor highlightColor = TextColor.color(255, 85, 255);

    private final Map<ComponentPosition, IComponent<T, B>> components;

    public ChatPaginator(BiFunction<T, B, TextComponent> lineGenerator) {
        this.lineGenerator = lineGenerator;
        this.components = new HashMap<>();
    }

    /**
     * Creates a new ChatPaginatorBuilder with the given line generator.
     * @param lineGenerator The line generator to use.
     * @param <T> The type of object to generate lines for.
     * @param <B> The type of object to use as generator info.
     * @return A new ChatPaginatorBuilder.
     */
    public static <T, B> ChatPaginatorBuilder<T, B> builder(BiFunction<T, B, TextComponent> lineGenerator) {
        return new ChatPaginatorBuilder<>(lineGenerator);
    }

    /**
     * Get the amount of equal signs that a components text will take up.
     *
     * @param generatorInfo The generator info to use.
     * @param component The component to get the equal sign count for.
     * @param results The results to get the text for.
     * @param page The page to get the text for.
     * @return The amount of equal signs that the components text will take up.
     */
    private int getEqualSignCount(B generatorInfo, IComponent<T, B> component, List<T> results, int page) {
        return getEqualSignCount(component.getText(generatorInfo, this, results, page));
    }

    /**
     * Get the amount of equal signs that a section will take up.
     * @param section The section to get the equal sign count for.
     * @return The amount of equal signs that the section will take up.
     */
    private int getEqualSignCount(TextComponent section) {
        return (int) Math.ceil((TextUtils.getMinecraftPixelWidth(section) * 1.0) / EQUAL_SIGN_WIDTH);
    }

    /**
     * Ensure a component has the correct amount of padding
     * @param generatorInfo The generator info to use.
     * @param component The component to ensure padding for.
     * @param results The results to get the text for.
     * @param page The page to get the text for.
     * @return The component with the correct amount of padding.
     */
    private IComponent<T, B> ensurePadding(B generatorInfo, IComponent<T, B> component, List<T> results, int page) {
        var section = component.getText(generatorInfo, this, results, page);
        if (section == null) return null;
        var raw = section.content();
        var text = component.getText(generatorInfo, this, results, page);
        if (!raw.endsWith(" ")) text = (TextComponent) text.appendSpace();
        if (!raw.startsWith(" ")) {
            TextComponent finalText = text;
            return (info1, pager1, results1, currentPage1) -> Component.space().append(finalText);
        }
        TextComponent finalText1 = text;
        return (info1, pager1, results1, currentPage1) -> finalText1;
    }

    /**
     * Generate the page to send to the caller.
     * @param generatorInfo The object that contains additional information that needs to be used to generate the page.
     * @param results The results to generate the page for.
     * @param page The page to generate.
     * @return The generated page. Null if the page is out of bounds.
     */
    public PageResult<T> generatePage(B generatorInfo, List<T> results, int page) {
        int maxPage = (int) Math.ceil(results.size() / 8.0);
        if (page < 1 || page > maxPage) return new PageResult<>(page, maxPage, null, results);

        var header = generateHeader(generatorInfo, results, page);
        var footer = generateFooter(generatorInfo, results, page);
        var body = generateBody(generatorInfo, results, page);

        return new PageResult<>(page, maxPage, header.append(body).append(footer).build(), results);
    }

    /**
     * Generate the page body
     */
    private TextComponent.Builder generateBody(B generatorInfo, List<T> results, int page) {
        var body = Component.text();
        var resultsForPage = results.stream().skip((page - 1) * 8L).limit(8).toList();
        resultsForPage.forEach(result -> body.appendNewline().append(lineGenerator.apply(result, generatorInfo)));
        body.appendNewline();
        return body;
    }

    /**
     * Generate the page header
     */
    private TextComponent.Builder generateHeader(B generatorInfo, List<T> results, int page) {

        //set both sides to EQUAL_SIGN_COUNT, the outermost = signs are always manually added
        int leftEqualSignCount = EQUAL_SIGN_COUNT;
        int rightEqualSignCount = EQUAL_SIGN_COUNT;
        var header = Component.text();
        header.append(Component.text("=", grayColor)); //left equal sign

        if (components.containsKey(ComponentPosition.TOP_LEFT)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.TOP_LEFT), results, page);
            if (text != null) {
                leftEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page);
                header.append(text.getText(generatorInfo, this, results, page));
                header.append(Component.text("=", grayColor));
                --leftEqualSignCount;
            }
        }

        if (components.containsKey(ComponentPosition.TOP_CENTER)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.TOP_CENTER), results, page);
            if (text != null) {
                leftEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page) / 2;
                rightEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page) / 2;

                if(leftEqualSignCount > 0) header.append(Component.text("=".repeat(leftEqualSignCount), grayColor));
                header.append(text.getText(generatorInfo, this, results, page));
            } else header.append(Component.text("=".repeat(leftEqualSignCount), grayColor));
        } else header.append(Component.text("=".repeat(leftEqualSignCount), grayColor));

        if (components.containsKey(ComponentPosition.TOP_RIGHT)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.TOP_RIGHT), results, page);
            if (text != null) {
                rightEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page);
                header.append(Component.text("=", grayColor));
                --rightEqualSignCount;

                if (rightEqualSignCount > 0) header.append(Component.text("=".repeat(rightEqualSignCount), grayColor));
                header.append(text.getText(generatorInfo, this, results, page));
            } else header.append(Component.text("=".repeat(rightEqualSignCount), grayColor));
        } else header.append(Component.text("=".repeat(rightEqualSignCount), grayColor));

        header.append(Component.text("=", grayColor));//right equal sign

        return header;
    }

    /**
     * Generate the page footer
     */
    private TextComponent.Builder generateFooter(B generatorInfo, List<T> results, int page) {
        //set both sides to EQUAL_SIGN_COUNT, the outermost = signs are always manually added
        int leftEqualSignCount = EQUAL_SIGN_COUNT;
        int rightEqualSignCount = EQUAL_SIGN_COUNT;
        var footer = Component.text();
        footer.append(Component.text("=", grayColor)); //left equal sign

        if (components.containsKey(ComponentPosition.BOTTOM_LEFT)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.BOTTOM_LEFT), results, page);
            if (text != null) {
                leftEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page);
                footer.append(text.getText(generatorInfo, this, results, page));
                footer.append(Component.text("=", grayColor));
                --leftEqualSignCount;
            }
        }

        if (components.containsKey(ComponentPosition.BOTTOM_CENTER)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.BOTTOM_CENTER), results, page);
            if (text != null) {
                leftEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page) / 2;
                rightEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page) / 2;

                if (leftEqualSignCount > 0) footer.append(Component.text("=".repeat(leftEqualSignCount), grayColor));
                footer.append(text.getText(generatorInfo, this, results, page));

            } else footer.append(Component.text("=".repeat(leftEqualSignCount), grayColor));
        } else  footer.append(Component.text("=".repeat(leftEqualSignCount), grayColor));

        if (components.containsKey(ComponentPosition.BOTTOM_RIGHT)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.BOTTOM_RIGHT), results, page);
            if (text != null) {
                rightEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page);
                footer.append(Component.text("=", grayColor));
                --rightEqualSignCount;

                if (rightEqualSignCount > 0) footer.append(Component.text("=".repeat(rightEqualSignCount), grayColor));
                footer.append(text.getText(generatorInfo, this, results, page));
            } else footer.append(Component.text("=".repeat(rightEqualSignCount), grayColor));
        } else footer.append(Component.text("=".repeat(rightEqualSignCount), grayColor));

        footer.append(Component.text("=", grayColor));//right equal sign

        return footer;
    }

    /**
     * Get the color to use for highlighted information
     * @return The color to use for highlighted information
     */
    public TextColor getHighlightColor() {
        return highlightColor;
    }

    /**
     * Set the color to use for highlighted information
     * @param highlightColor The color to use for highlighted information
     */
    public void setHighlightColor(TextColor highlightColor) {
        this.highlightColor = highlightColor;
    }

    /**
     * Get the color to use for information that normally is highlighted, but is not supposed to be at the moment
     * @return The color to use for information that normally is highlighted, but is not supposed to be at the moment
     */
    public TextColor getGrayedOutColor() {
        return grayedOutColor;
    }

    /**
     * Set the color to use for information that normally is highlighted, but is not supposed to be at the moment
     * @param grayedOutColor The color to use for information that normally is highlighted, but is not supposed to be at the moment
     */
    public void setGrayedOutColor(TextColor grayedOutColor) {
        this.grayedOutColor = grayedOutColor;
    }

    /**
     * Get the color to use for information that is not highlighted
     * @return The color to use for information that is not highlighted
     */
    public TextColor getGrayColor() {
        return grayColor;
    }

    /**
     * Set the color to use for information that is not highlighted
     * @param grayColor The color to use for information that is not highlighted
     */
    public void setGrayColor(TextColor grayColor) {
        this.grayColor = grayColor;
    }

    /**
     * Add a component to the page
     * @param component The component to add
     * @param position The position to add the component to
     */
    public void addComponent(IComponent<T, B> component, ComponentPosition position) {
        components.put(position, component);
    }
}
