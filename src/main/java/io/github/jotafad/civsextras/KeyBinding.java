package io.github.jotafad.civsextras;

import io.github.jotafad.civsextras.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class KeyBinding implements Listener
{
    private static final CivsExtras plugin = (CivsExtras) JavaPlugin.getProvidingPlugin(KeyBinding.class);

    public KeyBinding(){}

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event)
    {
        if (!ConfigManager.config.getString("f-key-command").equals("") && event.getOffHandItem().getType().equals(Material.AIR) && !event.getPlayer().isSneaking())
        {
            event.getPlayer().performCommand(ConfigManager.config.getString("f-key-command"));
            event.setCancelled(true);
        }
    }
}
