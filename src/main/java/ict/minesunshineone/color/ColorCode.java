package ict.minesunshineone.color;

import org.bukkit.plugin.java.JavaPlugin;

import ict.minesunshineone.color.listeners.AnvilListener;
import ict.minesunshineone.color.listeners.PingListener;
import ict.minesunshineone.color.listeners.PlayerChatListener;
import ict.minesunshineone.color.listeners.SignListener;

public class ColorCode extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PingListener(this), this);
        getLogger().info("ColorCode插件已启用");
    }

    @Override
    public void onDisable() {
        getLogger().info("ColorCode插件已禁用");
    }
}
