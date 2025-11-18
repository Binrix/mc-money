package merlin.plugin.money.eventlisteners;

import merlin.plugin.money.Money;
import merlin.plugin.money.player.PlayerData;
import merlin.plugin.money.player.Profession;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

public class MiningEventsListener implements Listener {
    private final Money plugin;
    private final Map<Material, Float> blocks;
    private final Float baseEfficiency;
    private final Float wrongJobPenalty;

    public MiningEventsListener(Money plugin, Map<Material, Float> blocks, final Float wrongJobPenalty, final Float baseEfficiency) {
        this.plugin = plugin;
        this.blocks = blocks;
        this.baseEfficiency = baseEfficiency;
        this.wrongJobPenalty = wrongJobPenalty;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player breakEventPlayer = event.getPlayer();
        Block block = event.getBlock();

        PlayerData player = plugin.getPlayerData(breakEventPlayer);

        if(blocks.containsKey(block.getType())) {
            float coins = blocks.get(block.getType()) * (player.hasProfession(Profession.MINER) ? baseEfficiency : wrongJobPenalty);
            player.addCoins(coins);
            plugin.updatePlayerData(breakEventPlayer, player);
        }
    }
}
