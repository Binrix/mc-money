package merlin.plugin.money;

import merlin.plugin.money.handlers.MiningHandler;
import merlin.plugin.money.npcs.NPCType;
import merlin.plugin.money.player.Profession;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EventListeners implements Listener {
    private final Money plugin;
    private final MiningHandler miningHandler;

    public EventListeners(Money plugin) {
        this.plugin = plugin;
        this.miningHandler = new MiningHandler(plugin.getBlocks(), plugin.getMoneyConfiguration().wrongJobPenalty, plugin.getMoneyConfiguration().baseEfficiency);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!plugin.isPlayerInList(player.getUniqueId())) {
            plugin.addPlayerCoins(player, 0f);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent interactAtEntityEvent) {
        Player player = interactAtEntityEvent.getPlayer();
        Entity clickedEntity = interactAtEntityEvent.getRightClicked();

        PersistentDataContainer data = clickedEntity.getPersistentDataContainer();

        NamespacedKey key = new NamespacedKey(plugin, "is_npc");

        if(data.has(key, PersistentDataType.STRING)) {
            NPCType type = NPCType.valueOf(data.get(key, PersistentDataType.STRING).toUpperCase());
            if(type.equals(NPCType.PROFESSION)) {
                plugin.getJobSelectionView().openJobSelectionView(player);
            } else if(type.equals(NPCType.BANKER)) {
                plugin.getBankerView().openBankerView(player);
            }

            interactAtEntityEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent playerLeave) {
        plugin.saveConfig();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent deathEvent) {
        Player player = deathEvent.getEntity();
        plugin.getPlayerData(player).loseCoinsInWallet();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent breakEvent) {
        Player player = breakEvent.getPlayer();
        Block block = breakEvent.getBlock();

        Float coins = miningHandler.handleMining(plugin.hasPlayerProfession(player, Profession.MINER), block);
        this.plugin.addPlayerCoins(player, coins);
    }
}
