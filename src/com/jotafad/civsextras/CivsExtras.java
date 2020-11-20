package com.jotafad.civsextras;

import com.jotafad.civsextras.commands.CECommand;
import com.jotafad.civsextras.effects.BreakEffect;
import com.jotafad.civsextras.effects.SoundEffect;
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
        getServer().getPluginManager().registerEvents(new TownBar(this), this);
        getServer().getPluginManager().registerEvents(new KeyBinding(this), this);
        getServer().getPluginManager().registerEvents(blueMapIntegration, this);
    }

    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll(this);
    }
}