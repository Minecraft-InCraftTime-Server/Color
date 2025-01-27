package ict.minesunshineone.chat;

import org.bukkit.plugin.java.JavaPlugin;

import ict.minesunshineone.chat.commands.ChannelCommand;
import ict.minesunshineone.chat.commands.MuteCommand;
import ict.minesunshineone.chat.commands.ReloadCommand;
import ict.minesunshineone.chat.listeners.AnvilListener;
import ict.minesunshineone.chat.listeners.PingListener;
import ict.minesunshineone.chat.listeners.PlayerChatListener;
import ict.minesunshineone.chat.listeners.SignListener;
import ict.minesunshineone.chat.managers.ChannelManager;
import ict.minesunshineone.chat.managers.CrossServerManager;
import ict.minesunshineone.chat.managers.MuteManager;

public class SimpleChat extends JavaPlugin {

    private MuteManager muteManager;
    private ChannelManager channelManager;
    private CrossServerManager crossServerManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("channels/normal.yml", false);
        saveResource("channels/staff.yml", false);
        saveResource("channels/global.yml", false);

        muteManager = new MuteManager(this);
        channelManager = new ChannelManager(this);
        crossServerManager = new CrossServerManager(this);

        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this, muteManager), this);
        getServer().getPluginManager().registerEvents(new PingListener(this), this);

        // 注册命令
        MuteCommand muteCommand = new MuteCommand(muteManager);
        getCommand("mute").setExecutor(muteCommand);
        getCommand("unmute").setExecutor(muteCommand);
        getCommand("mute").setTabCompleter(muteCommand);
        getCommand("unmute").setTabCompleter(muteCommand);
        getCommand("chatreload").setExecutor(new ReloadCommand(this));

        // 注册频道命令
        ChannelCommand channelCommand = new ChannelCommand(this);
        getCommand("channel").setExecutor(channelCommand);
        getCommand("channel").setTabCompleter(channelCommand);

        getLogger().info("SimpleChat插件已启用");
    }

    @Override
    public void onDisable() {
        if (muteManager != null) {
            muteManager.saveMuteData();
        }
        if (crossServerManager != null) {
            crossServerManager.disable();
        }
        getLogger().info("SimpleChat插件已禁用");
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public CrossServerManager getCrossServerManager() {
        return crossServerManager;
    }
}
