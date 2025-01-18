package ict.minesunshineone.chat;

import org.bukkit.plugin.java.JavaPlugin;

import ict.minesunshineone.chat.commands.MuteCommand;
import ict.minesunshineone.chat.commands.MuteCommandTabCompleter;
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
        getCommand("mute").setExecutor(new MuteCommand(muteManager));
        getCommand("unmute").setExecutor(new MuteCommand(muteManager));
        getCommand("mute").setTabCompleter(new MuteCommandTabCompleter());
        getCommand("unmute").setTabCompleter(new MuteCommandTabCompleter());

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
