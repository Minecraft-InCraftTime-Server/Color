package ict.minesunshineone.color.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class AnvilListener implements Listener {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .hexCharacter('#')
            .build();

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result == null) {
            return;
        }

        Player player = (Player) event.getView().getPlayer();
        if (!player.hasPermission("colorcode.anvil.color")) {
            return;
        }

        ItemMeta meta = result.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String name = meta.getDisplayName();
        Component coloredName = SERIALIZER.deserialize(name)
                .decoration(TextDecoration.ITALIC, false);

        meta.displayName(coloredName);
        result.setItemMeta(meta);
        event.setResult(result);
    }
}
