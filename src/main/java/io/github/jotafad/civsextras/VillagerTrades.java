package io.github.jotafad.civsextras;

import com.amihaiemil.eoyaml.YamlSequence;
import io.github.jotafad.civsextras.config.ConfigManager;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class VillagerTrades implements Listener
{
    private static final CivsExtras plugin = (CivsExtras) JavaPlugin.getProvidingPlugin(VillagerTrades.class);

    public VillagerTrades()
    {

    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event)
    {
        if(!ConfigManager.trades.getBoolean("enabled")) return;
        if (!(event.getEntity() instanceof Villager)) return;

        Villager villager = (Villager) event.getEntity();
        String recipesPath = String.join(".","villagers", villager.getProfession().toString().toLowerCase(), getLevelName(villager.getVillagerLevel()));

        YamlSequence recipes = ConfigManager.trades.getSequence(recipesPath);
        if (recipes == null || recipes.size() == 0) return;

        int index = (int) (Math.random() * recipes.size());
        for (int i = 0; i < recipes.size(); i++)
        {
            MerchantRecipe recipe = ConfigManager.trades.getRecipe(recipes.yamlMapping((index + i) % recipes.size()).yamlMapping("recipe"));
            if (recipe != null && !hasRecipe(villager, recipe))
            {
                event.setRecipe(recipe);
                return;
            }
        }

        event.setCancelled(true);
    }

    private boolean hasRecipe(Merchant merchant, MerchantRecipe recipe)
    {
        for (MerchantRecipe r : merchant.getRecipes())
        {
            if (recipe.getResult().equals(r.getResult())) return true;
        }

        return false;
    }

    private String getLevelName(int level)
    {
        String[] levelNames = {"novice", "apprentice", "journeyman", "expert", "master"};
        return levelNames[level - 1];
    }
}
