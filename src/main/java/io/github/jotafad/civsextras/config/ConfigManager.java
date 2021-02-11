package io.github.jotafad.civsextras.config;

import io.github.jotafad.civsextras.CivsExtras;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigManager
{
    private static final CivsExtras plugin = (CivsExtras) JavaPlugin.getProvidingPlugin(ConfigManager.class);

    public static ConfigFile config = new ConfigFile(new File(plugin.getDataFolder(), "config.yml"));
    public static ConfigFile trades = new ConfigFile(new File(plugin.getDataFolder(), "trades.yml"));

    public static void loadFiles()
    {
        config.load();
        trades.load();
    }
    
    public static void saveFiles()
    {
        config.save();
        trades.save();
    }
}
