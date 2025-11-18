package merlin.plugin.money.views;

import merlin.plugin.money.Helpers;
import merlin.plugin.money.Money;
import merlin.plugin.money.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SmithView implements Listener  {
    private final Inventory inventory;
    private final Money plugin;

    public SmithView(final Money plugin) {
        inventory = Bukkit.createInventory(null, 9, "Repair");
        this.plugin = plugin;
    }

    public void openSmithView(final HumanEntity entity) {
        PlayerInventory playersInventory = entity.getInventory();

        for(int i = 0; i < 9; i++) {
            ItemStack item = playersInventory.getItem(i);
            if(item == null || item.getType().isAir()) continue;

            final Material itemMaterial = item.getType();

            ItemMeta meta = item.getItemMeta();

            ItemStack itemForSmith = new ItemStack(itemMaterial);
            ItemMeta itemForSmithMeta = itemForSmith.getItemMeta();

            if(meta instanceof Damageable) {
                Damageable damageMeta = ((Damageable) meta);
                if(!damageMeta.hasDamage()) continue;

                float totalCost = getCostsForRepair(item, damageMeta);

                List<String> loreEntries = new ArrayList<>();
                loreEntries.add("Repairing costs " + ChatColor.GOLD + Helpers.formatCoins(totalCost) + " Coins.");
                itemForSmithMeta.setLore(loreEntries);
                itemForSmith.setItemMeta(itemForSmithMeta);
            }

            inventory.setItem(i, itemForSmith);
        }

        entity.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent clickEvent) {
        if(!clickEvent.getInventory().equals(inventory)) return;

        clickEvent.setCancelled(true);

        final ItemStack clickedItem = clickEvent.getCurrentItem();
        final HumanEntity player = clickEvent.getWhoClicked();

        if(clickedItem == null) return;

        int slot = clickEvent.getSlot();

        final ItemStack item = player.getInventory().getItem(slot);

        if(item == null) return;

        if(item.getItemMeta() instanceof Damageable) {
            float costsForRepair =  getCostsForRepair(item, ((Damageable) item.getItemMeta()));

            PlayerData playerData = plugin.getPlayerData((Player) player);
            if(playerData.removeCoins(costsForRepair)) {
                player.sendMessage("Item was repaired for " + ChatColor.GOLD + Helpers.formatCoins(costsForRepair) + " Coins.");
                Damageable itemDamageMeta = (Damageable) item.getItemMeta();
                itemDamageMeta.setDamage(0);
                item.setItemMeta(itemDamageMeta);
            } else {
                player.sendMessage("You don't have enough Coins to repair your item.");
            };
        }

        player.closeInventory();
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent dragEvent) {
        if(dragEvent.getInventory().equals(inventory)) {
            dragEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent closeEvent) {
        if(!closeEvent.getInventory().equals(inventory)) return;

        for(ItemStack item : inventory.getContents()) {
            inventory.remove(item);
        }
    }

    private float getCostsForRepair(ItemStack item, Damageable damageable) {
        float calcFactorBasis = 1f;
        float calcFactor = calcFactorBasis + ((float)item.getType().getMaxDurability() / 1000f);

        return  calcFactor * damageable.getDamage();
    }

    private boolean isItemPickaxeType(Material type) {
        switch(type) {
            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case GOLDEN_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
                return true;
            default:
                return false;
        }
    }
}
