package io.github.jotafad.civsextras.commands;

import io.github.jotafad.civsextras.CivsExtras;
import io.github.jotafad.civsextras.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class CECommand implements CommandExecutor
{
    private static final CivsExtras plugin = (CivsExtras) JavaPlugin.getProvidingPlugin(CECommand.class);

    public CECommand() {}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if (strings.length > 0 && strings[0].equals("reload"))
        {
            commandSender.sendMessage("Configuration reloaded");
            ConfigManager.loadFiles();
            if (ConfigManager.config.getBoolean("reload-civs"))
            {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cv reload");
            }
            if(plugin.blueMapIntegration != null)
            {
                plugin.blueMapIntegration.updateCivsMarkers();
            }

            return true;
        }
        return false;
    }
}
