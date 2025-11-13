package merlin.plugin.money.handlers;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Map;

public class MiningHandler {
    private final Map<Material, Float> blocks;
    private final Float wrongJobPenalty;
    private final Float baseEfficiency;


    public MiningHandler(final Map<Material, Float> blocks, final Float wrongJobPenalty, final Float baseEfficiency) {
        this.blocks = blocks;
        this.wrongJobPenalty = wrongJobPenalty;
        this.baseEfficiency = baseEfficiency;
    }

    public Float handleMining(final boolean hasPlayerProfession, final Block block) {
        if(blocks.containsKey(block.getType())) {
            return blocks.get(block.getType()) * (hasPlayerProfession ? baseEfficiency : wrongJobPenalty);
        }

        return 0f;
    }
}
