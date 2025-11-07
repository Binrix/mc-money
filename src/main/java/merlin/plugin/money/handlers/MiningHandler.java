package merlin.plugin.money.handlers;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Map;

public class MiningHandler {
    private final Map<Material, Float> BlockToCoins;

    public MiningHandler(final Map<Material, Float> BlockToCoins) {
        this.BlockToCoins = BlockToCoins;
    }

    public Float handleMining(final Float professionEfficiency, final Block block) {
        if(BlockToCoins.containsKey(block.getType())) {
            return BlockToCoins.get(block.getType()) * professionEfficiency;
        }

        return 0f;
    }
}
