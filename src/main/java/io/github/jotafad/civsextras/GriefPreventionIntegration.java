package io.github.jotafad.civsextras;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.redcastlemedia.multitallented.civs.events.RegionCreatedEvent;

import java.util.Optional;
import java.util.logging.Level;

public class GriefPreventionIntegration implements Listener
{
    private final CivsExtras plugin;

    public GriefPreventionIntegration(CivsExtras plugin)
    {
        this.plugin = plugin;
        plugin.getLogger().log(Level.INFO, "GriefPreventionIntegration Loaded");
    }

    public Optional<GriefPrevention> getGriefPreventionAPI()
    {
        return Optional.ofNullable(GriefPrevention.instance);
    }

    @EventHandler
    public void onRegionCreatedEvent(RegionCreatedEvent event)
    {
        getGriefPreventionAPI().ifPresent(griefPreventionAPI -> {
            plugin.getLogger().log(Level.INFO, "GriefPrevention Found");
        });
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
