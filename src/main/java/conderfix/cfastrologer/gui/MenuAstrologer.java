package conderfix.cfastrologer.gui;

import conderfix.cfastrologer.AstrologerPlugin;
import conderfix.cfastrologer.utils.ConfigUtil;
import conderfix.cfastrologer.utils.HexUtil;
import conderfix.cfastrologer.utils.TypeRarities;
import jdk.jfr.internal.tool.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class MenuAstrologer implements Listener {

    private static final Map<UUID, Inventory> viewing = new HashMap<>();

    private static final Inventory inventory = Bukkit.createInventory(null,
            ConfigUtil.getInt("inventory.size"),
            HexUtil.translate(ConfigUtil.getString("inventory.title")));

    private final NamespacedKey namespacedKeyItem = new NamespacedKey(AstrologerPlugin.inst, "astrologer-item");

    private ItemStack createItemMainInMenu(Integer amount) {
        ItemStack itemStack = new ItemStack(Material.valueOf(ConfigUtil.getString("item-in-menu.material")), amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ConfigUtil.getString("item-in-menu.name").replace("{size}", String.valueOf(amount)));
        List<String> lore = AstrologerPlugin.inst.getConfig().getStringList("item-in-menu.lore");
        List<String> translatedLore = new ArrayList<>();
        for (String line : lore) {
            String translatedLine = HexUtil.translate(line);
            translatedLore.add(translatedLine);
        }
        final boolean isGlow = AstrologerPlugin.inst.getConfig().getBoolean("item-in-menu.glow");
        if (isGlow) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemMeta.setLore(translatedLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void setMainItem(final Inventory inventory, Integer position) {
        String item1Value = ConfigUtil.getString("positions.item-"+position);
        String[] parts = item1Value.split(";");

        int slot = Integer.parseInt(parts[0]);
        int amountItem = Integer.parseInt(parts[1]);
        inventory.setItem(slot, createItemMainInMenu(amountItem));
    }

    public MenuAstrologer() {
        // decor
        ItemStack itemStack = new ItemStack(Material.valueOf(ConfigUtil.getString("inventory.decor-material")), 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(HexUtil.translate(ConfigUtil.getString("inventory.decor-name")));
        itemStack.setItemMeta(itemMeta);
        List<Integer> slots = AstrologerPlugin.inst.getConfig().getIntegerList("positions-decor");
        for (int i : slots) {
            inventory.setItem(i, itemStack);
        }
        // Main items
        setMainItem(inventory, 1);
        setMainItem(inventory, 2);
        setMainItem(inventory, 3);
    }

    private int countItem(Player player, String itemName, Integer amount) {
        int count = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (ItemStack item : contents) {
            if (item != null && item.getType().name().equalsIgnoreCase(itemName)) {
                if (item.getItemMeta().getPersistentDataContainer().has(namespacedKeyItem, PersistentDataType.STRING)) {
                    count += item.getAmount();
                    if (count >= amount) {
                        item.setAmount(count - amount);
                        return amount;
                    } else {
                        amount -= count;
                        count = 0;
                    }
                }
            }
        }

        return count;
    }

    private void giveItem(final Player player, final Integer amount, TypeRarities typeRarities) {
        int itemAmount = countItem(player, ConfigUtil.getString("exchange-item.material"), amount);
        if (itemAmount < amount) {
            player.sendMessage(ConfigUtil.getString("messages.no-buy"));
            player.playSound(player.getLocation(), Sound.valueOf(ConfigUtil.getString("sounds.error")), 1, 1);
        } else {
            AstrologerPlugin.fillInventoryWithRandomItem(player.getInventory(), typeRarities.toString());
            player.sendMessage(ConfigUtil.getString("messages.done"));
            player.playSound(player.getLocation(), Sound.valueOf(ConfigUtil.getString("sounds.done")), 1, 1);
        }

    }
    public static void open(Player player) {
        player.closeInventory();
        viewing.put(player.getUniqueId(),inventory);
        player.openInventory(inventory);
    }

    @EventHandler
    private void on(InventoryCloseEvent event) {
        viewing.remove(event.getPlayer());
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        final Player p = (Player) event.getWhoClicked();

        if (viewing.get(p.getUniqueId()) != event.getInventory()) return;
            event.setCancelled(true);

            final int slot = event.getSlot();
            if (slot == ConfigUtil.getSlot(1)) {
                giveItem(p, ConfigUtil.getAmount(1), TypeRarities.SHIT);
            }
            if (slot == ConfigUtil.getSlot(2)) {
                giveItem(p, ConfigUtil.getAmount(2), TypeRarities.FINE);
            }
            if (slot == ConfigUtil.getSlot(3)) {
                giveItem(p, ConfigUtil.getAmount(3), TypeRarities.VERYWELL);
            }


    }

}
