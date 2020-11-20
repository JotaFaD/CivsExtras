package com.jotafad.civsextras.commands;

import com.jotafad.civsextras.CivsExtras;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CECommand implements CommandExecutor
{
    private final CivsExtras plugin;

    public CECommand(CivsExtras plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if (strings.length > 0 && strings[0].equals("reload"))
        {
            commandSender.sendMessage("Configuration reloaded");
            plugin.reloadConfig();
            if (plugin.getConfig().getBoolean("reload-civs", false))
            {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cv reload");
            }
            plugin.blueMapIntegration.updateCivsMarkers();
            return true;
        }
        return false;
    }
}
