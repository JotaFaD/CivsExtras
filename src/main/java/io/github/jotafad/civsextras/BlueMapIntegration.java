package io.github.jotafad.civsextras;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.marker.MarkerAPI;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import de.bluecolored.bluemap.api.marker.POIMarker;
import io.github.jotafad.civsextras.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.redcastlemedia.multitallented.civs.events.*;
import org.redcastlemedia.multitallented.civs.items.ItemManager;
import org.redcastlemedia.multitallented.civs.regions.Region;
import org.redcastlemedia.multitallented.civs.regions.RegionManager;
import org.redcastlemedia.multitallented.civs.regions.RegionType;
import org.redcastlemedia.multitallented.civs.towns.Town;
import org.redcastlemedia.multitallented.civs.towns.TownManager;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public class BlueMapIntegration implements Listener
{
    private static final CivsExtras plugin = (CivsExtras) JavaPlugin.getProvidingPlugin(CivsExtras.class);

    public class UpdateCivsMarkers extends BukkitRunnable
    {
        public UpdateCivsMarkers() {}

        @Override
        public void run()
        {
            HashSet<Region> regions = (HashSet<Region>) RegionManager.getInstance().getAllRegions();

            removeCivsMarkers();
            createRegionMarkers(regions);
        }
    }

    public BlueMapIntegration()
    {
        BlueMapAPI.onEnable(blueMapAPI -> {
            new UpdateCivsMarkers().runTask(plugin);
        });
    }

    private void createRegionMarkers(Iterable<Region> regions)
    {
        BlueMapAPI.getInstance().ifPresent(blueMapAPI -> {
            MarkerAPI markerAPI;

            try
            {
                markerAPI = blueMapAPI.getMarkerAPI();
            }
            catch (IOException e)
            {
                return;
            }

            for(Region region : regions)
            {
                RegionType regionType = (RegionType) ItemManager.getInstance().getItemType(region.getType());

                if(regionType.getDynmapMarkerKey().equals("")
                || (!ConfigManager.config.getBoolean("bluemap.show-underground-regions") && region.getLocation().getBlock().getLightFromSky() == 0))
                {
                    continue;
                }

                Location regionLocation = region.getLocation();
                Town town = TownManager.getInstance().getTownAt(regionLocation);

                String markerSetId = town != null ? "Civs-" + town.getName() : "Civs-" + regionType.getDisplayName();
                String markerSetName = town != null ? town.getName() : regionType.getDisplayName();
                MarkerSet markerSet;

                if (!markerAPI.getMarkerSet(markerSetId).isPresent())
                {
                    markerSet = markerAPI.createMarkerSet(markerSetId);
                    markerSet.setLabel(markerSetName);
                }
                else
                {
                    markerSet = markerAPI.getMarkerSet(markerSetId).get();
                }

                Optional<BlueMapWorld> blueMapWorld = blueMapAPI.getWorld(region.getLocation().getWorld().getUID());

                if (blueMapWorld.isPresent())
                {
                    for (BlueMapMap map : blueMapWorld.get().getMaps())
                    {
                        POIMarker marker = markerSet.createPOIMarker(region.getId(), map, regionLocation.getX(), regionLocation.getY(), regionLocation.getZ());

                        marker.setMinDistance(20);
                        marker.setMaxDistance(200);
                        marker.setLabel(regionType.getDisplayName());

                        String iconName = regionType.getDynmapMarkerKey();
                        String iconPath;

                        try
                        {
                            InputStream stream = getClass().getClassLoader().getResourceAsStream("icons/" + iconName + ".png");

                            if (stream != null)
                            {
                                iconPath = blueMapAPI.createImage(ImageIO.read(stream), "icons/" + iconName);
                            }
                            else
                            {
                                stream = getClass().getClassLoader().getResourceAsStream("icons/default.png");
                                if (stream != null)
                                {
                                    iconPath = blueMapAPI.createImage(ImageIO.read(stream), "icons/default");
                                }
                                else
                                {
                                    continue;
                                }

                            }
                        }
                        catch (IOException e)
                        {
                            continue;
                        }

                        marker.setIcon(iconPath, 36, 36);
                    }
                }
            }

            try
            {
                markerAPI.save();
            }
            catch (IOException e)
            {
                return;
            }
        });
    }

    private void removeRegionMarkers(Iterable<Region> regions)
    {
        BlueMapAPI.getInstance().ifPresent(blueMapAPI -> {
            MarkerAPI markerAPI;

            try
            {
                markerAPI = blueMapAPI.getMarkerAPI();
            }
            catch (IOException e)
            {
                return;
            }

            for(Region region : regions)
            {
                RegionType regionType = (RegionType) ItemManager.getInstance().getItemType(region.getType());
                Town town = TownManager.getInstance().getTownAt(region.getLocation());

                String markerSetId = town != null ? "Civs-" + town.getName() : "Civs-" + regionType.getDisplayName();

                markerAPI.getMarkerSet(markerSetId).ifPresent(markerSet -> {
                    markerSet.removeMarker(region.getId());
                });
            }

            try
            {
                markerAPI.save();
            }
            catch (IOException e)
            {
                return;
            }
        });
    }

    private void removeCivsMarkers()
    {
        BlueMapAPI.getInstance().ifPresent(blueMapAPI -> {
            MarkerAPI markerAPI;

            try
            {
                markerAPI = blueMapAPI.getMarkerAPI();
            }
            catch (IOException e)
            {
                return;
            }

            HashSet<MarkerSet> civsMarkerSets = (HashSet<MarkerSet>) markerAPI.getMarkerSets().stream().filter(markerSet -> markerSet.getId().startsWith("Civs-")).collect(Collectors.toSet());

            for (MarkerSet civsMarkerSet : civsMarkerSets)
            {
                markerAPI.removeMarkerSet(civsMarkerSet);
            }

            try
            {
                markerAPI.save();
            }
            catch (IOException e)
            {
                return;
            }
        });
    }

    public void updateCivsMarkers()
    {
        new UpdateCivsMarkers().runTask(plugin);
    }

    @EventHandler
    public void onRegionCreated(RegionCreatedEvent event)
    {
        HashSet<Region> regions = new HashSet<>();
        regions.add(event.getRegion());

        createRegionMarkers(regions);
    }

    @EventHandler
    public void onRegionDestroyed(RegionDestroyedEvent event)
    {
        HashSet<Region> regions = new HashSet<>();
        regions.add(event.getRegion());

        removeRegionMarkers(regions);
    }

    @EventHandler
    public void onTownCreated(TownCreatedEvent event)
    {
        new UpdateCivsMarkers().runTask(plugin);
    }

    @EventHandler
    public void onTownDestroyed(TownDestroyedEvent event)
    {
        new UpdateCivsMarkers().runTask(plugin);
    }

    @EventHandler
    public void onTownRenamed(RenameTownEvent event)
    {
        new UpdateCivsMarkers().runTask(plugin);
    }
}
