package com.jotafad.civsextras.effects;

import com.jotafad.civsextras.CivsExtras;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.redcastlemedia.multitallented.civs.events.RegionUpkeepEvent;
import org.redcastlemedia.multitallented.civs.items.CVItem;
import org.redcastlemedia.multitallented.civs.regions.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class BreakEffect implements Listener
{
    public static String KEY = "break";
    private final CivsExtras plugin;

    public BreakEffect(CivsExtras plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRegionUpkeep(RegionUpkeepEvent event)
    {
        Region region = event.getRegion();
        Location regionLocation = event.getRegion().getLocation();
        World currentWorld = event.getRegion().getLocation().getWorld();

        Material breakType;
        int breakPeriod;

        if (!region.getEffects().containsKey(KEY)) return;

        String[] vars = region.getEffects().get(KEY).split("\\s*,\\s*");

        if (vars.length != 2) return;

        try
        {
            breakType = Material.valueOf(vars[0]);
            breakPeriod = Integer.parseInt(vars[1]);
        }
        catch (IllegalArgumentException ex)
        {
            return;
        }

        if (region.getUpkeepHistory().size() == 0 || region.getUpkeepHistory().size() % breakPeriod != 0) return;

        double xMax = regionLocation.getX() + region.getRadiusXP();
        double xMin = regionLocation.getX() - region.getRadiusXN();
        double yMax = regionLocation.getY() + region.getRadiusYP();
        double yMin = regionLocation.getY() - region.getRadiusYN();
        double zMax = regionLocation.getZ() + region.getRadiusZP();
        double zMin = regionLocation.getZ() - region.getRadiusZN();

        yMax = yMax > currentWorld.getMaxHeight() ? currentWorld.getMaxHeight() : yMax;
        yMin = yMin < 0 ? 0 : yMin;

        List<Block> topBlocksFound = new ArrayList<>();
        List<Block> remainingBlocksFound = new ArrayList<>();
        boolean foundAllTopBlocks = false;

        for (double y = yMax; y >= yMin; y--)
        {
            for (double x = xMin; x <= xMax; x++)
            {
                for (double z = zMin; z <= zMax; z++)
                {
                    Location location = new Location(currentWorld, x, y, z);
                    Block currentBlock = location.getBlock();
                    if (currentBlock.getType().equals(breakType))
                    {
                        if (!foundAllTopBlocks)
                        {
                            topBlocksFound.add(currentBlock);
                        }
                        else
                        {
                            remainingBlocksFound.add(currentBlock);
                        }
                    }
                }
            }
            if (!topBlocksFound.isEmpty())
            {
                foundAllTopBlocks = true;
            }
        }
        if (!topBlocksFound.isEmpty())
        {
            Block breakBlock = topBlocksFound.get((int) (Math.random() * topBlocksFound.size()));
            breakBlock.setType(Material.AIR);
            currentWorld.spawnParticle(Particle.BLOCK_DUST, breakBlock.getLocation().add(0.5, 0.5, 0.5), 32, breakType.createBlockData());
            currentWorld.playSound(breakBlock.getLocation().add(0.5, 0.5, 0.5), Sound.BLOCK_STONE_BREAK, 1.0f, 1.0f);
        }
        if (topBlocksFound.size() + remainingBlocksFound.size() <= 1)
        {
            List<List<CVItem>> missingBlocks = new ArrayList<>();
            List<CVItem> tempList = new ArrayList<>();
            tempList.add(new CVItem(breakType, 1));
            missingBlocks.add(tempList);

            region.setMissingBlocks(missingBlocks);
        }
    }
}
