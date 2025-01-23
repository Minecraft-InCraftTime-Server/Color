package ict.minesunshineone.chat;

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

    @Override
    public void onEnable() {

        saveDefaultConfig();

        muteManager = new MuteManager(this);

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

        getLogger().info("SimpleChat插件已启用");
    }

    @Override
    public void onDisable() {
        if (muteManager != null) {
            muteManager.saveMuteData();
        }
        getLogger().info("SimpleChat插件已禁用");
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }
}
