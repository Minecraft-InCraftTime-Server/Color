package ict.minesunshineone.color.utils;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentUtils {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
        .character('&')
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .hexCharacter('#')
        .build();
    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();

    public static LegacyComponentSerializer legacySerializer() {
        return SERIALIZER;
    }

    public static PlainTextComponentSerializer plainSerializer() {
        return PLAIN_SERIALIZER;
    }

}
