package merlin.plugin.money.views;

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
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.logging.Level;

public class BankerView  implements Listener {
    private final Inventory inventory;
    private final Money plugin;

    public BankerView(final Money plugin) {
        inventory = Bukkit.createInventory(null, 9, "Shop");
        this.plugin = plugin;
        initializeItems();
    }

    public void initializeItems() {
        inventory.setItem(0, createItem(Material.OAK_SIGN, "INFO", "You can withdraw or deposit"));
        inventory.setItem(1, createItem(Material.EMERALD, "Withdraw", "§aWithdraw."));
        inventory.setItem(2, createItem(Material.CHEST, "Deposit", "§aDeposit"));
    }

    public void openBankerView(final HumanEntity entity) {
        entity.openInventory(inventory);
    }

    private ItemStack createItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        if(meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));

            item.setItemMeta(meta);
        }

        return item;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent clickEvent) {
        if(!clickEvent.getInventory().equals(inventory)) return;

        clickEvent.setCancelled(true);

        final ItemStack clickedItem = clickEvent.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().isAir()) return;

        final Player player = (Player)clickEvent.getWhoClicked();

        ItemMeta meta = clickedItem.getItemMeta();

        if(meta == null) return;

        if(meta.getDisplayName().equalsIgnoreCase("deposit")) {
            depositCoins(player);
        } else if(meta.getDisplayName().equalsIgnoreCase("withdraw")) {
            withdraw(player);
        }

        player.closeInventory();
    }

    private void withdraw(final Player player) {
        try {
            final PlayerData playerData = plugin.getPlayerData(player);
            final float coinsToWithdraw = 500f;

            if(playerData.withdraw(coinsToWithdraw)) {
                player.sendMessage("You have withdrawn Coins");
            }
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, "Something went wrong withdrawing coins. Details: " + exception.getMessage());
        }
    }

    private void depositCoins(final Player player) {
        try {
            final PlayerData playerData = plugin.getPlayerData(player);
            final Float coinsToDeposit = playerData.getCoinsWallet();
            if(playerData.depositCoins(coinsToDeposit)) {
                player.sendMessage("You deposit " + ChatColor.GOLD + coinsToDeposit + " Coins");
            };
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, "Something went wrong depositing coins. Details: " + exception.getMessage());
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent dragEvent) {
        if(dragEvent.getInventory().equals(inventory)) {
            dragEvent.setCancelled(true);
        }
    }
}
