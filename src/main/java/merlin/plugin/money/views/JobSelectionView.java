package merlin.plugin.money.views;

import merlin.plugin.money.Money;
import merlin.plugin.money.SetMethodReturn;
import merlin.plugin.money.SetResult;
import merlin.plugin.money.player.PlayerData;
import merlin.plugin.money.player.Profession;
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

public class JobSelectionView implements Listener {
    private final Inventory inventory;
    private final Money money;

    public JobSelectionView(final Money money) {
        inventory = Bukkit.createInventory(null, 9, "Shop");
        this.money = money;
        initializeItems();
    }

    public void initializeItems() {
        inventory.setItem(0, createItem(Material.OAK_SIGN, "INFO", "If you don't have a profession the fee will be 500 Coins.", "Otherwise you have to pay 10'000 Coins to change your profession."));
        inventory.setItem(1, createItem(Material.NETHERITE_PICKAXE, Profession.MINER.name(), "§aEarn money by farming ores."));
        inventory.setItem(2, createItem(Material.NETHERITE_HOE, Profession.FARMER.name(), "§aEarn money by harvesting."));
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

    public void openJobSelectionView(final HumanEntity entity) {
        entity.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent clickEvent) {
        if(!clickEvent.getInventory().equals(inventory)) return;

        clickEvent.setCancelled(true);

        final ItemStack clickedItem = clickEvent.getCurrentItem();

        if(clickedItem == null || clickedItem.getType().isAir()) return;

        final Player player = (Player)clickEvent.getWhoClicked();

        changeProfession(player, clickedItem.getItemMeta());

        player.closeInventory();
    }

    private void changeProfession(Player player, ItemMeta meta) {
        try {
            if(meta != null) {
                Profession profession = Profession.valueOf(meta.getDisplayName().toUpperCase());
                PlayerData playerData = money.getPlayerData(player);

                SetMethodReturn result = playerData.setProfession(profession);
                player.sendMessage(result.message);

                if(result.result == SetResult.SUCCESS) {
                    ItemStack jobLabel = new ItemStack(Material.NAME_TAG, 1);
                    ItemMeta jobLabelMeta = jobLabel.getItemMeta();

                    if(jobLabelMeta != null) {
                        jobLabelMeta.setDisplayName("Your profession: " + profession);
                        jobLabel.setItemMeta(jobLabelMeta);
                    }

                    Inventory playerInventory = player.getInventory();
                    playerInventory.addItem(jobLabel);
                }
            }
        } catch (Exception exception) {
            player.sendMessage(ChatColor.DARK_RED + "Error");
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent dragEvent) {
        if(dragEvent.getInventory().equals(inventory)) {
            dragEvent.setCancelled(true);
        }
    }
}
