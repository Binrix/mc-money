package merlin.plugin.money.handlers;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Map;

public class MiningHandler {
    private final Map<Material, Float> BlockToCoins;
    private final Float wrongJobPenalty;
    private final Float baseEfficiency;


    public MiningHandler(final Map<Material, Float> BlockToCoins, final Float wrongJobPenalty, final Float baseEfficiency) {
        this.BlockToCoins = BlockToCoins;
        this.wrongJobPenalty = wrongJobPenalty;
        this.baseEfficiency = baseEfficiency;
    }

    public Float handleMining(final boolean hasPlayerProfession, final Block block) {
        if(BlockToCoins.containsKey(block.getType())) {
            return BlockToCoins.get(block.getType()) * (hasPlayerProfession ? baseEfficiency : wrongJobPenalty);
        }

        return 0f;
    }
}
