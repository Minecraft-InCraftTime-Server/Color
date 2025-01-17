package ict.minesunshineone.color.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ColorUtils {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .hexCharacter('#')
            .build();
    private static final PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");

    public static String convertHexToMiniMessage(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            matcher.appendReplacement(result, "<#" + hexColor + ">");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static Component formatText(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        text = convertHexToMiniMessage(text);

        Component result;
        if (text.contains("<") && text.contains(">")) {
            try {
                result = miniMessage.deserialize(text);
                if (!text.contains("&o")) {
                    return result.decoration(TextDecoration.ITALIC, false);
                }
                return result;
            } catch (Exception ignored) {
            }
        }

        try {
            result = legacySerializer.deserialize(text);
            if (!text.contains("&o")) {
                return result.decoration(TextDecoration.ITALIC, false);
            }
            return result;
        } catch (Exception e) {
            return Component.text(text).decoration(TextDecoration.ITALIC, false);
        }
    }

    public static String getPlainText(Component component) {
        return plainSerializer.serialize(component);
    }

    public static String getLegacyText(Component component) {
        return legacySerializer.serialize(component);
    }
}
