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
    public BlueMapIntegration blueMapIntegration;

    @Override
    public void onEnable()
    {
        ConfigManager.loadFiles();

        this.getCommand("ce").setExecutor(new CECommand());

        getServer().getPluginManager().registerEvents(new BreakEffect(this), this);
        getServer().getPluginManager().registerEvents(new SoundEffect(this), this);
        getServer().getPluginManager().registerEvents(new VillagerTrades(), this);
        getServer().getPluginManager().registerEvents(new TownBarManager(), this);
        getServer().getPluginManager().registerEvents(new KeyBinding(this), this);

        if(Bukkit.getPluginManager().getPlugin("BlueMap") != null)
        {
            blueMapIntegration = new BlueMapIntegration(this);
            getServer().getPluginManager().registerEvents(blueMapIntegration, this);
        }

        //if(Bukkit.getPluginManager().getPlugin("GriefPrevention") != null)
        //{
        //    getServer().getPluginManager().registerEvents(new GriefPreventionIntegration(this), this);
        //    getLogger().log(Level.INFO, "GP Found");
        //}

        int pluginId = 10098;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll(this);
    }
}