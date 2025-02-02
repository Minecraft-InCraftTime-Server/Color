package ict.minesunshineone.chat;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import ict.minesunshineone.chat.commands.MuteCommand;
import ict.minesunshineone.chat.commands.ReloadCommand;
import ict.minesunshineone.chat.listeners.AnvilListener;
import ict.minesunshineone.chat.listeners.PingListener;
import ict.minesunshineone.chat.listeners.PlayerChatListener;
import ict.minesunshineone.chat.listeners.SignListener;
import ict.minesunshineone.chat.managers.MuteManager;

public class SimpleChat extends JavaPlugin {

    private MuteManager muteManager;
    private PlayerChatListener chatListener;
    private PingListener pingListener;

    @Override
    public void onEnable() {
        // 保存默认配置
        saveDefaultConfig();

        // 初始化管理器和监听器
        initializeManagers();
        registerListeners();
        registerCommands();

        getLogger().info("SimpleChat 插件已启用!");
    }

    private void initializeManagers() {
        muteManager = new MuteManager(this);
    }

    private void registerListeners() {
        chatListener = new PlayerChatListener(this, muteManager);
        pingListener = new PingListener(this);

        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(), this);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(pingListener, this);
    }

    private void registerCommands() {
        // 注册命令
        MuteCommand muteCommand = new MuteCommand(muteManager);
        getCommand("mute").setExecutor(muteCommand);
        getCommand("unmute").setExecutor(muteCommand);
        getCommand("mute").setTabCompleter(muteCommand);
        getCommand("unmute").setTabCompleter(muteCommand);
        getCommand("chatreload").setExecutor(new ReloadCommand(this));
    }

    public void reload() {
        // 重载配置文件
        reloadConfig();

        // 重新初始化监听器
        HandlerList.unregisterAll(chatListener);
        HandlerList.unregisterAll(pingListener);

        chatListener = new PlayerChatListener(this, muteManager);
        pingListener = new PingListener(this);

        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(pingListener, this);

        getLogger().info("SimpleChat 配置已重载!");
    }

    @Override
    public void onDisable() {
        if (muteManager != null) {
            muteManager.saveMuteData();
        }
        getLogger().info("SimpleChat 插件已禁用!");
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }
}
