package com.jotafad.civsextras;

import com.sun.javafx.util.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;

public class VillagerTrades implements Listener
{
    private final CivsExtras plugin;

    public VillagerTrades(CivsExtras plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event)
    {
        if (!(event.getEntity() instanceof Villager)) return;

        Villager villager = (Villager) event.getEntity();
        String villagerPath = joinPaths("trades", villager.getProfession().toString().toLowerCase(), getLevelName(villager.getVillagerLevel()));

        if (!plugin.getConfig().contains(villagerPath)) return;
        if (plugin.getConfig().getConfigurationSection(villagerPath).getKeys(false).isEmpty()) return;

        List<String> recipeList = new ArrayList<>(plugin.getConfig().getConfigurationSection(villagerPath).getKeys(false));

        int index = (int) (Math.random() * recipeList.size());
        String recipePath;
        for (int i = 0; i < recipeList.size(); i++)
        {
            recipePath = joinPaths(villagerPath, recipeList.get((index + i) % recipeList.size()));
            MerchantRecipe recipe = getRecipeFromConfig(recipePath);
            if (recipe != null && !hasRecipe(villager, recipe))
            {
                event.setRecipe(recipe);
                return;
            }
        }

        event.setCancelled(true);
    }

    private ItemStack getItemStackFromConfig(String path)
    {
        String itemName = plugin.getConfig().getString(joinPaths(path, "item"), null);
        int itemAmount = plugin.getConfig().getInt(joinPaths(path, "amount"), 1);
        String enchantmentName = plugin.getConfig().getString(joinPaths(path, "enchantment"), null);
        int enchantmentLevel = plugin.getConfig().getInt(joinPaths(path, "enchantment-level"), 1);

        itemAmount = Math.max(itemAmount, 1);

        if (itemName == null || Material.getMaterial(itemName) == null)
        {
            return null;
        }

        ItemStack itemStack = new ItemStack(Material.valueOf(itemName), itemAmount);

        if (enchantmentName != null)
        {
            EnchantmentWrapper enchantmentWrapper = new EnchantmentWrapper(enchantmentName.toLowerCase());
            if (enchantmentWrapper.getEnchantment() != null)
            {
                enchantmentLevel = Utils.clamp(enchantmentLevel, 1, enchantmentWrapper.getMaxLevel());
                if (itemStack.getType().equals(Material.ENCHANTED_BOOK))
                {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
                    if (meta != null)
                    {
                        meta.addStoredEnchant(enchantmentWrapper.getEnchantment(), enchantmentLevel, false);
                        itemStack.setItemMeta(meta);
                    }
                }
                else if (enchantmentWrapper.canEnchantItem(itemStack))
                {
                    itemStack.addEnchantment(enchantmentWrapper.getEnchantment(), enchantmentLevel);
                }
            }
        }

        return itemStack;
    }

    private MerchantRecipe getRecipeFromConfig(String path)
    {
        ItemStack input1 = getItemStackFromConfig(joinPaths(path, "input1"));
        ItemStack input2 = getItemStackFromConfig(joinPaths(path, "input2"));
        ItemStack output = getItemStackFromConfig(joinPaths(path, "output"));

        if (output == null || (input1 == null && input2 == null)) return null;

        float priceMultiplier = (float) plugin.getConfig().getDouble(joinPaths(path, "price-multiplier"), 0.05);
        int maxUses = plugin.getConfig().getInt(joinPaths(path, "max-uses"), 12);
        int villagerExperience = plugin.getConfig().getInt(joinPaths(path, "villager-xp"), 2);

        MerchantRecipe recipe = new MerchantRecipe(output, 0, maxUses, true, villagerExperience, priceMultiplier);

        if (input1 != null) recipe.addIngredient(input1);
        if (input2 != null) recipe.addIngredient(input2);

        return recipe;
    }

    private boolean hasRecipe(Merchant merchant, MerchantRecipe recipe)
    {
        for (MerchantRecipe r : merchant.getRecipes())
        {
            if (recipe.getResult().equals(r.getResult())) return true;
        }

        return false;
    }

    private String joinPaths(String... paths)
    {
        return String.join(".", paths);
    }

    private String getLevelName(int level)
    {
        String[] levelNames = {"novice", "apprentice", "journeyman", "expert", "master"};
        return levelNames[level - 1];
    }
}
