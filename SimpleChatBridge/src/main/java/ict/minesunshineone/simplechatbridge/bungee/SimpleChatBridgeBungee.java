package ict.minesunshineone.simplechatbridge.bungee;

import java.util.Collection;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class SimpleChatBridgeBungee extends Plugin implements Listener {

    private static final String CHANNEL = "simplechat:chat";

    @Override
    public void onEnable() {
        // 注册插件消息通道
        getProxy().registerChannel(CHANNEL);

        // 注册事件监听器
        getProxy().getPluginManager().registerListener(this, this);

        getLogger().info("SimpleChatBridge for BungeeCord has been enabled!");
        getLogger().info("注册的消息通道: " + CHANNEL);
    }

    @Override
    public void onDisable() {
        // 注销插件消息通道
        getProxy().unregisterChannel(CHANNEL);

        getLogger().info("SimpleChatBridge for BungeeCord has been disabled!");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals(CHANNEL)) {
            return;
        }

        // 防止消息循环
        event.setCancelled(true);

        // 获取源服务器
        if (event.getSender() instanceof Server) {
            ServerInfo sourceServer = ((Server) event.getSender()).getInfo();
            getLogger().info("收到来自服务器 " + sourceServer.getName() + " 的消息");

            // 转发到所有其他服务器
            byte[] message = event.getData();
            Collection<ServerInfo> servers = getProxy().getServers().values();

            for (ServerInfo targetServer : servers) {
                if (!targetServer.equals(sourceServer)) {
                    targetServer.sendData(CHANNEL, message);
                    getLogger().info("转发消息到服务器: " + targetServer.getName());
                }
            }
        }
    }
}
