package com.nikolaipatrick.disablepluginscommand;

import org.bukkit.plugin.java.JavaPlugin;

public final class DisablePluginsCommand extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Loading DisablePluginsCommand v" + getDescription().getVersion());
        getLogger().info("DisablePluginsCommand by Nikolai Patrick 2022-2026");
        getServer().getPluginManager().registerEvents(new commandListener(), this);
        getCommand("disablepluginscmd").setExecutor(new commandDisablepluginscmd());
        getCommand("disablepluginscmd").setTabCompleter(new cmdTabCompleter());
        this.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye!");
    }
}
