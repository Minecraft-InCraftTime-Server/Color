package ict.minesunshineone.chat.channel;

import org.bukkit.entity.Player;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.config.ChannelConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public abstract class AbstractChatChannel implements ChatChannel {

    protected final SimpleChat plugin;
    protected final String name;
    protected final String permission;
    protected final Component displayName;
    protected final Component description;
    protected String format;
    protected boolean crossServer;

    public AbstractChatChannel(SimpleChat plugin, String name, String permission, Component displayName, Component description) {
        this.plugin = plugin;
        this.name = name;
        this.permission = permission;
        this.displayName = displayName;
        this.description = description;

        // 从配置文件加载
        ChannelConfig config = ChannelConfig.load(plugin, name);
        if (config != null) {
            this.format = config.getFormat();
            this.crossServer = config.isCrossServer();
        } else {
            this.format = plugin.getConfig().getString("chat.format", "%luckperms_prefix%%player_name%%luckperms_suffix% &a&l>>&r %message%");
            this.crossServer = false;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Component getDisplayName() {
        return displayName;
    }

    @Override
    public boolean canJoin(Player player) {
        return permission == null || permission.isEmpty() || player.hasPermission(permission);
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public Component getDescription() {
        return description;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public boolean isCrossServer() {
        return crossServer;
    }

    protected Component formatMessage(Player player, Component message) {
        String formatStr = PlaceholderAPI.setPlaceholders(player, format);
        Component formatComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(formatStr);

        return formatComponent.replaceText(builder
                -> builder.matchLiteral("%message%")
                        .replacement(message)
        );
    }

    protected void sendMessage(Player sender, Component message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (canReceiveMessage(player)) {
                player.sendMessage(message);
            }
        }
    }

    protected boolean canReceiveMessage(Player player) {
        return canJoin(player);
    }
}
