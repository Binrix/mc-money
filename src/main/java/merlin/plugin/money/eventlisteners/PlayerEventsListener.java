package merlin.plugin.money.eventlisteners;

import merlin.plugin.money.Money;
import merlin.plugin.money.npcs.NPCType;
import merlin.plugin.money.player.PlayerData;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerEventsListener implements Listener {
    private final Money plugin;

    public PlayerEventsListener(Money plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!plugin.isPlayerInList(player.getUniqueId())) {
            plugin.updatePlayerData(player, new PlayerData());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        plugin.saveConfig();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        plugin.getPlayerData(player).loseCoinsInWallet();
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();

        PersistentDataContainer data = clickedEntity.getPersistentDataContainer();

        NamespacedKey key = new NamespacedKey(plugin, "is_npc");

        if(data.has(key, PersistentDataType.STRING)) {
            NPCType type = NPCType.valueOf(data.get(key, PersistentDataType.STRING).toUpperCase());
            if(type.equals(NPCType.PROFESSION)) {
                plugin.getJobSelectionView().openJobSelectionView(player);
            } else if(type.equals(NPCType.BANKER)) {
                plugin.getBankerView().openBankerView(player);
            } else if(type.equals(NPCType.SMITH)) {
                plugin.getSmithView().openSmithView(player);
            }

            event.setCancelled(true);
        }
    }
}
