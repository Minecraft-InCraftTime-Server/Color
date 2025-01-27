package ict.minesunshineone.simplechatbridge.velocity;

import java.util.Optional;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

@Plugin(
        id = "simplechat-bridge",
        name = "SimpleChatBridge",
        version = "1.0-SNAPSHOT",
        description = "A bridge plugin for SimpleChat",
        authors = {"MineSunshineOne"}
)
public class SimpleChatBridgeVelocity {

    private final ProxyServer server;
    private final Logger logger;
    private static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("simplechat:chat");

    @Inject
    public SimpleChatBridgeVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // 注册插件消息通道
        server.getChannelRegistrar().register(CHANNEL);
        logger.info("SimpleChatBridge for Velocity has been enabled!");
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL)) {
            return;
        }

        // 防止消息循环
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        // 获取源服务器
        if (event.getSource() instanceof Player player) {
            Optional<RegisteredServer> sourceServer = player.getCurrentServer().map(connection -> connection.getServer());
            if (sourceServer.isEmpty()) {
                return;
            }

            // 转发到所有其他服务器
            byte[] message = event.getData();
            for (RegisteredServer targetServer : server.getAllServers()) {
                if (!targetServer.equals(sourceServer.get())) {
                    targetServer.sendPluginMessage(CHANNEL, message);
                    logger.info("转发消息到服务器: " + targetServer.getServerInfo().getName());
                }
            }
        }
    }
}
