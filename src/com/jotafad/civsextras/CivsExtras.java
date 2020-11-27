package com.jotafad.civsextras;

import com.jotafad.civsextras.commands.CECommand;
import com.jotafad.civsextras.effects.BreakEffect;
import com.jotafad.civsextras.effects.SoundEffect;
import com.jotafad.civsextras.townbar.TownBarManager;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class CivsExtras extends JavaPlugin
{
    public BlueMapIntegration blueMapIntegration = new BlueMapIntegration(this);

    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        getConfig();

        this.getCommand("ce").setExecutor(new CECommand(this));

        getServer().getPluginManager().registerEvents(new BreakEffect(this), this);
        getServer().getPluginManager().registerEvents(new SoundEffect(this), this);
        getServer().getPluginManager().registerEvents(new VillagerTrades(this), this);
        getServer().getPluginManager().registerEvents(new TownBarManager(), this);
        getServer().getPluginManager().registerEvents(new KeyBinding(this), this);
        getServer().getPluginManager().registerEvents(blueMapIntegration, this);
        //getServer().getPluginManager().registerEvents(new GriefPreventionIntegration(this), this);
    }

    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll(this);
    }
}