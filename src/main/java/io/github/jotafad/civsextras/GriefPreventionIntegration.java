package io.github.jotafad.civsextras;

import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.CreateClaimResult;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.redcastlemedia.multitallented.civs.events.RegionCreatedEvent;
import org.redcastlemedia.multitallented.civs.items.ItemManager;
import org.redcastlemedia.multitallented.civs.regions.Region;
import org.redcastlemedia.multitallented.civs.regions.RegionType;

import java.util.UUID;
import java.util.logging.Level;

public class GriefPreventionIntegration implements Listener
{
    private static final CivsExtras plugin = (CivsExtras) JavaPlugin.getProvidingPlugin(CivsExtras.class);
    private final GriefPrevention griefPrevention;

    public GriefPreventionIntegration()
    {
        griefPrevention = GriefPrevention.instance;
    }

    @EventHandler
    public void onClaimCreatedEvent(ClaimCreatedEvent event)
    {
        plugin.getLogger().log(Level.INFO, "Claim Created");
    }

    @EventHandler
    public void onRegionCreatedEvent(RegionCreatedEvent event)
    {
        Region region = event.getRegion();
        Location regionLocation = event.getRegion().getLocation();
        RegionType regionType = (RegionType) ItemManager.getInstance().getItemType(region.getType());
        World regionWorld = event.getRegion().getLocation().getWorld();

        int x1 = regionLocation.getBlockX() + regionType.getBuildRadiusX();
        int y1 = regionLocation.getBlockY() + regionType.getBuildRadiusY();
        int z1 = regionLocation.getBlockZ() + regionType.getBuildRadiusZ();

        int x2 = regionLocation.getBlockX() - regionType.getBuildRadiusX();
        int y2 = regionLocation.getBlockY() - regionType.getBuildRadiusY();
        int z2 = regionLocation.getBlockZ() - regionType.getBuildRadiusZ();

        CreateClaimResult result = griefPrevention.dataStore.createClaim(regionWorld, x1, x2, y1, y2, z1, z2, UUID.fromString(""), griefPrevention.dataStore.getClaimAt(regionLocation, false, null), null, null);
        if(result.succeeded)
        {
            result.claim.setPermission(null, ClaimPermission.Build);
        }
    }
//
//    @EventHandler
//    public void onClaimCreatedEvent(ClaimCreatedEvent event)
//    {
//        Set<Region> regions = getRegionsInClaim(event.getClaim());
//        Set<Town> towns = getTownsInClaim(event.getClaim());
//
//        if(!(event.getCreator() instanceof Player)) return;
//
//        Player player = (Player) event.getCreator();
//
//        if(!regions.isEmpty())
//        {
//            event.setCancelled(true);
//            return;
//        }
//
//        for(Town town : towns)
//        {
//            String role = town.getPeople().get(player.getUniqueId());
//            if(role == null || !role.equals("owner") && !role.equals("member"))
//            {
//                event.setCancelled(true);
//                return;
//            }
//        }
//    }
//
//    private Set<Region> getRegionsInClaim(Claim claim)
//    {
//        Set<Region> intersectingRegions = new HashSet<>();
//        Location lesser = claim.getLesserBoundaryCorner().add(0.5, 0.5, 0.5);
//        Location greater = claim.getGreaterBoundaryCorner().add(0.5, 0.5, 0.5);
//
//        for(Region region : RegionManager.getInstance().getAllRegions())
//        {
//            Location regionLocation = region.getLocation();
//            RegionType regionType = (RegionType) ItemManager.getInstance().getItemType(region.getType());
//
//            if(region.getLocation().getWorld() != lesser.getWorld()) continue;
//
//            if(!((lesser.getX() < regionLocation.getX() - regionType.getBuildRadiusX() && greater.getX() < regionLocation.getX() - regionType.getBuildRadiusX())
//                    || (lesser.getX() > regionLocation.getX() + regionType.getBuildRadiusX() && greater.getX() > regionLocation.getX() + regionType.getBuildRadiusX())
//                    || (lesser.getZ() < regionLocation.getZ() - regionType.getBuildRadiusZ() && greater.getZ() < regionLocation.getZ() - regionType.getBuildRadiusZ())
//                    || (lesser.getZ() > regionLocation.getZ() + regionType.getBuildRadiusZ() && greater.getZ() > regionLocation.getZ() + regionType.getBuildRadiusZ())))
//            {
//                intersectingRegions.add(region);
//            }
//        }
//
//        return intersectingRegions;
//    }
//
//    private Set<Town> getTownsInClaim(Claim claim)
//    {
//        Set<Town> intersectingTowns = new HashSet<>();
//        Location lesser = claim.getLesserBoundaryCorner().add(0.5, 0.5, 0.5);
//        Location greater = claim.getGreaterBoundaryCorner().add(0.5, 0.5, 0.5);
//
//        for(Town town : TownManager.getInstance().getTowns())
//        {
//            Location townLocation = town.getLocation();
//            TownType townType = (TownType) ItemManager.getInstance().getItemType(town.getType());
//
//            if(townLocation.getWorld() != lesser.getWorld()) continue;
//
//            if(!((lesser.getX() < townLocation.getX() - townType.getBuildRadius() && greater.getX() < townLocation.getX() - townType.getBuildRadius())
//                    || (lesser.getX() > townLocation.getX() + townType.getBuildRadius() && greater.getX() > townLocation.getX() + townType.getBuildRadius())
//                    || (lesser.getZ() < townLocation.getZ() - townType.getBuildRadius() && greater.getZ() < townLocation.getZ() - townType.getBuildRadius())
//                    || (lesser.getZ() > townLocation.getZ() + townType.getBuildRadius() && greater.getZ() > townLocation.getZ() + townType.getBuildRadius())))
//            {
//                intersectingTowns.add(town);
//            }
//        }
//
//        return intersectingTowns;
//    }
}
