package conderfix.cfastrologer;

import conderfix.cfastrologer.commands.AstrologerCommmand;
import conderfix.cfastrologer.gui.MenuAstrologer;
import conderfix.cfastrologer.utils.Storage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class AstrologerPlugin extends JavaPlugin {

    public static Plugin inst;
    public static Storage items;

    @Override
    public void onEnable() {
        inst = this;
        items = new Storage("items.yml");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new MenuAstrologer(), this);
        getCommand("astrologer").setExecutor(new AstrologerCommmand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void addItem(String id, ItemStack itemStack, int chance, String type) {
        type = type.toUpperCase();
        ConfigurationSection itemsSection = items.getConfig().getConfigurationSection("items_" + type);
        if (itemsSection == null) {
            items.getConfig().createSection("items_" + type);
            addItem(id, itemStack, chance, type);
        } else {
            itemsSection = itemsSection.createSection(String.valueOf(id));
            itemsSection.set("itemStack", itemStack);
            items.save();
        }
    }

    public static void fillInventoryWithRandomItem(Inventory inventory, String type) {
        type = type.toUpperCase();
        ConfigurationSection itemsSection = items.getConfig().getConfigurationSection("items_" + type);

        if (itemsSection != null) {
            List<ItemStack> availableItems = new ArrayList<>();

            for (String itemId : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemId);
                ItemStack item = itemSection.getItemStack("itemStack");

                availableItems.add(item);
            }

                if (!availableItems.isEmpty()) {
                    int randomIndex = (new Random()).nextInt(availableItems.size());
                    inventory.addItem(availableItems.get(randomIndex));
                    availableItems.remove(randomIndex);
                }
        } else {
            items.getConfig().createSection("items_" + type);
        }
    }
}
