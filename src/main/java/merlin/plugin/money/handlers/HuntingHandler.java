package merlin.plugin.money.handlers;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class HuntingHandler {
    private final Map<EntityType, Float> entities;
    private final Float wrongJobPenalty;
    private final Float baseEfficiency;

    public HuntingHandler(final Map<EntityType, Float> entities, final Float wrongJobPenalty, final Float baseEfficiency) {
        this.entities = entities;
        this.wrongJobPenalty = wrongJobPenalty;
        this.baseEfficiency = baseEfficiency;
    }

    public Float handleHunting(final boolean hasPlayerProfession, final LivingEntity entity) {
        if(entities.containsKey(entity.getType())) {
            return entities.get(entity.getType()) * (hasPlayerProfession ? baseEfficiency : wrongJobPenalty);
        }

        return 0f;
    }
}
