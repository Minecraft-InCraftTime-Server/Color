package com.github.color;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.color.listeners.AnvilListener;
import com.github.color.listeners.PlayerChatListener;
import com.github.color.listeners.SignListener;

public class ColorCode extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getLogger().info("ColorCode plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ColorCode plugin has been disabled!");
    }
}
