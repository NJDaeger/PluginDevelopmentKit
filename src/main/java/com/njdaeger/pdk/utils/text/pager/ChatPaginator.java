package com.njdaeger.pdk.utils.text.pager;

import com.njdaeger.pdk.utils.text.Text;
import com.njdaeger.pdk.utils.text.pager.components.IComponent;
import org.bukkit.Color;
import org.bukkit.map.MinecraftFont;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ChatPaginator<T, B> {

    public static final int EQUAL_SIGN_WIDTH = MinecraftFont.Font.getWidth("=");
    private static final int EQUAL_SIGN_COUNT = 22;

    private final BiFunction<T, B, Text.Section> lineGenerator;
    private Color highlightColor = Color.fromRGB(0xFF55FF);
    private Color grayedOutColor = Color.fromRGB(0xB3A3B3);
    private Color grayColor = Color.fromRGB(0xAAAAAA);

    private final Map<ComponentPosition, IComponent<T, B>> components;

    public ChatPaginator(BiFunction<T, B, Text.Section> lineGenerator) {
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
    public static <T, B> ChatPaginatorBuilder<T, B> builder(BiFunction<T, B, Text.Section> lineGenerator) {
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
    private int getEqualSignCount(Text.Section section) {
        return (int) Math.ceil((Text.getMinecraftPixelWidth(section) * 1.0) / EQUAL_SIGN_WIDTH);
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
        var raw = section.asUnformattedString();
        var text = component.getText(generatorInfo, this, results, page);
        if (!raw.endsWith(" ")) text.appendRoot(" ");
        if (!raw.startsWith(" ")) return (info1, pager1, results1, currentPage1) -> Text.of(" ").appendRoot(text);
        return (info1, pager1, results1, currentPage1) -> text;
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
        if (page < 1 || page > maxPage) return null;

        var header = generateHeader(generatorInfo, results, page);
        var footer = generateFooter(generatorInfo, results, page);
        var body = generateBody(generatorInfo, results, page);

        return new PageResult<>(page, maxPage, header.appendRoot(body).appendRoot(footer), results);
    }

    /**
     * Generate the page body
     */
    private Text.Section generateBody(B generatorInfo, List<T> results, int page) {
        var body = Text.of("");
        var resultsForPage = results.stream().skip((page - 1) * 8L).limit(8).toList();
        resultsForPage.forEach(result -> body.appendRoot("\n").appendRoot(lineGenerator.apply(result, generatorInfo)));
        body.appendRoot("\n");
        return body;
    }

    /**
     * Generate the page header
     */
    private Text.Section generateHeader(B generatorInfo, List<T> results, int page) {

        //set both sides to EQUAL_SIGN_COUNT, the outermost = signs are always manually added
        int leftEqualSignCount = EQUAL_SIGN_COUNT;
        int rightEqualSignCount = EQUAL_SIGN_COUNT;
        var header = Text.of("=").setColor(grayColor); //left equal sign

        if (components.containsKey(ComponentPosition.TOP_LEFT)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.TOP_LEFT), results, page);
            if (text != null) {
                leftEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page);
                header.appendRoot(text.getText(generatorInfo, this, results, page));
                header.appendRoot("=").setColor(grayColor);
                --leftEqualSignCount;
            }
        }

        if (components.containsKey(ComponentPosition.TOP_CENTER)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.TOP_CENTER), results, page);
            if (text != null) {
                leftEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page) / 2;
                rightEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page) / 2;

                if(leftEqualSignCount > 0) header.appendRoot("=".repeat(leftEqualSignCount)).setColor(grayColor);
                header.appendRoot(text.getText(generatorInfo, this, results, page));
            } else header.appendRoot("=".repeat(leftEqualSignCount)).setColor(grayColor);
        } else {
            header.appendRoot("=".repeat(leftEqualSignCount)).setColor(grayColor);
        }

        if (components.containsKey(ComponentPosition.TOP_RIGHT)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.TOP_RIGHT), results, page);
            if (text != null) {
                rightEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page);

                header.appendRoot("=").setColor(grayColor);
                --rightEqualSignCount;

                if (rightEqualSignCount > 0) header.appendRoot("=".repeat(rightEqualSignCount)).setColor(grayColor);
                header.appendRoot(text.getText(generatorInfo, this, results, page));
            } else header.appendRoot("=".repeat(rightEqualSignCount)).setColor(grayColor);
        } else {
            header.appendRoot("=".repeat(rightEqualSignCount)).setColor(grayColor);
        }

        header.appendRoot("=").setColor(grayColor);//right equal sign

        return header;
    }

    /**
     * Generate the page footer
     */
    private Text.Section generateFooter(B generatorInfo, List<T> results, int page) {
        //set both sides to EQUAL_SIGN_COUNT, the outermost = signs are always manually added
        int leftEqualSignCount = EQUAL_SIGN_COUNT;
        int rightEqualSignCount = EQUAL_SIGN_COUNT;
        var footer = Text.of("=").setColor(grayColor); //left equal sign

        if (components.containsKey(ComponentPosition.BOTTOM_LEFT)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.BOTTOM_LEFT), results, page);
            if (text != null) {
                leftEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page);
                footer.appendRoot(text.getText(generatorInfo, this, results, page));
                footer.appendRoot("=").setColor(grayColor);
                --leftEqualSignCount;
            }
        }

        if (components.containsKey(ComponentPosition.BOTTOM_CENTER)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.BOTTOM_CENTER), results, page);
            if (text != null) {
                leftEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page) / 2;
                rightEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page) / 2;

                if(leftEqualSignCount > 0) footer.appendRoot("=".repeat(leftEqualSignCount)).setColor(grayColor);
                footer.appendRoot(text.getText(generatorInfo, this, results, page));
            } else footer.appendRoot("=".repeat(leftEqualSignCount)).setColor(grayColor);
        } else {
            footer.appendRoot("=".repeat(leftEqualSignCount)).setColor(grayColor);
        }

        if (components.containsKey(ComponentPosition.BOTTOM_RIGHT)) {
            var text = ensurePadding(generatorInfo, components.get(ComponentPosition.BOTTOM_RIGHT), results, page);
            if (text != null) {
                rightEqualSignCount -= getEqualSignCount(generatorInfo, text, results, page);

                footer.appendRoot("=").setColor(grayColor);
                --rightEqualSignCount;

                if (rightEqualSignCount > 0) footer.appendRoot("=".repeat(rightEqualSignCount)).setColor(grayColor);
                footer.appendRoot(text.getText(generatorInfo, this, results, page));
            } else footer.appendRoot("=".repeat(rightEqualSignCount)).setColor(grayColor);
        } else {
            footer.appendRoot("=".repeat(rightEqualSignCount)).setColor(grayColor);
        }

        footer.appendRoot("=").setColor(grayColor);//right equal sign

        return footer;
    }

    /**
     * Get the color to use for highlighted information
     * @return The color to use for highlighted information
     */
    public Color getHighlightColor() {
        return highlightColor;
    }

    /**
     * Set the color to use for highlighted information
     * @param highlightColor The color to use for highlighted information
     */
    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    /**
     * Get the color to use for information that normally is highlighted, but is not supposed to be at the moment
     * @return The color to use for information that normally is highlighted, but is not supposed to be at the moment
     */
    public Color getGrayedOutColor() {
        return grayedOutColor;
    }

    /**
     * Set the color to use for information that normally is highlighted, but is not supposed to be at the moment
     * @param grayedOutColor The color to use for information that normally is highlighted, but is not supposed to be at the moment
     */
    public void setGrayedOutColor(Color grayedOutColor) {
        this.grayedOutColor = grayedOutColor;
    }

    /**
     * Get the color to use for information that is not highlighted
     * @return The color to use for information that is not highlighted
     */
    public Color getGrayColor() {
        return grayColor;
    }

    /**
     * Set the color to use for information that is not highlighted
     * @param grayColor The color to use for information that is not highlighted
     */
    public void setGrayColor(Color grayColor) {
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
