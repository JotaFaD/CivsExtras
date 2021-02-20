package io.github.jotafad.civsextras.townbar;

import io.github.jotafad.civsextras.CivsExtras;
import io.github.jotafad.civsextras.config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.redcastlemedia.multitallented.civs.events.*;
import org.redcastlemedia.multitallented.civs.items.ItemManager;
import org.redcastlemedia.multitallented.civs.regions.Region;
import org.redcastlemedia.multitallented.civs.regions.RegionType;
import org.redcastlemedia.multitallented.civs.regions.RegionUpkeep;
import org.redcastlemedia.multitallented.civs.towns.Town;
import org.redcastlemedia.multitallented.civs.towns.TownManager;

import java.util.HashMap;
import java.util.UUID;

public class TownBarManager implements Listener
{
    private static final CivsExtras plugin = (CivsExtras) JavaPlugin.getProvidingPlugin(CivsExtras.class);
    private final HashMap<String, TownBar> townBars = new HashMap<>();

    public TownBarManager()
    {
        for (Town town : TownManager.getInstance().getTowns())
        {
            createTownBar(town.getName());
        }
    }

    private void createTownBar(String townName)
    {
        if (hasTownBar(townName)) return;

        TownBar townBar = new TownBar(townName);
        townBars.put(townName, townBar);
    }

    private void removeTownBar(String townName)
    {
        if (!hasTownBar(townName)) return;

        townBars.get(townName).removeAllPlayers();
        townBars.remove(townName);
    }

    private boolean hasTownBar(String townName)
    {
        return townBars.containsKey(townName);
    }

    private TownBar getTownBar(String townName)
    {
        return townBars.get(townName);
    }

    @EventHandler
    public void onTownCreated(TownCreatedEvent event)
    {
        String townName = event.getTown().getName();

        createTownBar(townName);
    }

    @EventHandler
    public void onTownDestroyed(TownDestroyedEvent event)
    {
        String townName = event.getTown().getName();

        removeTownBar(townName);
    }

    @EventHandler
    public void onRenameTown(RenameTownEvent event)
    {
        String oldName = event.getOldName();
        String newName = event.getNewName();

        TownBar townBar = townBars.remove(oldName);
        townBars.put(newName, townBar);
        townBar.townName = newName;
    }

    @EventHandler
    public void onPlayerEnterTown(PlayerEnterTownEvent event)
    {
        UUID uuid = event.getUuid();
        String townName = event.getTown().getName();

        if (!hasTownBar(townName))
        {
            createTownBar(townName);
        }
        getTownBar(townName).addPlayer(uuid);
    }

    @EventHandler
    public void onPlayerExitTown(PlayerExitTownEvent event)
    {
        UUID uuid = event.getUuid();
        String townName = event.getTown().getName();

        if (hasTownBar(townName))
        {
            getTownBar(townName).removePlayer(uuid);
        }
    }

    @EventHandler
    public void onRegionUpkeep(RegionUpkeepEvent event)
    {
        Region region = event.getRegion();
        Town town = TownManager.getInstance().getTownAt(region.getLocation());

        if (town == null) return;

        if (!hasTownBar(town.getName())) return;

        TownBar townBar = getTownBar(town.getName());
        RegionType regionType = (RegionType) ItemManager.getInstance().getItemType(region.getType());
        RegionUpkeep regionUpkeep = regionType.getUpkeeps().get(event.getUpkeepIndex());

        String title;
        BarColor barColor;

        if (regionUpkeep.getPayout() > 0)
        {
            title = ChatColor.GREEN + "+" + ConfigManager.config.getString("townbar-payout-prefix") + regionUpkeep.getPayout() + plugin.getConfig().getString("townbar.payout-suffix") + " " + regionType.getDisplayName();
            barColor = BarColor.GREEN;
        }
        else if (regionUpkeep.getPowerOutput() > 0)
        {
            title = ChatColor.GREEN + "+" + regionUpkeep.getPowerOutput() + " Power " + regionType.getDisplayName();
            barColor = BarColor.GREEN;
        }
        else if (regionUpkeep.getPowerInput() > 0)
        {
            title = ChatColor.RED + "-" + regionUpkeep.getPowerInput() + " Power " + regionType.getDisplayName();
            barColor = BarColor.RED;
        }
        else
        {
            title = ChatColor.AQUA + regionType.getDisplayName();
            barColor = BarColor.BLUE;
        }

        townBar.addNotification(title, barColor, ConfigManager.config.getInt("townbar-notification-duration"));
        townBar.update();
    }

    @EventHandler
    public void onHousingAdded(RegionCreatedEvent event)
    {
        Region region = event.getRegion();
        Town town = TownManager.getInstance().getTownAt(region.getLocation());

        if (town == null) return;

        if (!hasTownBar(town.getName())) return;

        if (!region.getEffects().containsKey("housing")) return;

        TownBar townBar = getTownBar(town.getName());
        RegionType regionType = (RegionType) ItemManager.getInstance().getItemType(region.getType());

        String title = ChatColor.GREEN + "+" + region.getEffects().get("housing") + " Housing " + regionType.getDisplayName();
        townBar.addNotification(title, BarColor.GREEN, 5);
    }

    @EventHandler
    public void onHousingRemoved(RegionDestroyedEvent event)
    {
        Region region = event.getRegion();
        Town town = TownManager.getInstance().getTownAt(region.getLocation());

        if (town == null) return;

        if (!hasTownBar(town.getName())) return;

        if (!region.getEffects().containsKey("housing")) return;

        TownBar townBar = getTownBar(town.getName());
        RegionType regionType = (RegionType) ItemManager.getInstance().getItemType(region.getType());

        String title = ChatColor.RED + "-" + region.getEffects().get("housing") + " Housing " + regionType.getDisplayName();
        townBar.addNotification(title, BarColor.RED, 5);
    }
}
