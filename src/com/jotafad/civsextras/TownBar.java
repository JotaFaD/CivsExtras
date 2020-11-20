package com.jotafad.civsextras;

import com.jotafad.civsextras.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.redcastlemedia.multitallented.civs.events.*;
import org.redcastlemedia.multitallented.civs.items.ItemManager;
import org.redcastlemedia.multitallented.civs.regions.Region;
import org.redcastlemedia.multitallented.civs.regions.RegionType;
import org.redcastlemedia.multitallented.civs.regions.RegionUpkeep;
import org.redcastlemedia.multitallented.civs.towns.Town;
import org.redcastlemedia.multitallented.civs.towns.TownManager;

import java.util.HashMap;
import java.util.UUID;

public class TownBar implements Listener
{

    private class TownBarAnimation extends BukkitRunnable
    {

        private final BossBar bossBar;
        private final String title;
        private final BarColor color;

        public TownBarAnimation(BossBar bossBar, String title, BarColor color)
        {
            this.bossBar = bossBar;
            this.title = title;
            this.color = color;
        }

        @Override
        public void run()
        {
            bossBar.setColor(color);
            bossBar.setTitle(title);
        }
    }


    private final CivsExtras plugin;
    private final HashMap<String, BossBar> townBars = new HashMap<>();

    public TownBar(CivsExtras plugin)
    {
        this.plugin = plugin;

        for (Town town : TownManager.getInstance().getTowns())
        {
            createTownBar(town.getName());
        }
    }

    private void createTownBar(String townName)
    {
        if (hasTownBar(townName)) return;

        Town town = TownManager.getInstance().getTown(townName);
        BossBar townBar = Bukkit.createBossBar(townName, BarColor.WHITE, BarStyle.SEGMENTED_10);

        townBar.setTitle(ChatColor.WHITE + town.getName());
        townBars.put(townName, townBar);
        updateTownBarProgress(townName);
    }

    private void removeTownBar(String townName)
    {
        if (!hasTownBar(townName)) return;

        townBars.get(townName).removeAll();
        townBars.remove(townName);
    }

    private void addPlayerToTownBar(UUID uuid, String townName)
    {
        if (townBars.containsKey(townName))
        {
            getTownBar(townName).addPlayer(Bukkit.getPlayer(uuid));
        }
    }

    private void removePlayerFromTownBar(UUID uuid, String townName)
    {
        if (townBars.containsKey(townName))
        {
            getTownBar(townName).removePlayer(Bukkit.getPlayer(uuid));
        }
    }

    private boolean hasTownBar(String townName)
    {
        return townBars.containsKey(townName);
    }

    private BossBar getTownBar(String townName)
    {
        return townBars.get(townName);
    }

    private void updateTownBarProgress(String townName)
    {
        Town town = TownManager.getInstance().getTown(townName);

        getTownBar(townName).setProgress(Utils.clamp((double) town.getPower() / town.getMaxPower(), 0, 1));
    }

    private void flashTownBar(String townName, String title, BarColor color, long duration)
    {
        new TownBarAnimation(townBars.get(townName), ChatColor.WHITE + townName + " - " + title, color).runTask(plugin);
        new TownBarAnimation(townBars.get(townName), townName, BarColor.WHITE).runTaskLater(plugin, duration * 20);
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

        BossBar townBar = townBars.remove(oldName);
        townBars.put(newName, townBar);
        townBar.setTitle(newName);
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
        addPlayerToTownBar(uuid, townName);
    }

    @EventHandler
    public void onPlayerExitTown(PlayerExitTownEvent event)
    {
        UUID uuid = event.getUuid();
        String townName = event.getTown().getName();

        if (hasTownBar(townName))
        {
            removePlayerFromTownBar(uuid, townName);
        }
    }

    @EventHandler
    public void onRegionUpkeep(RegionUpkeepEvent event)
    {
        Region region = event.getRegion();
        Town town = TownManager.getInstance().getTownAt(region.getLocation());

        if (town == null) return;

        if (!hasTownBar(town.getName())) return;

        RegionType regionType = (RegionType) ItemManager.getInstance().getItemType(region.getType());

        String title;

        for (RegionUpkeep regionUpkeep : regionType.getUpkeeps())
        {
            if (regionUpkeep.getPayout() > 0)
            {
                title = ChatColor.GREEN + "+" + plugin.getConfig().getString("townbar.payout-prefix", "") + regionUpkeep.getPayout() + plugin.getConfig().getString("townbar.payout-suffix", "") + " " + regionType.getDisplayName();

                flashTownBar(town.getName(), title, BarColor.GREEN, 3);
            }
            else if (regionUpkeep.getPowerOutput() > 0)
            {
                title = ChatColor.GREEN + "+" + regionUpkeep.getPowerOutput() + " Power " + regionType.getDisplayName();

                flashTownBar(town.getName(), title, BarColor.GREEN, 3);
            }
            else if (regionUpkeep.getPowerInput() > 0)
            {
                title = ChatColor.RED + "-" + regionUpkeep.getPowerInput() + " Power " + regionType.getDisplayName();

                flashTownBar(town.getName(), title, BarColor.RED, 3);
            }
            else
            {
                title = ChatColor.AQUA + regionType.getDisplayName();

                flashTownBar(town.getName(), title, BarColor.BLUE, 3);
            }
        }
    }

    @EventHandler
    public void onHousingAdded(RegionCreatedEvent event)
    {
        Region region = event.getRegion();
        Town town = TownManager.getInstance().getTownAt(region.getLocation());

        if (town == null) return;

        if (!hasTownBar(town.getName())) return;

        if (!region.getEffects().containsKey("Housing")) return;

        RegionType regionType = (RegionType) ItemManager.getInstance().getItemType(region.getType());

        String title = ChatColor.GREEN + "+" + region.getEffects().get("housing") + " Population " + regionType.getDisplayName();
        flashTownBar(town.getName(), title, BarColor.GREEN, 2);
        updateTownBarProgress(town.getName());
    }

    @EventHandler
    public void onHousingRemoved(RegionDestroyedEvent event)
    {
        Region region = event.getRegion();
        Town town = TownManager.getInstance().getTownAt(region.getLocation());

        if (town == null) return;

        if (!hasTownBar(town.getName())) return;

        if (!region.getEffects().containsKey("Housing")) return;

        RegionType regionType = (RegionType) ItemManager.getInstance().getItemType(region.getType());

        String title = ChatColor.RED + "-" + region.getEffects().get("housing") + " Population " + regionType.getDisplayName();
        flashTownBar(town.getName(), title, BarColor.RED, 2);
        updateTownBarProgress(town.getName());
    }
}
