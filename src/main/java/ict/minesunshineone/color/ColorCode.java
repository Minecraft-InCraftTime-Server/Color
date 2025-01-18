package ict.minesunshineone.color;

import org.bukkit.plugin.java.JavaPlugin;

import ict.minesunshineone.color.listeners.AnvilListener;
import ict.minesunshineone.color.listeners.PingListener;
import ict.minesunshineone.color.listeners.PlayerChatListener;
import ict.minesunshineone.color.listeners.SignListener;
import ict.minesunshineone.color.managers.MuteManager;
import ict.minesunshineone.color.commands.MuteCommand;

public class ColorCode extends JavaPlugin {

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

        getLogger().info("ColorCode插件已启用");
    }

    @Override
    public void onDisable() {
        getLogger().info("ColorCode插件已禁用");
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }
}
