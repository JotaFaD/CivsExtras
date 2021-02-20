package io.github.jotafad.civsextras.effects;

import io.github.jotafad.civsextras.CivsExtras;
import io.github.jotafad.civsextras.KeyBinding;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.redcastlemedia.multitallented.civs.events.RegionUpkeepEvent;
import org.redcastlemedia.multitallented.civs.regions.Region;

public class SoundEffect implements Listener
{
    private static final CivsExtras plugin = (CivsExtras) JavaPlugin.getProvidingPlugin(SoundEffect.class);
    public static String KEY = "sound";

    public SoundEffect(){}

    @EventHandler
    public void onRegionUpkeep(RegionUpkeepEvent event)
    {
        Region region = event.getRegion();
        Location regionLocation = event.getRegion().getLocation();
        World currentWorld = event.getRegion().getLocation().getWorld();

        Sound sound;
        float volume;
        float pitch;

        if (!region.getEffects().containsKey(KEY)) return;

        String[] vars = region.getEffects().get(KEY).split("\\s*,\\s*");

        if (vars.length != 3) return;

        try
        {
            sound = Sound.valueOf(vars[0]);
            volume = Float.parseFloat(vars[1]);
            pitch = Float.parseFloat(vars[2]);
        }
        catch (IllegalArgumentException | NullPointerException ex1)
        {
            return;
        }

        currentWorld.playSound(regionLocation.add(0.5, 0.5, 0.5), sound, volume, pitch);
    }
}
