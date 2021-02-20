package io.github.jotafad.civsextras;

import io.github.jotafad.civsextras.commands.CECommand;
import io.github.jotafad.civsextras.config.ConfigManager;
import io.github.jotafad.civsextras.effects.BreakEffect;
import io.github.jotafad.civsextras.effects.SoundEffect;
import io.github.jotafad.civsextras.townbar.TownBarManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class CivsExtras extends JavaPlugin
{
    public final int pluginId = 10098;

    public BlueMapIntegration blueMapIntegration;

    @Override
    public void onEnable()
    {
        ConfigManager.loadFiles();

        this.getCommand("ce").setExecutor(new CECommand());

        getServer().getPluginManager().registerEvents(new BreakEffect(), this);
        getServer().getPluginManager().registerEvents(new SoundEffect(), this);
        getServer().getPluginManager().registerEvents(new VillagerTrades(), this);
        getServer().getPluginManager().registerEvents(new TownBarManager(), this);
        getServer().getPluginManager().registerEvents(new KeyBinding(), this);

        if(Bukkit.getPluginManager().getPlugin("BlueMap") != null)
        {
            blueMapIntegration = new BlueMapIntegration();
            getServer().getPluginManager().registerEvents(blueMapIntegration, this);
        }

        //if(Bukkit.getPluginManager().getPlugin("GriefPrevention") != null)
        //{
        //    getServer().getPluginManager().registerEvents(new GriefPreventionIntegration(this), this);
        //    getLogger().log(Level.INFO, "GP Found");
        //}

        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll(this);
    }
}