package io.github.jotafad.civsextras.config;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.extensions.MergedYamlMapping;
import io.github.jotafad.civsextras.CivsExtras;
import io.github.jotafad.civsextras.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ConfigFile
{
    private static final CivsExtras plugin = (CivsExtras) JavaPlugin.getProvidingPlugin(ConfigManager.class);

    public File file;
    public YamlMapping data;

    public ConfigFile(File file)
    {
        this.file = file;
    }

    public void load()
    {
        try
        {
            if (file.isFile())
            {
                YamlMapping localData = Yaml.createYamlInput(file).readYamlMapping();
                YamlMapping defaultData = Yaml.createYamlInput(plugin.getResource(file.getName())).readYamlMapping();

                if(defaultData.keys().equals(localData.keys()))
                {
                    data = localData;
                }
                else
                {
                    data = new MergedYamlMapping(defaultData, localData, true).asMapping();
                    save();
                }
            }
            else
            {
                data = Yaml.createYamlInput(plugin.getResource(file.getName())).readYamlMapping();
                save();
            }
        }
        catch (IOException e)
        {
            return;
        }
    }

    public void save()
    {
        try
        {
            if (!file.isFile())
            {
                file.getParentFile().mkdirs();
            }

            //YamlPrinter yamlPrinter = Yaml.createYamlPrinter(new FileWriter(file));
            //yamlPrinter.print(data);
            new ConfigPrinter(new FileWriter(file)).print(data);
        }
        catch (IOException e)
        {
            return;
        }
    }

    public YamlMapping getMapping(String... paths)
    {
        if(paths == null)
        {
            return null;
        }
        else if(paths.length == 1 && paths[0].isEmpty())
        {
            return data;
        }
        else
        {
            String[] keys = String.join(".", paths).split("\\.");

            YamlMapping mapping = data;

            for (String key : keys)
            {
                if (mapping != null)
                {
                    mapping = mapping.yamlMapping(key);
                }
                else
                {
                    return null;
                }
            }

            return mapping;
        }
    }

    public YamlSequence getSequence(String... paths)
    {
        String[] keys = String.join(".", paths).split("\\.");
        String parentPath = String.join(".", Arrays.copyOfRange(keys, 0, keys.length - 1));
        String childPath = keys[keys.length - 1];

        YamlMapping mapping = getMapping(parentPath);
        if(mapping == null || mapping.yamlSequence(childPath) == null)
        {
            return null;
        }
        else
        {
            return mapping.yamlSequence(childPath);
        }
    }

    public String getString(String... paths)
    {
        return getStringOrFallback(null, paths);
    }

    public String getStringOrFallback(String fallback, String... paths)
    {
        String[] keys = String.join(".", paths).split("\\.");
        String parentPath = String.join(".", Arrays.copyOfRange(keys, 0, keys.length - 1));
        String childPath = keys[keys.length - 1];

        YamlMapping mapping = getMapping(parentPath);

        if(mapping == null || mapping.string(childPath) == null)
        {
            return fallback;
        }
        else
        {
            return mapping.string(childPath);
        }
    }

    public Boolean getBoolean(String... paths)
    {
        return getBooleanOrFallback(null, paths);
    }

    public Boolean getBooleanOrFallback(Boolean fallback, String... paths)
    {
        String value = getString(paths);
        return value != null ? Boolean.parseBoolean(value) : fallback;
    }

    public Double getDouble(String... paths)
    {
        return getDoubleOrFallback(null, paths);
    }

    public Double getDoubleOrFallback(Double fallback, String... paths)
    {
        String value = getString(paths);
        return value != null ? Double.parseDouble(value) : fallback;
    }

    public Integer getInt(String... paths)
    {
        return getIntOrFallback(null, paths);
    }

    public Integer getIntOrFallback(Integer fallback, String... paths)
    {
        String value = getString(paths);
        return value != null ? Integer.parseInt(value) : fallback;
    }

    public ItemStack getItemStack(YamlMapping mapping)
    {
        if(mapping == null)
        {
            return null;
        }

        String itemName = mapping.string("item");
        int itemAmount = Math.max(mapping.integer("amount"), 1);
        String enchantmentName = mapping.string("enchantment");
        int enchantmentLevel = Math.max(mapping.integer("enchantment-level"), 1);

        if (itemName == null || Material.getMaterial(itemName) == null)
        {
            return null;
        }

        ItemStack itemStack = new ItemStack(Material.valueOf(itemName), itemAmount);

        if (enchantmentName != null)
        {
            EnchantmentWrapper enchantmentWrapper = new EnchantmentWrapper(enchantmentName.toLowerCase());
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

        return itemStack;
    }

    public MerchantRecipe getRecipe(YamlMapping mapping)
    {
        if(mapping == null)
        {
            return null;
        }

        ItemStack input1 = getItemStack(mapping.yamlMapping("input1"));
        ItemStack input2 = getItemStack(mapping.yamlMapping("input2"));
        ItemStack output = getItemStack(mapping.yamlMapping("output"));

        if (output == null || (input1 == null && input2 == null)) return null;

        float priceMultiplier = mapping.floatNumber("price-multiplier") <= 0 ? 0.05f : mapping.floatNumber("price-multiplier");
        int maxUses = mapping.integer("price-multiplier") <= 0 ? 12 : mapping.integer("price-multiplier");
        int villagerExperience = mapping.integer("villager-xp") <= 0 ? 2 : mapping.integer("villager-xp");

        MerchantRecipe recipe = new MerchantRecipe(output, 0, maxUses, true, villagerExperience, priceMultiplier);

        if (input1 != null) recipe.addIngredient(input1);
        if (input2 != null) recipe.addIngredient(input2);

        return recipe;
    }
}
